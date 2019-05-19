package ladysnake.satin.api.experimental.managed;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

public interface Uniform1i {
    /**
     * Sets the value of this uniform
     *
     * @param value       int value
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    void set(int value);
}
