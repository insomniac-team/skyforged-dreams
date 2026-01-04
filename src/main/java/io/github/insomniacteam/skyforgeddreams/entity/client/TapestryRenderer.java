package io.github.insomniacteam.skyforgeddreams.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.insomniacteam.skyforgeddreams.entity.TapestryEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Renderer for the Tapestry entity.
 * Handles rotation to mount properly on walls.
 */
public class TapestryRenderer extends GeoEntityRenderer<TapestryEntity> {
    public TapestryRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TapestryModel());
    }

    @Override
    protected void applyRotations(TapestryEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float scale) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entity.getYRot()));

        poseStack.translate(0.0D, -2.01, 0.0D);
    }
}
