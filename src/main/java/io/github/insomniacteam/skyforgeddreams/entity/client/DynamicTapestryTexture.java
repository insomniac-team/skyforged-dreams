package io.github.insomniacteam.skyforgeddreams.entity.client;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.insomniacteam.skyforgeddreams.SkyforgedDreams;
import io.github.insomniacteam.skyforgeddreams.worldstate.EpochManager;
import io.github.insomniacteam.skyforgeddreams.worldstate.WorldEpoch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.io.IOException;
import java.io.InputStream;

/**
 * Manages the dynamic texture for the Tapestry entity.
 * Blends between epoch textures based on the current world state.
 */
public class DynamicTapestryTexture {
    private static final ResourceLocation DYNAMIC_TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(
            SkyforgedDreams.MOD_ID,
            "textures/entity/tapestry_dynamic.png"
    );

    private static final int TRANSITION_START_PERCENT = 80;
    private static final int SEAM_COLOR = 0xFF8B00FF; // Purple color for the seam (ARGB format)
    private static final int SEAM_WIDTH = 2; // Width of the purple seam in pixels

    // Tapestry painting area bounds (the area that changes, not the frame)
    private static final int PAINTING_WIDTH = 44;  // 0-43 pixels
    private static final int PAINTING_HEIGHT = 60; // 0-59 pixels

    private DynamicTexture dynamicTexture;
    private NativeImage baseTexture = null; // The base tapestry texture with frame
    private WorldEpoch lastEpoch = null;
    private WorldEpoch lastNextEpoch = null;
    private int lastProgress = -1;

    public DynamicTapestryTexture() {
    }

    /**
     * Updates the dynamic texture based on the current epoch state.
     */
    public void update(Level level) {
        if (level == null) {
            return;
        }

        if (level.isClientSide) {
            // On client side, use the synced epoch data from ClientEpochCache
            updateFromClientCache();
        } else {
            // On server side (shouldn't happen in rendering, but just in case)
            if (level instanceof ServerLevel serverLevel) {
                updateFromServerLevel(serverLevel);
            }
        }
    }

    private void updateFromClientCache() {
        WorldEpoch currentEpoch = io.github.insomniacteam.skyforgeddreams.worldstate.ClientEpochCache.getCurrentEpoch();
        WorldEpoch nextEpoch = io.github.insomniacteam.skyforgeddreams.worldstate.ClientEpochCache.getNextEpoch();
        int progress = io.github.insomniacteam.skyforgeddreams.worldstate.ClientEpochCache.getProgress();

        // Check if we're in single player and can access server directly
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSingleplayerServer() != null) {
            ServerLevel serverLevel = mc.getSingleplayerServer().overworld();
            if (serverLevel != null) {
                EpochManager epochManager = EpochManager.get(serverLevel);
                currentEpoch = epochManager.getCurrentEpoch();
                nextEpoch = epochManager.getNextEpoch();
                progress = epochManager.getProgressPercentage();
            }
        }

        // Only update if something changed
        if (currentEpoch == lastEpoch && nextEpoch == lastNextEpoch && progress == lastProgress) {
            return;
        }

        lastEpoch = currentEpoch;
        lastNextEpoch = nextEpoch;
        lastProgress = progress;

