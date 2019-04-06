package ladysnake.satin.api.experimental.config;

public class OptionalFeature {
    /**Indicates that this feature is used by at least one mod*/
    private transient boolean used;
    /**Indicates that this feature is enabled by the config*/
    private boolean enabled;

    OptionalFeature enableByDefault() {
        this.enabled = true;
        return this;
    }

    public void use() {
        this.used = true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isInUse() {
        return isEnabled() && used;
    }
}
