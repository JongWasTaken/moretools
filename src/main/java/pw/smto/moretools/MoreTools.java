package pw.smto.moretools;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.LoggerFactory;
import pw.smto.moretools.item.ExcavatorToolItem;
import pw.smto.moretools.item.HammerToolItem;
import pw.smto.moretools.item.SawToolItem;
import pw.smto.moretools.item.VeinHammerToolItem;

import java.lang.reflect.Field;
import java.util.*;

public class MoreTools implements ModInitializer {
	public static final String MOD_ID = "moretools";
	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PolymerResourcePackUtils.addModAssets(MOD_ID);
		PolymerResourcePackUtils.markAsRequired();

		// Register all items
		for (Field field : Items.class.getFields()) {
            try {
				Registry.register(Registries.ITEM, Identifier.of(MOD_ID, field.getName().toLowerCase(Locale.ROOT)), (Item)field.get(null));
            } catch (Exception ignored) {
				LOGGER.error("Failed to register item: " + field.getName());
			}
        }

		// Create an item group with all items
		PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(MOD_ID,"items"), PolymerItemGroupUtils.builder()
				.icon(() -> new ItemStack(Items.DIAMOND_HAMMER))
				.displayName(Text.of("More Tools"))
				.entries((context, entries) -> {
					entries.add(Items.WOODEN_HAMMER);
					entries.add(Items.STONE_HAMMER);
					entries.add(Items.IRON_HAMMER);
					entries.add(Items.GOLDEN_HAMMER);
					entries.add(Items.DIAMOND_HAMMER);
					entries.add(Items.NETHERITE_HAMMER);
					entries.add(Items.WOODEN_EXCAVATOR);
					entries.add(Items.STONE_EXCAVATOR);
					entries.add(Items.IRON_EXCAVATOR);
					entries.add(Items.GOLDEN_EXCAVATOR);
					entries.add(Items.DIAMOND_EXCAVATOR);
					entries.add(Items.NETHERITE_EXCAVATOR);
					entries.add(Items.WOODEN_SAW);
					entries.add(Items.STONE_SAW);
					entries.add(Items.IRON_SAW);
					entries.add(Items.GOLDEN_SAW);
					entries.add(Items.DIAMOND_SAW);
					entries.add(Items.NETHERITE_SAW);
					entries.add(Items.WOODEN_VEIN_HAMMER);
					entries.add(Items.STONE_VEIN_HAMMER);
					entries.add(Items.IRON_VEIN_HAMMER);
					entries.add(Items.GOLDEN_VEIN_HAMMER);
					entries.add(Items.DIAMOND_VEIN_HAMMER);
					entries.add(Items.NETHERITE_VEIN_HAMMER);
				}).build());

		LOGGER.info("MoreTools loaded!");
	}

	public static class Items {
		public static final Item WOODEN_HAMMER = new HammerToolItem((PickaxeItem) net.minecraft.item.Items.WOODEN_PICKAXE);
		public static final Item STONE_HAMMER = new HammerToolItem((PickaxeItem) net.minecraft.item.Items.STONE_PICKAXE);
		public static final Item IRON_HAMMER = new HammerToolItem((PickaxeItem) net.minecraft.item.Items.IRON_PICKAXE);
		public static final Item GOLDEN_HAMMER = new HammerToolItem((PickaxeItem) net.minecraft.item.Items.GOLDEN_PICKAXE);
		public static final Item DIAMOND_HAMMER = new HammerToolItem((PickaxeItem) net.minecraft.item.Items.DIAMOND_PICKAXE);
		public static final Item NETHERITE_HAMMER = new HammerToolItem((PickaxeItem) net.minecraft.item.Items.NETHERITE_PICKAXE);
		public static final Item WOODEN_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.item.Items.WOODEN_SHOVEL);
		public static final Item STONE_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.item.Items.STONE_SHOVEL);
		public static final Item IRON_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.item.Items.IRON_SHOVEL);
		public static final Item GOLDEN_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.item.Items.GOLDEN_SHOVEL);
		public static final Item DIAMOND_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.item.Items.DIAMOND_SHOVEL);
		public static final Item NETHERITE_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.item.Items.NETHERITE_SHOVEL);
		public static final Item WOODEN_SAW = new SawToolItem((AxeItem) net.minecraft.item.Items.WOODEN_AXE);
		public static final Item STONE_SAW = new SawToolItem((AxeItem) net.minecraft.item.Items.STONE_AXE);
		public static final Item IRON_SAW = new SawToolItem((AxeItem) net.minecraft.item.Items.IRON_AXE);
		public static final Item GOLDEN_SAW = new SawToolItem((AxeItem) net.minecraft.item.Items.GOLDEN_AXE);
		public static final Item DIAMOND_SAW = new SawToolItem((AxeItem) net.minecraft.item.Items.DIAMOND_AXE);
		public static final Item NETHERITE_SAW = new SawToolItem((AxeItem) net.minecraft.item.Items.NETHERITE_AXE);
		public static final Item WOODEN_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.WOODEN_PICKAXE);
		public static final Item STONE_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.STONE_PICKAXE);
		public static final Item IRON_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.IRON_PICKAXE);
		public static final Item GOLDEN_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.GOLDEN_PICKAXE);
		public static final Item DIAMOND_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.DIAMOND_PICKAXE);
		public static final Item NETHERITE_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.NETHERITE_PICKAXE);
	}
}