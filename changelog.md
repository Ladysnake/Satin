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
