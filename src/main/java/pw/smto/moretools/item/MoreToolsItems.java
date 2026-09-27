package pw.smto.moretools.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import pw.smto.moretools.util.ConfigManager;

import java.util.function.Function;

public class MoreToolsItems {
    public static void register() {
    }

    public static final Item WOODEN_HAMMER = register(
        MoreToolsItemIds.WOODEN_HAMMER,
        "wooden_hammer",
        Items.WOODEN_PICKAXE,
        ToolMaterial.WOOD,
        Item.Properties::pickaxe,
        HammerToolItem::new,
        new Item.Properties()
    );
    public static final Item STONE_HAMMER = register(
        MoreToolsItemIds.STONE_HAMMER,
        "stone_hammer",
        Items.STONE_PICKAXE,
        ToolMaterial.STONE,
        Item.Properties::pickaxe,
        HammerToolItem::new,
        new Item.Properties()
    );
    public static final Item COPPER_HAMMER = register(
        MoreToolsItemIds.COPPER_HAMMER,
        "copper_hammer",
        Items.COPPER_PICKAXE,
        ToolMaterial.COPPER,
        Item.Properties::pickaxe,
        HammerToolItem::new,
        new Item.Properties()
    );
    public static final Item IRON_HAMMER = register(
        MoreToolsItemIds.IRON_HAMMER,
        "iron_hammer",
        Items.IRON_PICKAXE,
        ToolMaterial.IRON,
        Item.Properties::pickaxe,
        HammerToolItem::new,
        new Item.Properties()
    );
    public static final Item GOLDEN_HAMMER = register(
        MoreToolsItemIds.GOLDEN_HAMMER,
        "golden_hammer",
        Items.GOLDEN_PICKAXE,
        ToolMaterial.GOLD,
        Item.Properties::pickaxe,
        HammerToolItem::new,
        new Item.Properties()
    );
    public static final Item DIAMOND_HAMMER = register(
        MoreToolsItemIds.DIAMOND_HAMMER,
        "diamond_hammer",
        Items.DIAMOND_PICKAXE,
        ToolMaterial.DIAMOND,
        Item.Properties::pickaxe,
        HammerToolItem::new,
        new Item.Properties()
    );
    public static final Item NETHERITE_HAMMER = register(
        MoreToolsItemIds.NETHERITE_HAMMER,
        "netherite_hammer",
        Items.NETHERITE_PICKAXE,
        ToolMaterial.NETHERITE,
        Item.Properties::pickaxe,
        HammerToolItem::new,
        new Item.Properties().fireResistant()
    );

    public static final Item WOODEN_EXCAVATOR = register(
        MoreToolsItemIds.WOODEN_EXCAVATOR,
        "wooden_excavator",
        Items.WOODEN_SHOVEL,
        ToolMaterial.WOOD,
        Item.Properties::shovel,
        ExcavatorToolItem::new,
        new Item.Properties()
    );
    public static final Item STONE_EXCAVATOR = register(
        MoreToolsItemIds.STONE_EXCAVATOR,
        "stone_excavator",
        Items.STONE_SHOVEL,
        ToolMaterial.STONE,
        Item.Properties::shovel,
        ExcavatorToolItem::new,
        new Item.Properties()
    );
    public static final Item COPPER_EXCAVATOR = register(
        MoreToolsItemIds.COPPER_EXCAVATOR,
        "copper_excavator",
        Items.COPPER_SHOVEL,
        ToolMaterial.COPPER,
        Item.Properties::shovel,
        ExcavatorToolItem::new,
        new Item.Properties()
    );
    public static final Item IRON_EXCAVATOR = register(
        MoreToolsItemIds.IRON_EXCAVATOR,
        "iron_excavator",
        Items.IRON_SHOVEL,
        ToolMaterial.IRON,
        Item.Properties::shovel,
        ExcavatorToolItem::new,
        new Item.Properties()
    );
    public static final Item GOLDEN_EXCAVATOR = register(
        MoreToolsItemIds.GOLDEN_EXCAVATOR,
        "golden_excavator",
        Items.GOLDEN_SHOVEL,
        ToolMaterial.GOLD,
        Item.Properties::shovel,
        ExcavatorToolItem::new,
        new Item.Properties()
    );
    public static final Item DIAMOND_EXCAVATOR = register(
        MoreToolsItemIds.DIAMOND_EXCAVATOR,
        "diamond_excavator",
        Items.DIAMOND_SHOVEL,
        ToolMaterial.DIAMOND,
        Item.Properties::shovel,
        ExcavatorToolItem::new,
        new Item.Properties()
    );
    public static final Item NETHERITE_EXCAVATOR = register(
        MoreToolsItemIds.NETHERITE_EXCAVATOR,
        "netherite_excavator",
        Items.NETHERITE_SHOVEL,
        ToolMaterial.NETHERITE,
        Item.Properties::shovel,
        ExcavatorToolItem::new,
        new Item.Properties().fireResistant()
    );

