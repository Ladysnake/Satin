package ladysnake.satin.api.program;

/**
 * A {@code ShaderLinkException} is thrown by {@link ShaderLoader}
 * when a shader program fails to be linked
 */
public class ShaderLinkException extends RuntimeException {
    public ShaderLinkException(String message) {
        super(message);
    }
}
