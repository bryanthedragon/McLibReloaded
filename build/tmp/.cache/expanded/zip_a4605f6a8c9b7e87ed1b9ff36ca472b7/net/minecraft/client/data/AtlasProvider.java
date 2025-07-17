package net.minecraft.client.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.renderer.MaterialMapper;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.resources.model.AtlasIds;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.item.equipment.trim.TrimPatterns;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AtlasProvider implements DataProvider {
    private static final ResourceLocation TRIM_PALETTE_KEY = ResourceLocation.withDefaultNamespace("trims/color_palettes/trim_palette");
    private static final Map<String, ResourceLocation> TRIM_PALETTE_VALUES = extractAllMaterialAssets()
        .collect(
            Collectors.toMap(MaterialAssetGroup.AssetInfo::suffix, p_397747_ -> ResourceLocation.withDefaultNamespace("trims/color_palettes/" + p_397747_.suffix()))
        );
    private static final List<ResourceKey<TrimPattern>> VANILLA_PATTERNS = List.of(
        TrimPatterns.SENTRY,
        TrimPatterns.DUNE,
        TrimPatterns.COAST,
        TrimPatterns.WILD,
        TrimPatterns.WARD,
        TrimPatterns.EYE,
        TrimPatterns.VEX,
        TrimPatterns.TIDE,
        TrimPatterns.SNOUT,
        TrimPatterns.RIB,
        TrimPatterns.SPIRE,
        TrimPatterns.WAYFINDER,
        TrimPatterns.SHAPER,
        TrimPatterns.SILENCE,
        TrimPatterns.RAISER,
        TrimPatterns.HOST,
        TrimPatterns.FLOW,
        TrimPatterns.BOLT
    );
    private static final List<EquipmentClientInfo.LayerType> HUMANOID_LAYERS = List.of(
        EquipmentClientInfo.LayerType.HUMANOID, EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS
    );
    private final PackOutput.PathProvider pathProvider;

    public AtlasProvider(PackOutput p_391466_) {
        this.pathProvider = p_391466_.createPathProvider(PackOutput.Target.RESOURCE_PACK, "atlases");
    }

    private static List<ResourceLocation> patternTextures() {
        List<ResourceLocation> list = new ArrayList<>(VANILLA_PATTERNS.size() * HUMANOID_LAYERS.size());

        for (ResourceKey<TrimPattern> resourcekey : VANILLA_PATTERNS) {
            ResourceLocation resourcelocation = TrimPatterns.defaultAssetId(resourcekey);

            for (EquipmentClientInfo.LayerType equipmentclientinfo$layertype : HUMANOID_LAYERS) {
                list.add(resourcelocation.withPath(p_392927_ -> equipmentclientinfo$layertype.trimAssetPrefix() + "/" + p_392927_));
            }
        }

        return list;
    }

    private static SpriteSource forMaterial(Material p_394734_) {
        return new SingleFile(p_394734_.texture());
    }

    private static SpriteSource forMapper(MaterialMapper p_392705_) {
        return new DirectoryLister(p_392705_.prefix(), p_392705_.prefix() + "/");
    }

    private static List<SpriteSource> simpleMapper(MaterialMapper p_395587_) {
        return List.of(forMapper(p_395587_));
    }

    private static List<SpriteSource> noPrefixMapper(String p_397331_) {
        return List.of(new DirectoryLister(p_397331_, ""));
    }

    private static Stream<MaterialAssetGroup.AssetInfo> extractAllMaterialAssets() {
        return ItemModelGenerators.TRIM_MATERIAL_MODELS
            .stream()
            .map(ItemModelGenerators.TrimMaterialData::assets)
            .flatMap(p_392441_ -> Stream.concat(Stream.of(p_392441_.base()), p_392441_.overrides().values().stream()))
            .sorted(Comparator.comparing(MaterialAssetGroup.AssetInfo::suffix));
    }

    private static List<SpriteSource> armorTrims() {
        return List.of(new PalettedPermutations(patternTextures(), TRIM_PALETTE_KEY, TRIM_PALETTE_VALUES));
    }

    private static List<SpriteSource> blocksList() {
        return List.of(
            forMapper(Sheets.BLOCKS_MAPPER),
            forMapper(Sheets.ITEMS_MAPPER),
            forMapper(ConduitRenderer.MAPPER),
            forMaterial(BellRenderer.BELL_RESOURCE_LOCATION),
            forMaterial(Sheets.DECORATED_POT_SIDE),
            forMaterial(EnchantTableRenderer.BOOK_LOCATION),
            new PalettedPermutations(
                List.of(ItemModelGenerators.TRIM_PREFIX_HELMET, ItemModelGenerators.TRIM_PREFIX_CHESTPLATE, ItemModelGenerators.TRIM_PREFIX_LEGGINGS, ItemModelGenerators.TRIM_PREFIX_BOOTS),
                TRIM_PALETTE_KEY,
                TRIM_PALETTE_VALUES
            )
        );
    }

    private static List<SpriteSource> bannerPatterns() {
        return List.of(forMaterial(ModelBakery.BANNER_BASE), forMapper(Sheets.BANNER_MAPPER));
    }

    private static List<SpriteSource> shieldPatterns() {
        return List.of(forMaterial(ModelBakery.SHIELD_BASE), forMaterial(ModelBakery.NO_PATTERN_SHIELD), forMapper(Sheets.SHIELD_MAPPER));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput p_396582_) {
        return CompletableFuture.allOf(
            this.storeAtlas(p_396582_, AtlasIds.ARMOR_TRIMS, armorTrims()),
            this.storeAtlas(p_396582_, AtlasIds.BANNER_PATTERNS, bannerPatterns()),
            this.storeAtlas(p_396582_, AtlasIds.BEDS, simpleMapper(Sheets.BED_MAPPER)),
            this.storeAtlas(p_396582_, AtlasIds.BLOCKS, blocksList()),
            this.storeAtlas(p_396582_, AtlasIds.CHESTS, simpleMapper(Sheets.CHEST_MAPPER)),
            this.storeAtlas(p_396582_, AtlasIds.DECORATED_POT, simpleMapper(Sheets.DECORATED_POT_MAPPER)),
            this.storeAtlas(p_396582_, AtlasIds.GUI, noPrefixMapper("gui/sprites")),
            this.storeAtlas(p_396582_, AtlasIds.MAP_DECORATIONS, noPrefixMapper("map/decorations")),
            this.storeAtlas(p_396582_, AtlasIds.MOB_EFFECTS, noPrefixMapper("mob_effect")),
            this.storeAtlas(p_396582_, AtlasIds.PAINTINGS, noPrefixMapper("painting")),
            this.storeAtlas(p_396582_, AtlasIds.PARTICLES, noPrefixMapper("particle")),
            this.storeAtlas(p_396582_, AtlasIds.SHIELD_PATTERNS, shieldPatterns()),
            this.storeAtlas(p_396582_, AtlasIds.SHULKER_BOXES, simpleMapper(Sheets.SHULKER_MAPPER)),
            this.storeAtlas(p_396582_, AtlasIds.SIGNS, simpleMapper(Sheets.SIGN_MAPPER))
        );
    }

    private CompletableFuture<?> storeAtlas(CachedOutput p_392737_, ResourceLocation p_392807_, List<SpriteSource> p_393361_) {
        return DataProvider.saveStable(p_392737_, SpriteSources.FILE_CODEC, p_393361_, this.pathProvider.json(p_392807_));
    }

    @Override
    public String getName() {
        return "Atlas Definitions";
    }
}