# Tapestry Setup Instructions

## Overview
The Tapestry is a 3D wall-mounted entity similar to paintings in Minecraft. It uses GeckoLib for rendering and supports 3D models created in Blockbench.

**Size**: 3 blocks wide × 4 blocks tall

## Code Implementation ✅

All code has been implemented:
- ✅ `TapestryEntity` - Entity class with wall mounting logic
- ✅ `TapestryModel` - GeckoLib model class
- ✅ `TapestryRenderer` - Renderer with proper wall rotation
- ✅ `TapestryItem` - Item that spawns the entity when placed on walls
- ✅ Entity registration in `ModEntityTypes`
- ✅ Renderer registration in `ClientSetup`

## Blockbench Model Export

To replace the placeholder model with your actual Blockbench model, follow these steps:

### 1. Create Your Model in Blockbench

1. Open Blockbench and create a new **"Bedrock Entity"** project
2. Set the model identifier to: `geometry.tapestry`
3. Recommended texture size: 64×64 or larger
4. Create your tapestry design (remember: 3 blocks wide × 4 blocks tall = 48×64 pixels in Minecraft units)

### 2. Export the Model

1. In Blockbench, go to **File → Export → Export GeckoLib Model**
2. Save the `.geo.json` file as: `tapestry.geo.json`
3. Place it in: `src/main/resources/assets/skyforged_dreams/geo/tapestry.geo.json`
   - This will replace the placeholder file

### 3. Export Animations (Optional)

If you want to add animations later:
1. In Blockbench, go to **File → Export → Export GeckoLib Animations**
2. Save as: `tapestry.animation.json`
3. Place in: `src/main/resources/assets/skyforged_dreams/animations/tapestry.animation.json`

### 4. Add Your Texture

1. Export your texture from Blockbench or save it directly
2. Save as PNG: `tapestry.png`
3. Place in: `src/main/resources/assets/skyforged_dreams/textures/entity/tapestry.png`

## File Structure

```
src/main/resources/assets/skyforged_dreams/
├── geo/
│   └── tapestry.geo.json          # Your Blockbench model (replace placeholder)
├── animations/
│   └── tapestry.animation.json    # Animations (optional)
└── textures/
    └── entity/
        └── tapestry.png           # Your texture (ADD THIS)
```

## Current Placeholder

A simple placeholder model has been created:
- **Model**: Simple flat rectangle (48×64×1 units)
- **Animations**: Empty idle animation
- **Texture**: Not included - you need to add `tapestry.png`

## Model Guidelines

When creating your model in Blockbench:

### Size Recommendations
- **Width**: 48 pixels (3 blocks)
- **Height**: 64 pixels (4 blocks)
- **Depth**: 1-4 pixels (very thin, like a tapestry hanging on wall)

### Positioning
- Center the model at origin (0, 0, 0)
- The model will automatically rotate to face the correct direction when placed on a wall
- The renderer handles all rotation logic

### Performance
- Keep polygon count reasonable (aim for < 1000 faces)
- Use efficient UV mapping
- The entity is static (no automatic updates), so complex models are fine

## Testing

1. Build the mod: `./gradlew build`
2. Run the client: `./gradlew runClient`
3. Get the tapestry item from creative inventory (Skyforged Dreams tab)
4. Right-click on any horizontal wall surface to place it
5. The tapestry should:
   - Only place on vertical walls (not floor/ceiling)
   - Face the correct direction
   - Occupy a 3×4 block space
   - Drop the item when broken

## Adding Animations

If you want to add animations in the future:

1. Create animations in Blockbench
2. Export them to `tapestry.animation.json`
3. Update `TapestryEntity.java`:

```java
@Override
public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    controllers.add(new AnimationController<>(this, "controller", 0, state -> {
        return state.setAndContinue(RawAnimation.begin().thenLoop("animation.tapestry.idle"));
    }));
}
```

## Troubleshooting

### Model doesn't appear
- Check that `tapestry.geo.json` is in the correct location
- Verify the model identifier is `geometry.tapestry`
- Check console for error messages

### Texture is missing/purple
- Ensure `tapestry.png` exists in `textures/entity/`
- Check that texture dimensions match your model's UV mapping
- Verify texture path in console output

### Wrong rotation
- The renderer handles rotation automatically based on wall direction
- If rotation is wrong, check the model's pivot point in Blockbench

### Can't place on walls
- Only horizontal faces work (not floor/ceiling)
- Make sure there's enough space (3 wide × 4 tall)
- Check that the blocks behind the wall are solid

## Item Model

The item currently uses the default GeckoLib item rendering. If you want a custom item icon:

1. Create a 2D icon (16×16 PNG recommended)
2. Place in: `textures/item/tapestry_of_the_ages.png`
3. Create item model JSON in: `models/item/tapestry_of_the_ages.json`

## Next Steps

1. **Required**: Add your texture file (`tapestry.png`)
2. **Required**: Replace placeholder model with your Blockbench model
3. **Optional**: Add animations if desired
4. **Optional**: Create custom item icon
5. Test in-game!
