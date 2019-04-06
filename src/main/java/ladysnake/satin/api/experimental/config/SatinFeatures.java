package ladysnake.satin.api.experimental.config;

import ladysnake.satin.Satin;
import ladysnake.satin.impl.ConfigLoader;

import java.io.IOException;

public class SatinFeatures {
    private static SatinFeatures instance;

    public static synchronized SatinFeatures getInstance() {
        if (instance == null) {
            try {
                instance = ConfigLoader.load("satin.json", SatinFeatures.class, SatinFeatures::new);
            } catch (IOException e) {
                Satin.LOGGER.error("Failed to load features from config!");
                instance = new SatinFeatures();
            }
        }
        return instance;
    }

    public final OptionalFeature readableDepthFramebuffers = new OptionalFeature().enableByDefault();

    private SatinFeatures() { super(); }

}