        // Generate the blended texture
        generateBlendedTexture(currentEpoch, nextEpoch, progress);
    }

    private void updateFromServerLevel(ServerLevel serverLevel) {
        EpochManager epochManager = EpochManager.get(serverLevel);
        WorldEpoch currentEpoch = epochManager.getCurrentEpoch();
        WorldEpoch nextEpoch = epochManager.getNextEpoch();
        int progress = epochManager.getProgressPercentage();

        // Only update if something changed
        if (currentEpoch == lastEpoch && nextEpoch == lastNextEpoch && progress == lastProgress) {
            return;
        }

        lastEpoch = currentEpoch;
        lastNextEpoch = nextEpoch;
        lastProgress = progress;

        // Generate the blended texture
        generateBlendedTexture(currentEpoch, nextEpoch, progress);
    }

    private void generateBlendedTexture(WorldEpoch currentEpoch, WorldEpoch nextEpoch, int progress) {
        try {
            // Load base tapestry texture if not loaded yet
            if (baseTexture == null) {
                baseTexture = loadBaseTapestryTexture();
                if (baseTexture == null) {
                    return;
                }
            }

            // Load current epoch texture
            NativeImage currentImage = loadEpochTexture(currentEpoch);
            if (currentImage == null) {
                return;
            }

            // Create result image - start with base tapestry (includes frame)
            NativeImage resultImage = new NativeImage(baseTexture.getWidth(), baseTexture.getHeight(), false);

            // Copy the entire base texture (frame and all)
            for (int y = 0; y < baseTexture.getHeight(); y++) {
                for (int x = 0; x < baseTexture.getWidth(); x++) {
                    resultImage.setPixelRGBA(x, y, baseTexture.getPixelRGBA(x, y));
                }
            }

            if (progress < TRANSITION_START_PERCENT) {
                // No transition yet, just paint current epoch texture on the painting area
                paintEpochTexture(currentImage, resultImage);
            } else {
                // Transition is happening
                NativeImage nextImage = loadEpochTexture(nextEpoch);
                if (nextImage == null) {
                    paintEpochTexture(currentImage, resultImage);
                } else {
                    paintWithTransition(currentImage, nextImage, resultImage, progress);
                    nextImage.close();
                }
            }

            currentImage.close();

            // Upload to GPU
            if (dynamicTexture == null) {
                dynamicTexture = new DynamicTexture(resultImage);
                Minecraft.getInstance().getTextureManager().register(DYNAMIC_TEXTURE_LOCATION, dynamicTexture);
            } else {
                dynamicTexture.setPixels(resultImage);
                dynamicTexture.upload();
            }

        } catch (Exception e) {
            SkyforgedDreams.LOG.error("Failed to generate tapestry texture", e);
        }
    }

    /**
     * Paints epoch texture onto the painting area without transition
     */
    private void paintEpochTexture(NativeImage epochTexture, NativeImage result) {
        for (int y = 0; y < PAINTING_HEIGHT && y < epochTexture.getHeight(); y++) {
            for (int x = 0; x < PAINTING_WIDTH && x < epochTexture.getWidth(); x++) {
                result.setPixelRGBA(x, y, epochTexture.getPixelRGBA(x, y));
            }
        }
    }

    /**
     * Paints epoch textures with transition effect onto the painting area
     */
    private void paintWithTransition(NativeImage current, NativeImage next, NativeImage result, int progress) {
        // Calculate transition progress (0.0 to 1.0) within the 80-100% range
        float transitionProgress = (progress - TRANSITION_START_PERCENT) / 20.0f;
        transitionProgress = Math.max(0.0f, Math.min(1.0f, transitionProgress));

        // Calculate the transition line position (from top to bottom) within painting area
        int transitionY = (int) (PAINTING_HEIGHT * transitionProgress);

        for (int y = 0; y < PAINTING_HEIGHT; y++) {
            // Generate noise offset for this scanline to create wavy seam
            float noiseOffset = generateNoise(y, transitionProgress);
            int adjustedTransitionY = transitionY + (int) (noiseOffset * 8); // ±8 pixels variance

            for (int x = 0; x < PAINTING_WIDTH; x++) {
                // Determine which texture to use
                if (y < adjustedTransitionY - SEAM_WIDTH) {
                    // Below transition - use next epoch
                    result.setPixelRGBA(x, y, next.getPixelRGBA(x, y));
                } else if (y < adjustedTransitionY + SEAM_WIDTH) {
                    // On the seam - blend with purple
                    int baseColor = (y < adjustedTransitionY) ? next.getPixelRGBA(x, y) : current.getPixelRGBA(x, y);
                    result.setPixelRGBA(x, y, blendColors(baseColor, SEAM_COLOR, 0.5f));
                } else {
                    // Above transition - use current epoch
                    result.setPixelRGBA(x, y, current.getPixelRGBA(x, y));
                }
            }
        }
    }

    /**
     * Generates Perlin-like noise for the transition seam.
     * Creates a wavy, non-linear transition line.
     */
    private float generateNoise(int y, float time) {
        // Simple multi-octave noise
        float noise = 0.0f;

        // First octave - large waves
        noise += Math.sin(y * 0.1f + time * 6.28f) * 0.5f;

        // Second octave - medium waves
        noise += Math.sin(y * 0.3f + time * 12.56f) * 0.3f;

        // Third octave - small details
        noise += Math.sin(y * 0.7f + time * 18.84f) * 0.2f;

        return noise;
    }

    /**
     * Blends two ARGB colors together.
     */
    private int blendColors(int color1, int color2, float ratio) {
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int a = (int) (a1 * (1 - ratio) + a2 * ratio);
        int r = (int) (r1 * (1 - ratio) + r2 * ratio);
        int g = (int) (g1 * (1 - ratio) + g2 * ratio);
        int b = (int) (b1 * (1 - ratio) + b2 * ratio);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private NativeImage loadBaseTapestryTexture() {
        ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(
                SkyforgedDreams.MOD_ID,
                "textures/entity/tapestry.png"
        );

        try {
            InputStream stream = Minecraft.getInstance()
                    .getResourceManager()
                    .getResource(textureLocation)
                    .orElseThrow()
                    .open();

            NativeImage image = NativeImage.read(stream);
            stream.close();
            return image;
        } catch (IOException e) {
            SkyforgedDreams.LOG.error("Failed to load base tapestry texture: " + textureLocation, e);
            return null;
        }
    }

    private NativeImage loadEpochTexture(WorldEpoch epoch) {
        if (epoch == null) {
            return null;
        }

        ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(
                SkyforgedDreams.MOD_ID,
                "textures/entity/ages/" + epoch.getName() + ".png"
        );

        try {
            InputStream stream = Minecraft.getInstance()
                    .getResourceManager()
                    .getResource(textureLocation)
                    .orElseThrow()
                    .open();

            NativeImage image = NativeImage.read(stream);
            stream.close();
            return image;
        } catch (IOException e) {
            SkyforgedDreams.LOG.error("Failed to load epoch texture: " + textureLocation, e);
            return null;
        }
    }

    public ResourceLocation getTextureLocation() {
        return DYNAMIC_TEXTURE_LOCATION;
    }

    public void close() {
        if (dynamicTexture != null) {
            dynamicTexture.close();
        }
        if (baseTexture != null) {
            baseTexture.close();
        }
    }
}
