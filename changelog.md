------------------------------------------------------
Version 1.18.0
------------------------------------------------------
**Additions**
- Added `WorldRendererReloadCallback`, an event that gets triggered when e.g. video settings are updated or the player joins a world

**Changes**
- Shaders' init callbacks now also run during the above event
  - This fixes resource leaks caused by setting a sampler uniform to a vanilla Framebuffer in those callbacks

------------------------------------------------------
Version 1.17.0
------------------------------------------------------
Updated to MC 1.20.5

**Additions**
- Added `PostWorldRenderCallbackV3`, with the projection and model-view matrices passed in

**Changes**
- The `nanoTime` parameter in `PostWorldRenderCallback` and `PostWorldRenderCallbackV2` has been deprecated, and is subject to removal in a future version

------------------------------------------------------
Version 1.16.0
------------------------------------------------------
Updated to MC 1.20.4 proper (thanks DietrichPaul!)

**Additions**
- Added a new `UniformMat4#setFromArray` method for those cases where going through a matrix is too impractical

------------------------------------------------------
Version 1.15.0
------------------------------------------------------
Updated to MC 1.20.3 (thanks DietrichPaul!)

**Changes**
- Slightly optimized shader uniform loading

------------------------------------------------------
Version 1.14.0
------------------------------------------------------
Updated to MC 1.20.1

**Additions**
- Added the `satin:format` framebuffer extension (big thanks to Will BL !)

**Changes**
- Moved publication to the ladysnake maven (`maven.ladysnake.org/releases` instead of `ladysnake.jfrog.io/mods`)
- Changed the maven group to `org.ladysnake`

Refer to the readme for updated buildscript instructions.

------------------------------------------------------
Version 1.13.0
------------------------------------------------------
Updated to MC 1.20

------------------------------------------------------
Version 1.12.1
------------------------------------------------------
**Changes**
- Optimized shader loading (should now be faster)

**Fixes**
- Fixed managed sampler uniforms losing data upon resource reloading (Ported fix from v1.10.1 to MC 1.19.4)

------------------------------------------------------
Version 1.12.0
------------------------------------------------------
**Additions**
- Satin's float uniforms now have a setter overload that accepts a JOML vector object as argument

**Fixes**
- Fixed crash with Iris installed

------------------------------------------------------
Version 1.11.0
------------------------------------------------------
Updated to MC 1.19.4

------------------------------------------------------
Version 1.10.1
------------------------------------------------------
**Fixes**
- Fixed managed sampler uniforms losing data upon resource reloading

------------------------------------------------------
Version 1.10.0
------------------------------------------------------
Updated to MC 1.19.3

------------------------------------------------------
Version 1.9.0
------------------------------------------------------
**Changes**
- The vertex format specified in `ShaderEffectManager#manageCoreShader` is now used by `RenderLayer`s created from `ManagedCoreShader#getRenderLayer`

------------------------------------------------------
Version 1.8.1
------------------------------------------------------
**Fixes**
- Fixed mixin crash when Sodium or Canvas was installed

------------------------------------------------------
Version 1.8.0
------------------------------------------------------
Updated to MC 1.19

------------------------------------------------------
Version 1.7.2
------------------------------------------------------
- removed iris from transitive dependencies

------------------------------------------------------
Version 1.7.1
------------------------------------------------------
**Fixes**
- Fixed crash at launch with Iris

------------------------------------------------------
Version 1.7.0
------------------------------------------------------
Updated to MC 1.18

------------------------------------------------------
Version 1.6.4
------------------------------------------------------
**Fixes**
- Fixed modded shader locations not working with Architectury

------------------------------------------------------
Version 1.6.3
------------------------------------------------------
**Fixes**
- Fixed incompatibility between Satin's customized render layer API and Iris

------------------------------------------------------
Version 1.6.2
------------------------------------------------------
**Fixes**
- Fixed mixin error with Architectury

------------------------------------------------------
Version 1.6.1
------------------------------------------------------
**Fixes**
- Fixed uniforms for core shaders still being reset after resource reloading

------------------------------------------------------
Version 1.6.0
------------------------------------------------------
Updated to 1.17

**Changes**
- `ManagedShaderProgram` has been replaced with `ManagedCoreShader`
  - shader files referenced from one of those should be moved from `assets/shaders/program` to `assets/shaders/core`
  
