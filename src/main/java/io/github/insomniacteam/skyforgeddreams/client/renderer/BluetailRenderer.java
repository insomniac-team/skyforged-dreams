package io.github.insomniacteam.skyforgeddreams.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.insomniacteam.skyforgeddreams.SkyforgedDreams;
import io.github.insomniacteam.skyforgeddreams.client.model.BluetailModel;
import io.github.insomniacteam.skyforgeddreams.entity.BluetailEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BluetailRenderer extends GeoEntityRenderer<BluetailEntity> {

    public BluetailRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BluetailModel());
        this.shadowRadius = 0.36F;
        this.scaleHeight = 1.35F;
        this.scaleWidth = 1.35F;
    }
}
