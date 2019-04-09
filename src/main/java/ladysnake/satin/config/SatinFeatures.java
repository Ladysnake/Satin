package ladysnake.satin.config;

import ladysnake.satin.Satin;

import java.io.IOException;

public class SatinFeatures implements Versionable {
    private static final int CURRENT_VERSION = 1;
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

    private int version = CURRENT_VERSION;
    public final OptionalFeature readableDepthFramebuffers = new OptionalFeature().name("Framebuffer depth textures").enableByDefault();

    @Override
    public boolean isUpToDate() {
        return this.version == CURRENT_VERSION;
    }

    @Override
    public void update() {
        this.version = CURRENT_VERSION;
    }

    private SatinFeatures() { super(); }
}
