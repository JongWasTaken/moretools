package pw.smto.moretools;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.LoggerFactory;
import pw.smto.moretools.item.ExcavatorToolItem;
import pw.smto.moretools.item.HammerToolItem;
import pw.smto.moretools.item.SawToolItem;
import pw.smto.moretools.item.VeinHammerToolItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MoreTools implements ModInitializer {
	public static final String MOD_ID = "moretools";
	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MoreTools.MOD_ID);
	public static final List<ServerPlayerEntity> PLAYERS_WITH_CLIENT = new ArrayList<>();

	@Override
	public void onInitialize() {
		PolymerResourcePackUtils.addModAssets(MoreTools.MOD_ID);
		PolymerResourcePackUtils.markAsRequired();

		// Register all items
		for (Field field : Items.class.getFields()) {
            try {
				Registry.register(Registries.ITEM, Identifier.of(MoreTools.MOD_ID, field.getName().toLowerCase(Locale.ROOT)), (Item)field.get(null));
            } catch (Exception ignored) {
                MoreTools.LOGGER.error("Failed to register item: {}", field.getName());
			}
        }

		// Create an item group with all items
		PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(MoreTools.MOD_ID,"items"), PolymerItemGroupUtils.builder()
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

		// Client compatibility stuff
		ServerPlayConnectionEvents.JOIN.register((ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) -> sender.sendPacket(new Payloads.S2CHandshake(true)));
		PayloadTypeRegistry.playS2C().register(Payloads.S2CHandshake.ID, Payloads.S2CHandshake.CODEC);
		PayloadTypeRegistry.playC2S().register(Payloads.C2SHandshakeCallback.ID, Payloads.C2SHandshakeCallback.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(Payloads.C2SHandshakeCallback.ID, (payload, context) -> {
			// Why isn't context.server() available here? Maybe I'm tweaking, but I feel like that was a thing?
			if (context.player() == null) return;
			context.player().server.execute(() -> {
                MoreTools.LOGGER.info("Enabling client-side enhancements for player: {}", Objects.requireNonNull(context.player().getDisplayName()).getString());
                MoreTools.PLAYERS_WITH_CLIENT.add(context.player());
				context.player().getInventory().markDirty();
			});
		});

		ServerPlayConnectionEvents.DISCONNECT.register((ServerPlayNetworkHandler handler, MinecraftServer server) -> MoreTools.PLAYERS_WITH_CLIENT.remove(handler.player));

        MoreTools.LOGGER.info("MoreTools loaded!");
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
		public static final Item STONE_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.STONE_PICKAXE, 4);
		public static final Item IRON_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.IRON_PICKAXE, 5);
		public static final Item GOLDEN_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.GOLDEN_PICKAXE, 6);
		public static final Item DIAMOND_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.DIAMOND_PICKAXE, 6);
		public static final Item NETHERITE_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.NETHERITE_PICKAXE, 7);
	}

	public static class BlockTags {
		public static final TagKey<Block> SAW_MINEABLE = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MoreTools.MOD_ID, "saw_mineable"));
		public static final TagKey<Block> VEIN_HAMMER_APPLICABLE = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MoreTools.MOD_ID, "vein_hammer_applicable"));
	}

	public static class Payloads {
		public record S2CHandshake(boolean ignored) implements CustomPayload {
			public static final CustomPayload.Id<S2CHandshake> ID = new CustomPayload.Id<>(Identifier.of(MoreTools.MOD_ID, "s2c_handshake"));
			public static final PacketCodec<RegistryByteBuf, S2CHandshake> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, S2CHandshake::ignored, S2CHandshake::new);
			@Override
			public Id<? extends CustomPayload> getId() {
				return S2CHandshake.ID;
			}
		}
		public record C2SHandshakeCallback(boolean ignored) implements CustomPayload {
			public static final CustomPayload.Id<C2SHandshakeCallback> ID = new CustomPayload.Id<>(Identifier.of(MoreTools.MOD_ID, "c2s_handshake_callback"));
			public static final PacketCodec<RegistryByteBuf, C2SHandshakeCallback> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, C2SHandshakeCallback::ignored, C2SHandshakeCallback::new);
			@Override
			public Id<? extends CustomPayload> getId() {
				return C2SHandshakeCallback.ID;
			}
		}
	}
}