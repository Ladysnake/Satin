package ladysnake.satin.api.program;

import ladysnake.satin.impl.ShaderLoaderImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apiguardian.api.API;

import javax.annotation.Nullable;
import java.io.IOException;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * A {@link ShaderLoader} simplifies the process of loading, creating and linking OpenGL shader objects
 *
 * @since 1.0.0
 */
public interface ShaderLoader {
    @API(status = MAINTAINED)
    static ShaderLoader getInstance() {
        return ShaderLoaderImpl.INSTANCE;
    }

    /**
     * Initializes a program from a fragment and a vertex source file
     *
     * @param vertexLocation   the name or relative location of the vertex shader
     * @param fragmentLocation the name or relative location of the fragment shader
     * @return the reference to the initialized program
     * @throws IOException If an I/O error occurs while reading the shader source files
     * @throws ShaderLinkException if the shader fails to be linked
     */
    @API(status = MAINTAINED)
    int loadShader(ResourceManager resourceManager, @Nullable Identifier vertexLocation, @Nullable Identifier fragmentLocation) throws IOException;
}
