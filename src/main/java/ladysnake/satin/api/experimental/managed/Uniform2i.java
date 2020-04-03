package ladysnake.satin.api.experimental.managed;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

public interface Uniform2i {
    /**
     * Sets the value of a uniform
     *
     * @param value0      int value
     * @param value1      int value
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    void set(int value0, int value1);
}
