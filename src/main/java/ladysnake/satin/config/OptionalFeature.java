package ladysnake.satin.config;

import ladysnake.satin.Satin;

public class OptionalFeature {
    /**Indicates that this feature is used by at least one mod*/
    private transient boolean used;
    /**The readable name of the feature*/
    private String name;
    /**Indicates that this feature is enabled by the config*/
    private boolean configEnabled;

    OptionalFeature enableByDefault() {
        this.configEnabled = true;
        return this;
    }

    OptionalFeature name(String name) {
        this.name = name;
        return this;
    }

    public void use() {
        if (!this.used && !this.configEnabled) {
            Satin.LOGGER.warn("Couldn't activate the '{}' feature as it is disabled by the config", this.name);
        }
        this.used = true;
    }

    public boolean isActive() {
        return configEnabled && used;
    }
}