    public static final Item WOODEN_SAW = register(
        MoreToolsItemIds.WOODEN_SAW,
        "wooden_saw",
        Items.WOODEN_AXE,
        ToolMaterial.WOOD,
        Item.Properties::axe,
        SawToolItem::new,
        new Item.Properties()
    );
    public static final Item STONE_SAW = register(
        MoreToolsItemIds.STONE_SAW,
        "stone_saw",
        Items.STONE_AXE,
        ToolMaterial.STONE,
        Item.Properties::axe,
        SawToolItem::new,
        new Item.Properties()
    );
    public static final Item COPPER_SAW = register(
        MoreToolsItemIds.COPPER_SAW,
        "copper_saw",
        Items.COPPER_AXE,
        ToolMaterial.COPPER,
        Item.Properties::axe,
        SawToolItem::new,
        new Item.Properties()
    );
    public static final Item IRON_SAW = register(
        MoreToolsItemIds.IRON_SAW,
        "iron_saw",
        Items.IRON_AXE,
        ToolMaterial.IRON,
        Item.Properties::axe,
        SawToolItem::new,
        new Item.Properties()
    );
    public static final Item GOLDEN_SAW = register(
        MoreToolsItemIds.GOLDEN_SAW,
        "golden_saw",
        Items.GOLDEN_AXE,
        ToolMaterial.GOLD,
        Item.Properties::axe,
        SawToolItem::new,
        new Item.Properties()
    );
    public static final Item DIAMOND_SAW = register(
        MoreToolsItemIds.DIAMOND_SAW,
        "diamond_saw",
        Items.DIAMOND_AXE,
        ToolMaterial.DIAMOND,
        Item.Properties::axe,
        SawToolItem::new,
        new Item.Properties()
    );
    public static final Item NETHERITE_SAW = register(
        MoreToolsItemIds.NETHERITE_SAW,
        "netherite_saw",
        Items.NETHERITE_AXE,
        ToolMaterial.NETHERITE,
        Item.Properties::axe,
        SawToolItem::new,
        new Item.Properties().fireResistant()
    );

    public static final Item WOODEN_VEIN_HAMMER = register(
        MoreToolsItemIds.WOODEN_VEIN_HAMMER,
        "wooden_vein_hammer",
        Items.WOODEN_PICKAXE,
        ToolMaterial.WOOD,
        Item.Properties::pickaxe,
        VeinHammerToolItem::new,
        new Item.Properties()
    );
    public static final Item STONE_VEIN_HAMMER = register(
        MoreToolsItemIds.STONE_VEIN_HAMMER,
        "stone_vein_hammer",
        Items.STONE_PICKAXE,
        ToolMaterial.STONE,
        Item.Properties::pickaxe,
        VeinHammerToolItem::new,
        new Item.Properties()
    );
    public static final Item COPPER_VEIN_HAMMER = register(
        MoreToolsItemIds.COPPER_VEIN_HAMMER,
        "copper_vein_hammer",
        Items.COPPER_PICKAXE,
        ToolMaterial.COPPER,
        Item.Properties::pickaxe,
        VeinHammerToolItem::new,
        new Item.Properties()
    );
    public static final Item IRON_VEIN_HAMMER = register(
        MoreToolsItemIds.IRON_VEIN_HAMMER,
        "iron_vein_hammer",
        Items.IRON_PICKAXE,
        ToolMaterial.IRON,
        Item.Properties::pickaxe,
        VeinHammerToolItem::new,
        new Item.Properties()
    );
    public static final Item GOLDEN_VEIN_HAMMER = register(
        MoreToolsItemIds.GOLDEN_VEIN_HAMMER,
        "golden_vein_hammer",
        Items.GOLDEN_PICKAXE,
        ToolMaterial.GOLD,
        Item.Properties::pickaxe,
        VeinHammerToolItem::new,
        new Item.Properties()
    );
    public static final Item DIAMOND_VEIN_HAMMER = register(
        MoreToolsItemIds.DIAMOND_VEIN_HAMMER,
        "diamond_vein_hammer",
        Items.DIAMOND_PICKAXE,
        ToolMaterial.DIAMOND,
        Item.Properties::pickaxe,
        VeinHammerToolItem::new,
        new Item.Properties()
    );
    public static final Item NETHERITE_VEIN_HAMMER = register(
        MoreToolsItemIds.NETHERITE_VEIN_HAMMER,
        "netherite_vein_hammer",
        Items.NETHERITE_PICKAXE,
        ToolMaterial.NETHERITE,
        Item.Properties::pickaxe,
        VeinHammerToolItem::new,
        new Item.Properties().fireResistant()
    );

