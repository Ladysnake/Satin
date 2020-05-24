/*
 * Satin
 * Copyright (C) 2019-2020 Ladysnake
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
package ladysnake.satin.config;

import ladysnake.satin.Satin;

import java.io.IOException;

public class SatinFeatures implements Versionable {
    private static final int CURRENT_VERSION = 1;
    private static SatinFeatures instance;

    public static synchronized SatinFeatures getInstance() {
        if (instance == null) {
            try {
                instance = ConfigLoader.load("satin.json", SatinFeatures.class, SatinFeatures::new);
            } catch (IOException e) {
                Satin.LOGGER.error("[Satin] Failed to load features from config!", e);
                instance = new SatinFeatures();
            }
        }
        return instance;
    }

    private int version = CURRENT_VERSION;
    public final OptionalFeature readableDepthFramebuffers = new OptionalFeature().enableByDefault();

    @Override
    public boolean isUpToDate() {
        return this.version == CURRENT_VERSION;
    }

    @Override
    public void update() {
        this.version = CURRENT_VERSION;
    }

    private SatinFeatures() { super(); }
}
