/*
 * Satin
 * Copyright (C) 2019-2024 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package org.ladysnake.satin.impl;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.profiler.Profiler;
import org.apiguardian.api.API;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles creating framebuffers with custom pixel formats.
 * <p>
 * For post-chains, this (along with some mixins) also handles a lot of the logic for implementing
 * targets with custom formats. The data is shuffled around a lot, so here's a guide:
 * <ul>
 *     <li>Resource Loading starts.</li>
 *     <ul>
 *     <li>{@link net.minecraft.client.gl.ShaderLoader#prepare(ResourceManager, Profiler)} starts loading post-chains.</li>
 *     <ul>
 *         <li>{@link net.minecraft.client.gl.ShaderLoader#loadPostEffect(Identifier, Resource, ImmutableMap.Builder)}
 *         is called for each post-chain.</li>
 *         <li>In loadPostEffect, we parse the custom target formats and put them in LOADING_TARGET_FORMATS_BY_POSTCHAIN_ID
 *         ({@link #parseTargetFormatMap(Identifier, JsonElement)})</li>
 *     </ul>
 *     <li>The second phase of resource loading happens, and the prepared post-chains are put into a newly-created {@link net.minecraft.client.gl.ShaderLoader.Cache}.</li>
 *     <li>In the cache's constructor, we copy from LOADING_TARGET_FORMATS_BY_POSTCHAIN into a field injected into the cache. ({@link #getPreparedTargetFormatMaps()})</li>
 *     </ul>
 *     <li>A post-chain processor ({@link net.minecraft.client.gl.PostEffectProcessor}) is created.</li>
 *     <ul>
 *         <li>The ShaderLoader cache's {@link net.minecraft.client.gl.ShaderLoader.Cache#loadProcessor(Identifier, Set)} is run.</li>
 *         <li>In that method we find the post-chain's target format map (stored in the field we injected into the cache) and put it into TARGET_FORMATS_FOR_PROCESSOR_INIT. ({@link #prepareFormatsForProcessor(Map)})</li>
 *         <li>In the post-chain processor's constructor, we take the map out of TARGET_FORMATS_FOR_PROCESSOR_INIT and put it into an injected field on the processor. ({@link #getFormatsForProcessor()})</li>
 *     </ul>
 *     <li>The post-chain processor is called for rendering (in one frame) ( {@link net.minecraft.client.gl.PostEffectProcessor#render(FrameGraphBuilder, int, int, PostEffectProcessor.FramebufferSet)})</li>
 *     <ul>
 *         <li>The processor creates new framebuffer factories for each of its targets.</li>
 *         <li>When these factories are created, we look up the custom texture format for the target's ID and associate it with the factory in FORMATS_BY_FACTORY. ({@link #setCustomFormatForFactory(SimpleFramebufferFactory, TextureFormat)})</li>
 *         <li>When the factory has its {@link SimpleFramebufferFactory#create()} method run, we remove the factory's custom texture format map entry and place it in FORMAT. ({@link #prepareCustomFormatForFramebufferInit(SimpleFramebufferFactory)})</li>
 *         <li>In the framebuffer's constructor, we then take the format from FORMAT and use it. ({@link #getCustomFormatForFramebufferInit()})</li>
 *     </ul>
 * </ul>
 *
 */
public class CustomFormatFramebuffers {
    public static final String FORMAT_KEY = "satin:format";
    private static final ThreadLocal<TextureFormat> FORMAT = new ThreadLocal<>();
    private static final ThreadLocal<Map<Identifier, TextureFormat>> TARGET_FORMATS_FOR_PROCESSOR_INIT = new ThreadLocal<>();
    // identity hash-map because SimpleFramebufferFactories are records
    private static final Map<SimpleFramebufferFactory, TextureFormat> FORMATS_BY_FACTORY = new IdentityHashMap<>();
    private static final Map<Identifier, Map<Identifier, TextureFormat>> LOADING_TARGET_FORMATS_BY_POSTCHAIN_ID = new HashMap<>();

