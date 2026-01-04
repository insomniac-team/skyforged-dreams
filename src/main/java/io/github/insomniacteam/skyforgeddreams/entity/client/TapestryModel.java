package io.github.insomniacteam.skyforgeddreams.entity.client;

import io.github.insomniacteam.skyforgeddreams.SkyforgedDreams;
import io.github.insomniacteam.skyforgeddreams.entity.TapestryEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TapestryModel extends GeoModel<TapestryEntity> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
        SkyforgedDreams.MOD_ID,
        "geo/tapestry.geo.json"
    );

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
        SkyforgedDreams.MOD_ID,
        "textures/entity/tapestry.png"
    );

    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(
        SkyforgedDreams.MOD_ID,
        "animations/tapestry.animation.json"
    );

    @Override
    public ResourceLocation getModelResource(TapestryEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(TapestryEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(TapestryEntity animatable) {
        return ANIMATIONS;
    }
}
