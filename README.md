## **The Ladysnake maven is moving!**

**As Jfrog is ending their free service for OSS projects, we have to move the maven repository before the 1st of July 2023.
See below for the new maven instructions - you will have to update your buildscripts with the new URL before the cutoff date to avoid dependency resolution failures.**


# Satin

[![](https://jitpack.io/v/Ladysnake/Satin.svg)](https://jitpack.io/#Ladysnake/Satin)

A lightweight Fabric library for OpenGL shader usage.

---

## Adding Satin to your project

You can add the library by inserting the following in your `build.gradle` (Requires Loom 0.2.4+):

**Note 1: since MC 1.17 builds, the Satin dependency must be lowercase.**  
**Note 2: since MC 1.20.1 builds (1.14.0), the maven group changed from `io.github.ladysnake` to `org.ladysnake`.**  
**Note 3: since June 2023, the maven url changed from `ladysnake.jfrog.io/artifactory/mods` to `maven.ladysnake.org/releases`.**  

```gradle
repositories {
        maven {
        name = 'Ladysnake Mods'
        url = 'https://maven.ladysnake.org/releases'
        content {
            includeGroup 'io.github.ladysnake'
            includeGroup 'org.ladysnake'
            includeGroupByRegex 'dev\\.onyxstudios.*'
        }
    }
}

dependencies {
    modImplementation "org.ladysnake:satin:${satin_version}"
    // Include Satin as a Jar-in-Jar dependency (optional)
    include "org.ladysnake:satin:${satin_version}"
}
```

You can then add the library version to your `gradle.properties`file:

```properties
# Satin library
satin_version = 1.x.y
```

You can find the current version of Satin in the [releases](https://github.com/Ladysnake/Satin/releases) tab of the repository on Github.

*If you wish your mod to be 100% standalone, you also need to include the `fabric-api-base` and `fabric-resource-loader-v0` modules from Fabric API in your mod jar.*

## Using Satin

### Changes to Vanilla

Simply having Satin installed alters the game in a few ways, mainly related to ShaderEffects.

- **Uniform fix**: Using a vector of integers as a uniform crashes the game in Vanilla because of a bad
copy paste. Satin redirects a call to upload the right buffer.
- **Shader locations**: Satin patches shader processors to accept a resource domain in the specification
of a program name and of a fragment/vertex shader file.
- **Custom Target Formats**: Satin adds a `satin:format` property to [post-process shader JSONs](https://github.com/Ladysnake/Satin/wiki/Post-Process-Shader-format)
  to allow shader targets to specify different formats to `RGBA8`.
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