**Fixes**
- Shader uniforms no longer get their value reset after resource reloading
- Fixed the first call to `ManagedUniform#set` not doing anything when the values were all `0`

*Reminder: we Core Profile now !*

------------------------------------------------------
Version 1.5.1
------------------------------------------------------
- Fixed a crash when calling some GlMatrices methods on Java 8
- Fixed a crash in obfuscated environments when using the RenderLayer duplication feature

------------------------------------------------------
Version 1.5.0
------------------------------------------------------
- Added a method in RenderLayerHelper to get the name of an existing render phase/layer
- Added the ability to set a regular block's render layer through `RenderLayerHelper`
    - Marked the functionality as deprecated because of the potential for incompatibilities.
- Removed experimental `BufferBuildersInitCallback` as it seems superseded by RenderLayer duplication

------------------------------------------------------
Version 1.4.1
------------------------------------------------------
Update to 1.16.2

Changes
- Removed the experimental versions of the `UniformFinder` interface and associated handles (use the promoted versions!)

------------------------------------------------------
Version 1.4.0
------------------------------------------------------
Update to 1.16 (thanks to Draylar and DaveyL2013 for snapshot updates)

Additions
- Added `RenderLayerHelper`, a class with new methods for simplifying the creation of `RenderLayer`s based on existing ones
- Added `ManagedShaderProgram`, a new alternative to `ManagedShaderEffect` that encapsulates a single shader program that can be enabled or disabled.
- Added `ManagedFramebuffer`, for cleaner code when using a `ManagedShaderEffect`'s target framebuffers
- Added `PostWorldRenderCallbackV2`, now passing the MatrixStack for actual drawing

Changes
- Shaders are no longer garbage collected when you remove all references to them without calling `ShaderManager#dispose`. Clean the place before you leave!
- The `UniformFinder` interface and associated handles have been promoted to maintained API and moved to the appropriate package. The previous experimental classes have been marked deprecated.

Fixes
- Fixed a vanilla bug causing corrupted overlays after rendering a shader in `ShaderEffectRenderCallback`, also spectator shaders

------------------------------------------------------
Version 1.3.3
------------------------------------------------------
Bug fixes
- Fixed framebuffer scaling on OSX (closes #1) (thanks to grondag !)

------------------------------------------------------
Version 1.3.2
------------------------------------------------------
Changes
- Satin now works with Optifine installed, at the cost of the custom entity shader feature
- Fixed dependency metadata still using "requires"

------------------------------------------------------
Version 1.3.1
------------------------------------------------------
Changes
- Updated all dependencies (including Fabric API)
- Managed uniforms now ignore calls to `set(...)` when they are not initialized

------------------------------------------------------
Version 1.3.0
------------------------------------------------------
Additions
- Added managed uniforms as an experimental feature

Changes
- Forget lazily loading shaders, managed shaders get loaded with other resources now

Bug fixes
- Fixed a broken early return in uniform uploading (good job Mojang)

------------------------------------------------------
Version 1.2.1
------------------------------------------------------
Additions
- Added an entity pre render callback

Bug fixes
- Fixed test mods

------------------------------------------------------
Version 1.2.0
------------------------------------------------------
Additions
- Added an experimental depth map access, allowing for fancier effects than ever
- Added a config file that can be used to disable the depth map hook
- Added a PostRenderWorldEvent, useful to render effects that use world information

Bug fixes
- Fixed int uniforms crashing the game (Mojang's bug!)

Also Satin now has a wiki and example mods that you can check out!

------------------------------------------------------
Version 1.1.1
------------------------------------------------------
- Fixed a dumb bug causing all mixins to be skipped

------------------------------------------------------
Version 1.1.0
------------------------------------------------------
Additions
- Added an experimental readable depth texture access to framebuffers
- Added a basic json config for optional features (currently used to let users forcefully disable the above feature)
- Added matrix multiplication to GlMatrices
- Also added a logo and support for mod menu

Changes
- Replaced some methods in GlMatrices with better versions. Old ones have been deprecated.
- Updated to Loader 0.4.0 ! *Nest me up daddy*

------------------------------------------------------
Version 1.0.0
------------------------------------------------------
Initial release
