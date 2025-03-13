package pw.smto.moretools;

import com.mojang.serialization.Codec;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
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
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.LoggerFactory;
import pw.smto.moretools.item.*;

import java.lang.reflect.Field;
import java.util.*;

public class MoreTools implements ModInitializer {
	public static final String MOD_ID = "moretools";
	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MoreTools.MOD_ID);
	public static final List<ServerPlayerEntity> PLAYERS_WITH_CLIENT = new ArrayList<>();
	@SuppressWarnings("OptionalGetWithoutIsPresent")
    public static final String VERSION = FabricLoader.getInstance().getModContainer(MoreTools.MOD_ID).get().getMetadata().getVersion().toString();

	public static final ComponentType<Boolean> ACT_AS_BASE_TOOL = ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(PacketCodecs.BOOLEAN).build();

	@Override
	public void onInitialize() {
		PolymerResourcePackUtils.addModAssets(MoreTools.MOD_ID);
		PolymerResourcePackUtils.markAsRequired();

		Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(MoreTools.MOD_ID, "act_as_base_tool"), MoreTools.ACT_AS_BASE_TOOL);
		PolymerComponent.registerDataComponent(MoreTools.ACT_AS_BASE_TOOL);

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
					entries.add(Items.WOODEN_VEIN_EXCAVATOR);
					entries.add(Items.STONE_VEIN_EXCAVATOR);
					entries.add(Items.IRON_VEIN_EXCAVATOR);
					entries.add(Items.GOLDEN_VEIN_EXCAVATOR);
					entries.add(Items.DIAMOND_VEIN_EXCAVATOR);
					entries.add(Items.NETHERITE_VEIN_EXCAVATOR);
				}).build());

		// Client compatibility stuff
		ServerPlayConnectionEvents.JOIN.register((ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) -> sender.sendPacket(new Payloads.S2CHandshake(true)));
		PayloadTypeRegistry.playS2C().register(Payloads.S2CHandshake.ID, Payloads.S2CHandshake.CODEC);
		PayloadTypeRegistry.playC2S().register(Payloads.C2SHandshakeCallback.ID, Payloads.C2SHandshakeCallback.CODEC);
		PayloadTypeRegistry.playC2S().register(Payloads.C2SHandshakeCallbackWithVersion.ID, Payloads.C2SHandshakeCallbackWithVersion.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(Payloads.C2SHandshakeCallback.ID, (payload, context) -> {
			// Why isn't context.server() available here? Maybe I'm tweaking, but I feel like that was a thing?
			if (context.player() == null) return;
			context.player().server.execute(() -> MoreTools.handleClientCallback(context.player(), "1.7.3"));
		});
		ServerPlayNetworking.registerGlobalReceiver(Payloads.C2SHandshakeCallbackWithVersion.ID, (payload, context) -> {
			if (context.player() == null) return;
			context.player().server.execute(() -> MoreTools.handleClientCallback(context.player(), payload.version));
		});

		ServerPlayConnectionEvents.DISCONNECT.register((ServerPlayNetworkHandler handler, MinecraftServer server) -> MoreTools.PLAYERS_WITH_CLIENT.remove(handler.player));

		//net.minecraft.registry.tag.BlockTags.SOUL
        MoreTools.LOGGER.info("MoreTools loaded!");
	}

	private static void handleClientCallback(ServerPlayerEntity player, String version) {
		if (!Objects.equals(version.charAt(3), MoreTools.VERSION.split("\\+")[0].charAt(3))) {
			player.sendMessage(Text.translatable("moretools.client_version_mismatch.1"), false);
			player.sendMessage(Text.translatable("moretools.client_version_mismatch.2"), false);
			player.sendMessage(Text.translatable("moretools.client_version_mismatch.3").append(Text.literal(" " + version).formatted(Formatting.RED)), false);
			player.sendMessage(Text.translatable("moretools.client_version_mismatch.4").append(Text.literal(" " + MoreTools.VERSION).formatted(Formatting.GREEN)), false);
			return;
		}
		MoreTools.LOGGER.info("Enabling client-side enhancements for player: {}", Objects.requireNonNull(player.getDisplayName()).getString());
		MoreTools.PLAYERS_WITH_CLIENT.add(player);
		player.getInventory().markDirty();
	}

	public static class Items {
		public static final Item WOODEN_HAMMER = new HammerToolItem((PickaxeItem) net.minecraft.item.Items.WOODEN_PICKAXE, ToolMaterial.WOOD);
		public static final Item STONE_HAMMER = new HammerToolItem((PickaxeItem) net.minecraft.item.Items.STONE_PICKAXE, ToolMaterial.STONE);
		public static final Item IRON_HAMMER = new HammerToolItem((PickaxeItem) net.minecraft.item.Items.IRON_PICKAXE, ToolMaterial.IRON);
		public static final Item GOLDEN_HAMMER = new HammerToolItem((PickaxeItem) net.minecraft.item.Items.GOLDEN_PICKAXE, ToolMaterial.GOLD);
		public static final Item DIAMOND_HAMMER = new HammerToolItem((PickaxeItem) net.minecraft.item.Items.DIAMOND_PICKAXE, ToolMaterial.DIAMOND);
		public static final Item NETHERITE_HAMMER = new HammerToolItem((PickaxeItem) net.minecraft.item.Items.NETHERITE_PICKAXE, ToolMaterial.NETHERITE);
		public static final Item WOODEN_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.item.Items.WOODEN_SHOVEL, ToolMaterial.WOOD);
		public static final Item STONE_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.item.Items.STONE_SHOVEL, ToolMaterial.STONE);
		public static final Item IRON_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.item.Items.IRON_SHOVEL, ToolMaterial.IRON);
		public static final Item GOLDEN_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.item.Items.GOLDEN_SHOVEL, ToolMaterial.GOLD);
		public static final Item DIAMOND_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.item.Items.DIAMOND_SHOVEL, ToolMaterial.DIAMOND);
		public static final Item NETHERITE_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.item.Items.NETHERITE_SHOVEL, ToolMaterial.NETHERITE);
		public static final Item WOODEN_SAW = new SawToolItem((AxeItem) net.minecraft.item.Items.WOODEN_AXE, ToolMaterial.WOOD);
		public static final Item STONE_SAW = new SawToolItem((AxeItem) net.minecraft.item.Items.STONE_AXE, ToolMaterial.STONE);
		public static final Item IRON_SAW = new SawToolItem((AxeItem) net.minecraft.item.Items.IRON_AXE, ToolMaterial.IRON);
		public static final Item GOLDEN_SAW = new SawToolItem((AxeItem) net.minecraft.item.Items.GOLDEN_AXE, ToolMaterial.GOLD);
		public static final Item DIAMOND_SAW = new SawToolItem((AxeItem) net.minecraft.item.Items.DIAMOND_AXE, ToolMaterial.DIAMOND);
		public static final Item NETHERITE_SAW = new SawToolItem((AxeItem) net.minecraft.item.Items.NETHERITE_AXE, ToolMaterial.NETHERITE);
		public static final Item WOODEN_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.WOODEN_PICKAXE, ToolMaterial.WOOD);
		public static final Item STONE_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.STONE_PICKAXE, ToolMaterial.STONE, 4);
		public static final Item IRON_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.IRON_PICKAXE, ToolMaterial.IRON, 5);
		public static final Item GOLDEN_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.GOLDEN_PICKAXE, ToolMaterial.GOLD, 6);
		public static final Item DIAMOND_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.DIAMOND_PICKAXE, ToolMaterial.DIAMOND, 6);
		public static final Item NETHERITE_VEIN_HAMMER = new VeinHammerToolItem((PickaxeItem) net.minecraft.item.Items.NETHERITE_PICKAXE, ToolMaterial.NETHERITE, 7);
		public static final Item WOODEN_VEIN_EXCAVATOR = new VeinExcavatorToolItem((ShovelItem) net.minecraft.item.Items.WOODEN_SHOVEL, ToolMaterial.WOOD);
		public static final Item STONE_VEIN_EXCAVATOR = new VeinExcavatorToolItem((ShovelItem) net.minecraft.item.Items.STONE_SHOVEL, ToolMaterial.STONE, 4);
		public static final Item IRON_VEIN_EXCAVATOR = new VeinExcavatorToolItem((ShovelItem) net.minecraft.item.Items.IRON_SHOVEL, ToolMaterial.IRON, 5);
		public static final Item GOLDEN_VEIN_EXCAVATOR = new VeinExcavatorToolItem((ShovelItem) net.minecraft.item.Items.GOLDEN_SHOVEL, ToolMaterial.GOLD, 6);
		public static final Item DIAMOND_VEIN_EXCAVATOR = new VeinExcavatorToolItem((ShovelItem) net.minecraft.item.Items.DIAMOND_SHOVEL, ToolMaterial.DIAMOND, 6);
		public static final Item NETHERITE_VEIN_EXCAVATOR = new VeinExcavatorToolItem((ShovelItem) net.minecraft.item.Items.NETHERITE_SHOVEL, ToolMaterial.NETHERITE, 7);

	}

	public static class BlockTags {
		public static final TagKey<Block> SAW_MINEABLE = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MoreTools.MOD_ID, "saw_mineable"));
		public static final TagKey<Block> SAW_APPLICABLE = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MoreTools.MOD_ID, "saw_applicable"));
		public static final TagKey<Block> VEIN_HAMMER_APPLICABLE = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MoreTools.MOD_ID, "vein_hammer_applicable"));
		public static final TagKey<Block> VEIN_EXCAVATOR_APPLICABLE = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MoreTools.MOD_ID, "vein_excavator_applicable"));
	}

	public static class Payloads {
		public record S2CHandshake(boolean ignored) implements CustomPayload {
			public static final CustomPayload.Id<S2CHandshake> ID = new CustomPayload.Id<>(Identifier.of(MoreTools.MOD_ID, "s2c_handshake"));
			public static final PacketCodec<RegistryByteBuf, S2CHandshake> CODEC = PacketCodec.tuple(PacketCodecs.BOOLEAN, S2CHandshake::ignored, S2CHandshake::new);
			@Override
			public Id<? extends CustomPayload> getId() {
				return S2CHandshake.ID;
			}
		}
		public record C2SHandshakeCallback(boolean ignored) implements CustomPayload {
			public static final CustomPayload.Id<C2SHandshakeCallback> ID = new CustomPayload.Id<>(Identifier.of(MoreTools.MOD_ID, "c2s_handshake_callback"));
			public static final PacketCodec<RegistryByteBuf, C2SHandshakeCallback> CODEC = PacketCodec.tuple(PacketCodecs.BOOLEAN, C2SHandshakeCallback::ignored, C2SHandshakeCallback::new);
			@Override
			public Id<? extends CustomPayload> getId() {
				return C2SHandshakeCallback.ID;
			}
		}
		public record C2SHandshakeCallbackWithVersion(String version) implements CustomPayload {
			public static final CustomPayload.Id<C2SHandshakeCallbackWithVersion> ID = new CustomPayload.Id<>(Identifier.of(MoreTools.MOD_ID, "c2s_handshake_callback_with_version"));
			public static final PacketCodec<RegistryByteBuf, C2SHandshakeCallbackWithVersion> CODEC = PacketCodec.tuple(PacketCodecs.STRING, C2SHandshakeCallbackWithVersion::version, C2SHandshakeCallbackWithVersion::new);
			@Override
			public Id<? extends CustomPayload> getId() {
				return C2SHandshakeCallbackWithVersion.ID;
			}
		}
	}
}