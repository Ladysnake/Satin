package ladysnake.satin.config;

public class OptionalFeature {
    /**Indicates that this feature is used by at least one mod*/
    private transient boolean used;
    /**Indicates that this feature is enabled by the config*/
    private boolean configEnabled;

    OptionalFeature enableByDefault() {
        this.configEnabled = true;
        return this;
    }

    public void use() {
        this.used = true;
    }

    public boolean isConfigEnabled() {
        return configEnabled;
    }

    public boolean isActive() {
        return configEnabled && used;
    }
}
