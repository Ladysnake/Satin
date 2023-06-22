## **The Ladysnake maven is moving!**

**As Jfrog is ending their free service for OSS projects, we have to move the maven repository before the 1st of July 2023.
We will be updating this page as soon as the new maven is operational - you will then have to update your buildscripts with the new URL.**


# Satin

[![](https://jitpack.io/v/Ladysnake/Satin.svg)](https://jitpack.io/#Ladysnake/Satin)

A lightweight Fabric library for OpenGL shader usage.

---

## Adding Satin to your project

You can add the library by inserting the following in your `build.gradle` (Requires Loom 0.2.4+):

**Note: since MC 1.17 builds, the Satin dependency must be lowercase.**

```gradle
repositories {
        maven {
        name = 'Ladysnake Mods'
        url = 'https://ladysnake.jfrog.io/artifactory/mods'
        content {
            includeGroup 'io.github.ladysnake'
            includeGroupByRegex 'io\\.github\\.onyxstudios.*'
        }
    }
}

dependencies {
    modImplementation "io.github.ladysnake:satin:${satin_version}"
    // Include Satin as a Jar-in-Jar dependency (optional)
    include "io.github.ladysnake:satin:${satin_version}"
}
```

You can then add the library version to your `gradle.properties`file:

```properties
# Satin library
satin_version = 1.x.y
```

You can find the current version of Satin in the [releases](https://github.com/Ladysnake/Satin/releases) tab of the repository on Github.

*If you wish your mod to be 100% standalone, you also need to include the `fabric-api-base` and `fabric-resource-loader-v0` modules from Fabric API in your mod jar.*

## Updating from 1.16 to 1.17

1. Update your Satin dependency
2. If you only use post process shaders, you are mostly done.
   You should already be able to play, but it's preferable you also update your shaders to Core Profile (go to step 4).
3. If you use custom shaders for your entities, follow these steps:
    1. Move the relevant shader files from `assets/shaders/program` to `assets/shaders/core`
    2. Replace references to `ManagedShaderProgram` with references to `ManagedCoreShader`
    3. Replace calls to `ShaderEffectManager#manageProgram` with calls to `ShaderEffectManager#manageCoreShader`
4. Update your shaders to Core Profile: https://docs.substance3d.com/sddoc/switching-your-shaders-to-opengl-core-profile-172819178.html
    - You can refer to vanilla shaders for examples

## Using Satin

### Changes to Vanilla

Simply having Satin installed alters the game in a few ways, mainly related to ShaderEffects.

- **Uniform fix**: Using a vector of integers as a uniform crashes the game in Vanilla because of a bad
copy paste. Satin redirects a call to upload the right buffer.
- **Shader locations**: Satin patches shader processors to accept a resource domain in the specification
of a program name and of a fragment/vertex shader file.
- **Custom Target Formats**: Satin allows shader targets to specify different formats to `RGBA8`. 
Supported formats are `RGBA8`, `RGBA16`, `RGBA16F`, and `RGBA32F`.
- ~~**Readable depth**~~: Satin offered access to a Framebuffer's depth texture before it was cool (superseded in 1.16).

Satin **does not** set the shader in `GameRenderer`, except if a mod registers a `PickEntityShaderCallback`.

### Shader Management

Satin's main feature is the Shader Effect management facility. 

`ShaderEffect` is a Minecraft class implementing data driven post processing shaders, with a few caveats.
First, those shader effects are initialized immediately at construction, but they must be initialized after the game has
finished loading to avoid gl errors. Then, they must be updated each time the game's resolution changes.
Finally, they do not have any way of setting uniforms from external code.

Satin can manage a shader effect for you, giving you a `ManagedShaderEffect` object.
This shader effect is lazily initialized - although it can be initialized manually at any time.
Initialized shader effects will be automatically reloaded each time the game's resolution changes,
and during resource reloading. It also provides a number of access methods to set uniforms dynamically.

Here is the whole java code for a mod that applies a basic shader to the game:

```java
public class GreyscaleMinecraft implements ClientModInitializer {
    private static final ManagedShaderEffect GREYSCALE_SHADER = ShaderEffectManager.getInstance()
    		.manage(new Identifier("shaderexample", "shaders/post/greyscale.json"));
    private static boolean enabled = true;  // can be disabled whenever you want
    
    @Override
    public void onInitializeClient() {
        // the render method of the shader will be called after the game
        // has drawn the world on the main framebuffer, when it renders
        // vanilla post process shaders
    	ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
    	    if (enabled) {
                GREYSCALE_SHADER.render(tickDelta);
            }
    	});
    }
}
```

For examples of json shader definitions, look for the `assets/minecraft/shaders` folder in the minecraft source (description of those shaders can be found on the [Minecraft Wiki](https://minecraft.gamepedia.com/Shaders)). There is also a real application of this library in [Requiem](https://github.com/Ladysnake/Requiem/blob/d95c4f5c55/src/main/java/ladysnake/requiem/client/RequiemFx.java).

### RenderLayer Utilities
The `ManagedFramebuffer` and `ManagedShaderProgram` classes have methods to obtain clones of existing `RenderLayer` objects,
with a custom `Target`. This target causes draw calls to happen on the `ManagedFramebuffer` for the former, or using the
shader program for the latter. This can be notably used to render custom effects on entities and block entities.

For examples of entities using those, see [the relevant test mod](https://github.com/Ladysnake/Satin/blob/master/test_mods/render-layer/src/main/java/ladysnake/satinrenderlayer/).

**Regular blocks do not support custom render layers.** For advanced shader materials, you should consider using
an alternative renderer like [Canvas](https://github.com/grondag/canvas).

### Shader Utilities

Satin has a few utility classes and methods to facilitate working with shaders, not limited to `ShaderEffect`. 
`ShaderLoader` provides a way to load, create and link OpenGL shader programs through a single method call, 
`GlPrograms` offer helper methods that operate on those programs, and the `matrix` package helps with matrix retrieval and manipulation. 
More information is available in the javadoc.

### Events

Satin adds in a few events for rendering purposes / related to shaders. Currently, there are 5 available callbacks:

- `EntitiesPostRenderCallback`: fired between entity rendering end and block entity rendering start
- `PickEntityShaderCallback`: allows mods to add their own entity view shaders
- `PostWorldRenderCallback`: allows mods to render things between the moment minecraft finishes rendering the world
and the moment it starts rendering HUD's and GUI's
- `ResolutionChangeCallback`: allows mods to react to resolution changes
- `ShaderEffectRenderCallback`: fired at the time vanilla renders their post process shaders

*Note that those events may move to a more dedicated library at some point.*

## Full documentation
This [repository's wiki](https://github.com/Ladysnake/Satin/wiki) provides documentation to write and use shaders with Satin API.
