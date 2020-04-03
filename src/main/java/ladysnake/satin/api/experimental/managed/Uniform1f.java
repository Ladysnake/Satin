package ladysnake.satin.api.experimental.managed;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

public interface Uniform1f {

    /**
     * Sets the value of a uniform
     *
     * @param value float value
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    void set(float value);
}
