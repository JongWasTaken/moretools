package pw.smto.moretools.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import pw.smto.moretools.MoreTools;

public class MoreToolsItemIds {
    public static ResourceKey<Item> create(String name) {
        return ResourceKey.create(Registries.ITEM, MoreTools.id(name));
    }
    public static ResourceKey<Item> WOODEN_HAMMER = create("wooden_hammer");
    public static ResourceKey<Item> STONE_HAMMER = create("stone_hammer");
    public static ResourceKey<Item> COPPER_HAMMER = create("copper_hammer");
    public static ResourceKey<Item> IRON_HAMMER = create("iron_hammer");
    public static ResourceKey<Item> GOLDEN_HAMMER = create("golden_hammer");
    public static ResourceKey<Item> DIAMOND_HAMMER = create("diamond_hammer");
    public static ResourceKey<Item> NETHERITE_HAMMER = create("netherite_hammer");
    public static ResourceKey<Item> WOODEN_EXCAVATOR = create("wooden_excavator");
    public static ResourceKey<Item> STONE_EXCAVATOR = create("stone_excavator");
    public static ResourceKey<Item> COPPER_EXCAVATOR = create("copper_excavator");
    public static ResourceKey<Item> IRON_EXCAVATOR = create("iron_excavator");
    public static ResourceKey<Item> GOLDEN_EXCAVATOR = create("golden_excavator");
    public static ResourceKey<Item> DIAMOND_EXCAVATOR = create("diamond_excavator");
    public static ResourceKey<Item> NETHERITE_EXCAVATOR = create("netherite_excavator");
    public static ResourceKey<Item> WOODEN_SAW = create("wooden_saw");
    public static ResourceKey<Item> STONE_SAW = create("stone_saw");
    public static ResourceKey<Item> COPPER_SAW = create("copper_saw");
    public static ResourceKey<Item> IRON_SAW = create("iron_saw");
    public static ResourceKey<Item> GOLDEN_SAW = create("golden_saw");
    public static ResourceKey<Item> DIAMOND_SAW = create("diamond_saw");
    public static ResourceKey<Item> NETHERITE_SAW = create("netherite_saw");
    public static ResourceKey<Item> WOODEN_VEIN_HAMMER = create("wooden_vein_hammer");
    public static ResourceKey<Item> STONE_VEIN_HAMMER = create("stone_vein_hammer");
    public static ResourceKey<Item> COPPER_VEIN_HAMMER = create("copper_vein_hammer");
    public static ResourceKey<Item> IRON_VEIN_HAMMER = create("iron_vein_hammer");
    public static ResourceKey<Item> GOLDEN_VEIN_HAMMER = create("golden_vein_hammer");
    public static ResourceKey<Item> DIAMOND_VEIN_HAMMER = create("diamond_vein_hammer");
    public static ResourceKey<Item> NETHERITE_VEIN_HAMMER = create("netherite_vein_hammer");
    public static ResourceKey<Item> WOODEN_VEIN_EXCAVATOR = create("wooden_vein_excavator");
    public static ResourceKey<Item> STONE_VEIN_EXCAVATOR = create("stone_vein_excavator");
    public static ResourceKey<Item> COPPER_VEIN_EXCAVATOR = create("copper_vein_excavator");
    public static ResourceKey<Item> IRON_VEIN_EXCAVATOR = create("iron_vein_excavator");
    public static ResourceKey<Item> GOLDEN_VEIN_EXCAVATOR = create("golden_vein_excavator");
    public static ResourceKey<Item> DIAMOND_VEIN_EXCAVATOR = create("diamond_vein_excavator");
    public static ResourceKey<Item> NETHERITE_VEIN_EXCAVATOR = create("netherite_vein_excavator");
}
