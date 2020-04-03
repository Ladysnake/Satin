package ladysnake.satin.api.experimental.managed;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

public interface Uniform4f {

    /**
     * Sets the value of a uniform declared in json
     *
     * @param value0 float value
     * @param value1 float value
     * @param value2 float value
     * @param value3 float value
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    void set(float value0, float value1, float value2, float value3);

}