    /**
     * Experimental method to create a new framebuffer from code, please open an issue if you actually use it
     *
     * <p>Refer to {@link SimpleFramebuffer} for the list of parameters
     */
    @API(status = API.Status.EXPERIMENTAL)
    public static Framebuffer create(int width, int height, boolean useDepth, TextureFormat format) {
        try {
            FORMAT.set(format);
            return new SimpleFramebuffer(width, height, useDepth);
        } finally {
            FORMAT.remove();
        }
    }

    public static @Nullable CustomFormatFramebuffers.TextureFormat getCustomFormatForFramebufferInit() {
        return FORMAT.get();
    }

    public static void clearCustomFormatForFramebufferInit() {
        FORMAT.remove();
    }

    public static void setCustomFormatForFactory(SimpleFramebufferFactory factory, TextureFormat format) {
        FORMATS_BY_FACTORY.put(factory, format);
    }

    public static void prepareCustomFormatForFramebufferInit(SimpleFramebufferFactory factory) {
        FORMAT.set(FORMATS_BY_FACTORY.remove(factory));
    }

    public static void parseTargetFormatMap(Identifier postChainId, JsonElement jsonElement) {
        LOADING_TARGET_FORMATS_BY_POSTCHAIN_ID.put(postChainId, makeTargetFormatMap(jsonElement));
    }

    public static Map<Identifier, Map<Identifier, TextureFormat>> getPreparedTargetFormatMaps() {
        var res = Map.copyOf(LOADING_TARGET_FORMATS_BY_POSTCHAIN_ID);
        LOADING_TARGET_FORMATS_BY_POSTCHAIN_ID.clear();
        return res;
    }

    private static Map<Identifier, TextureFormat> makeTargetFormatMap(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            var targets = jsonElement.getAsJsonObject().getAsJsonObject("targets");
            if (targets != null && !targets.isEmpty()) {
                return targets.entrySet().stream()
                        .filter(e -> e.getValue().isJsonObject() && e.getValue().getAsJsonObject().has(FORMAT_KEY))
                        .collect(Collectors.toMap(
                                e -> Identifier.of(e.getKey()),
                                e -> TextureFormat.CODEC.decode(JsonOps.INSTANCE, e.getValue().getAsJsonObject().get(FORMAT_KEY)).getOrThrow(JsonSyntaxException::new).getFirst()));
            }
        }
        return Map.of();
    }

    public static void prepareFormatsForProcessor(Map<Identifier, TextureFormat> targetFormatMap) {
        TARGET_FORMATS_FOR_PROCESSOR_INIT.set(targetFormatMap);
    }

    public static Map<Identifier, TextureFormat> getFormatsForProcessor() {
        var res = TARGET_FORMATS_FOR_PROCESSOR_INIT.get();
        TARGET_FORMATS_FOR_PROCESSOR_INIT.remove();
        return res;
    }

    public enum TextureFormat implements StringIdentifiable {
        RGBA8(GL11.GL_RGBA8),
        RGBA16(GL11.GL_RGBA16),
        RGBA16F(GL30.GL_RGBA16F),
        RGBA32F(GL30.GL_RGBA32F),
        ;

        public static final Codec<TextureFormat> CODEC = StringIdentifiable.createCodec(TextureFormat::values);

        public final int value;

        /**
         * Decodes a pixel format specified by name into its corresponding OpenGL enum.
         * <strong>This only supports the formats needed for {@link #prepareCustomFormat(String)}.</strong>
         * @param formatString  the format name
         * @return              the format enum value
         * @see #prepareCustomFormat(String)
         */
        public static TextureFormat decode(String formatString) {
            return switch (formatString) {
                // unsigned normalised 8 bits
                case "RGBA8" -> RGBA8;
                // unsigned normalised 16 bits
                case "RGBA16" -> RGBA16;
                // float 16 bits
                case "RGBA16F" -> RGBA16F;
                // float 32 bits
                case "RGBA32F" -> RGBA32F;
                // we don't support un-normalised signed or unsigned integral formats here
                // because it's not valid to blit between them and normalised/float formats
                default -> throw new IllegalArgumentException("Unsupported texture format "+formatString);
            };
        }

        TextureFormat(int value) {
            this.value = value;
        }

        @Override
        public String asString() {
            return this.name();
        }
    }
}
