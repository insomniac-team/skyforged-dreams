package io.github.insomniacteam.skyforgeddreams.client.model;

import io.github.insomniacteam.skyforgeddreams.SkyforgedDreams;
import io.github.insomniacteam.skyforgeddreams.entity.BluetailEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BluetailModel extends GeoModel<BluetailEntity> {
    private static final ResourceLocation MODEL_LOCATION =
            ResourceLocation.fromNamespaceAndPath(SkyforgedDreams.MOD_ID, "geo/bluetail.geo.json");
    private static final ResourceLocation TEXTURE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(SkyforgedDreams.MOD_ID, "textures/entity/bluetail.png");
    private static final ResourceLocation ANIMATION_LOCATION =
            ResourceLocation.fromNamespaceAndPath(SkyforgedDreams.MOD_ID, "animations/bluetail.animation.json");

    @Override
    public ResourceLocation getModelResource(BluetailEntity animatable) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(BluetailEntity animatable) {
        return TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(BluetailEntity animatable) {
        return ANIMATION_LOCATION;
    }
}
