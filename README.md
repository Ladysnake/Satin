# Satin

[![](https://jitpack.io/v/Ladysnake/Satin.svg)](https://jitpack.io/#Ladysnake/Satin)

A lightweight Fabric library for OpenGL shader usage.

---

## Adding Satin to your project

You can add the library by inserting the following in your `build.gradle`:

```gradle
repositories {
	maven { url 'https://jitpack.io' }
}

dependencies {
    modCompile "com.github.Ladysnake:Satin:${satin_version}"
    // Include Satin as a Jar-in-Jar dependency (optional)
    include "com.github.Ladysnake:Satin:${satin_version}"
}
```
*note: the `include` configuration requires loom 0.2.1 or more recent*

You can then add the library version to your `gradle.properties`file:

```properties
# Satin library
satin_version = 1.x.y
```

You can find the current version of Satin in the [releases](https://github.com/Ladysnake/Satin/releases) tab of the repository on Github.

## Using Satin

### Changes to Vanilla

Simply having Satin installed alters the game in a few ways, mainly related to ShaderEffects.

- **Uniform fix**: Using a vector of integers as a uniform crashes the game in Vanilla because of a bad
copy paste. Satin redirects a call to upload the right buffer.
- **Shader locations**: Satin patches JsonGlProgram to accept a resource domain in the specification
of a program name and of a fragment/vertex shader file.
- **Readable depth**: Satin patches minecraft's framebuffer objects to use textures instead of render
buffers as depth attachments. This allows modders to reuse the depth information in shaders notably.
Because this feature has a non-negligible risk of incompatibility with similar patches, it has to be enabled 
by consumer code first, and users can forcefully disable it any time in the config.

### Shader Management

Satin's main feature is the Shader Effect management facility. 

`ShaderEffect` is a Minecraft class implementing data driven post processing shaders, with a few caveats. First, those shader effects are initialized immediately at construction, but they must be initialized after the game has finished loading to avoid gl errors. Then, they must be updated each time the game's resolution changes. Finally, they do not have any way of setting uniforms from external code.

Satin can manage a shader effect for you, giving you a `ManagedShaderEffect` object. This shader effect is lazily initialized - although it can be initialized manually at any time. Initialized shader effects will be automatically reloaded each time the game's resolution changes, and during resource reloading. It also provides a number of access methods to set uniforms dynamically.

Here is the whole java code for a mod that applies a basic shader to the game:

```java
public class GreyscaleMinecraft implements ClientModInitializer {
    private static final ManagedShaderEffect GREYSCALE_SHADER = ShaderEffectManager.getInstance()
    		.manage(new Identifier("shaderexample", "shaders/post/greyscale.json"));
    
    @Override
    public void onInitializeClient() {
        // the render method of the shader will be called after the game
        // has drawn the world on the main framebuffer, when it renders
        // vanilla post process shaders
	ShaderEffectRenderCallback.EVENT.register(GREYSCALE_SHADER::render);
    }
}
```

For examples of json shader definitions, look for the `assets/minecraft/shaders` folder in the minecraft source (description of those shaders can be found on the [Minecraft Wiki](https://minecraft.gamepedia.com/Shaders)). There is also a real application of this library in [Dissolution](https://github.com/Ladysnake/Dissolution/blob/2ab4f85f4d70e45b6c23efba63f9b8b6cf352d32/src/main/java/ladysnake/dissolution/client/DissolutionFx.java).

### Shader Utilities

Satin has a few utility classes and methods to facilitate working with shaders, not limited to `ShaderEffect`. 
`ShaderLoader` provides a way to load, create and link OpenGL shader programs through a single method call, 
`GlPrograms` offer helper methods that operate on those programs, and the `matrix` package helps with matrix retrieval and manipulation. 
More information is available in the javadoc.

### Events

Satin adds in a few events for rendering purposes / related to shaders. Currently, there are 4 available callbacks:

- `EntitiesPostRenderCallback`: fired between entity rendering end and block entity rendering start
- `PickEntityShaderCallback`: allows mods to add their own entity view shaders
- `PostWorldRenderCallback`: allows mods to render things between the moment minecraft finishes to render the world
and the moment it starts rendering HUD's and GUI's
- `ResolutionChangeCallback`: allows mods to react to resolution changes
- `ShaderEffectRenderCallback`: fired at the time vanilla renders their post process shaders

*Note that those events may move to a more dedicated library at some point.*

## Full documentation
This [repository's wiki](https://github.com/Ladysnake/Satin/wiki) provides documentation to write and use shaders with Satin API.