    public static final Item WOODEN_VEIN_EXCAVATOR = register(
        MoreToolsItemIds.WOODEN_VEIN_EXCAVATOR,
        "wooden_vein_excavator",
        Items.WOODEN_SHOVEL,
        ToolMaterial.WOOD,
        Item.Properties::shovel,
        VeinExcavatorToolItem::new,
        new Item.Properties()
    );
    public static final Item STONE_VEIN_EXCAVATOR = register(
        MoreToolsItemIds.STONE_VEIN_EXCAVATOR,
        "stone_vein_excavator",
        Items.STONE_SHOVEL,
        ToolMaterial.STONE,
        Item.Properties::shovel,
        VeinExcavatorToolItem::new,
        new Item.Properties()
    );
    public static final Item COPPER_VEIN_EXCAVATOR = register(
        MoreToolsItemIds.COPPER_VEIN_EXCAVATOR,
        "copper_vein_excavator",
        Items.COPPER_SHOVEL,
        ToolMaterial.COPPER,
        Item.Properties::shovel,
        VeinExcavatorToolItem::new,
        new Item.Properties()
    );
    public static final Item IRON_VEIN_EXCAVATOR = register(
        MoreToolsItemIds.IRON_VEIN_EXCAVATOR,
        "iron_vein_excavator",
        Items.IRON_SHOVEL,
        ToolMaterial.IRON,
        Item.Properties::shovel,
        VeinExcavatorToolItem::new,
        new Item.Properties()
    );
    public static final Item GOLDEN_VEIN_EXCAVATOR = register(
        MoreToolsItemIds.GOLDEN_VEIN_EXCAVATOR,
        "golden_vein_excavator",
        Items.GOLDEN_SHOVEL,
        ToolMaterial.GOLD,
        Item.Properties::shovel,
        VeinExcavatorToolItem::new,
        new Item.Properties()
    );
    public static final Item DIAMOND_VEIN_EXCAVATOR = register(
        MoreToolsItemIds.DIAMOND_VEIN_EXCAVATOR,
        "diamond_vein_excavator",
        Items.DIAMOND_SHOVEL,
        ToolMaterial.DIAMOND,
        Item.Properties::shovel,
        VeinExcavatorToolItem::new,
        new Item.Properties()
    );
    public static final Item NETHERITE_VEIN_EXCAVATOR = register(
        MoreToolsItemIds.NETHERITE_VEIN_EXCAVATOR,
        "netherite_vein_excavator",
        Items.NETHERITE_SHOVEL,
        ToolMaterial.NETHERITE,
        Item.Properties::shovel,
        VeinExcavatorToolItem::new,
        new Item.Properties().fireResistant()
    );

    @FunctionalInterface
    public interface PropertiesToolApplier {
        Item.Properties apply(Item.Properties settings, ToolMaterial material, float attackDamageBaseline, float attackSpeedBaseline);
    }

    @FunctionalInterface
    public interface PolyBaseToolItemFactory {
        PolyBaseToolItem create(Item.Properties settings, float fastSpeed, float slowSpeed, Item baseItem);
    }

    private static Item register(ResourceKey<Item> id, String configId, Item baseItem, ToolMaterial material, PropertiesToolApplier toolType, PolyBaseToolItemFactory factory, Item.Properties settings) {
        var config = ConfigManager.config.get(configId);
        return register(
            id,
            p -> factory.create(p, config.defaultSpeed(), config.sneakSpeed(), baseItem),
            toolType.apply(settings, material, Math.max(material.attackDamageBonus() * config.attackDamageModifier(), 1f), config.attackSpeed())
                .durability(material.durability() * config.durabilityMultiplier())
        );
    }

    public static Item register(ResourceKey<Item> itemKey, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        Item item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }
}