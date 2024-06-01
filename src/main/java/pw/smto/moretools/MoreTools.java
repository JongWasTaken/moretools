package pw.smto.moretools;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.LoggerFactory;

public class MoreTools implements ModInitializer {
	public static final String MOD_ID = "moretools";

	@Override
	public void onInitialize() {
		PolymerResourcePackUtils.addModAssets(MOD_ID);
		PolymerResourcePackUtils.markAsRequired();

		Registry.register(Registries.ITEM, new Identifier(MOD_ID,"wooden_hammer"), Items.WOODEN_HAMMER);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID,"stone_hammer"), Items.STONE_HAMMER);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID,"iron_hammer"), Items.IRON_HAMMER);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID,"golden_hammer"), Items.GOLDEN_HAMMER);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID,"diamond_hammer"), Items.DIAMOND_HAMMER);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID,"netherite_hammer"), Items.NETHERITE_HAMMER);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID,"wooden_excavator"), Items.WOODEN_EXCAVATOR);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID,"stone_excavator"), Items.STONE_EXCAVATOR);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID,"iron_excavator"), Items.IRON_EXCAVATOR);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID,"golden_excavator"), Items.GOLDEN_EXCAVATOR);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID,"diamond_excavator"), Items.DIAMOND_EXCAVATOR);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID,"netherite_excavator"), Items.NETHERITE_EXCAVATOR);

		Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID,"items"), FabricItemGroup.builder()
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
				}).build());

		Logger.info("MoreTools loaded!");
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
	}

	public static class Logger {
		private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
		public static void info(String s, Object... objects) {
			LOGGER.info(s, objects);
		}
		public static void warn(String s, Object... objects) {
			LOGGER.warn(s, objects);
		}
		public static void error(String s, Object... objects) {
			LOGGER.error(s, objects);
		}
	}
}