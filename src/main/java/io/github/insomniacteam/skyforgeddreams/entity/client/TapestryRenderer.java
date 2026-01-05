package io.github.insomniacteam.skyforgeddreams.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.insomniacteam.skyforgeddreams.entity.TapestryEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Renderer for the Tapestry entity.
 * Handles rotation to mount properly on walls and dynamic epoch texture rendering.
 */
public class TapestryRenderer extends GeoEntityRenderer<TapestryEntity> {
    private final DynamicTapestryTexture dynamicTexture;

    public TapestryRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TapestryModel());
        this.dynamicTexture = new DynamicTapestryTexture();
    }

    @Override
    protected void applyRotations(TapestryEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float scale) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entity.getYRot()));
        poseStack.translate(0.0D, -2.01, 0.0D);
    }

    @Override
    public void preRender(PoseStack poseStack, TapestryEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        // Update the dynamic texture
        dynamicTexture.update(animatable.level());
    }

    @Override
    public RenderType getRenderType(TapestryEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        // Use the dynamic texture instead of the static one
        ResourceLocation dynamicTextureLocation = dynamicTexture.getTextureLocation();
        return RenderType.entityCutoutNoCull(dynamicTextureLocation);
    }
}
