package ladysnake.satin.api.experimental.managed;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

public interface UniformFinder {
    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    Uniform1i findUniform1i(String uniformName);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    Uniform2i findUniform2i(String uniformName);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    Uniform3i findUniform3i(String uniformName);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    Uniform4i findUniform4i(String uniformName);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    Uniform1f findUniform1f(String uniformName);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    Uniform2f findUniform2f(String uniformName);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    Uniform3f findUniform3f(String uniformName);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    Uniform4f findUniform4f(String uniformName);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    UniformMat4 findUniformMat4(String uniformName);

    /**
     * Sets the value of a sampler uniform declared in json
     *
     * @param samplerName the name of the sampler uniform field in the shader source file and json
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    SamplerUniform findSamplerUniform(String samplerName);

}
