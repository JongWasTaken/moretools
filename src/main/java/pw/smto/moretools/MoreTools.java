package pw.smto.moretools;

import com.mojang.serialization.Codec;
import eu.pb4.polymer.core.api.item.PolymerCreativeModeTabUtils;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import org.geysermc.geyser.api.GeyserApi;
import org.jspecify.annotations.NonNull;
import org.slf4j.LoggerFactory;
import pw.smto.moretools.item.*;
import pw.smto.moretools.util.ConfigManager;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

public class MoreTools implements ModInitializer {
	public static final String MOD_ID = "moretools";
	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MoreTools.MOD_ID);
	public static final HashSet<UUID> PLAYERS_WITH_CLIENT = new HashSet<>();
	@SuppressWarnings("OptionalGetWithoutIsPresent")
    public static final String VERSION = FabricLoader.getInstance().getModContainer(MoreTools.MOD_ID).get().getMetadata().getVersion().toString();

	public static final DataComponentType<Boolean> ACT_AS_BASE_TOOL = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();

	private static Function<UUID, Boolean> bedrockPlayerCheckFunction = _ -> false;

	@Override
	public void onInitialize() {
		ConfigManager.init();

		PolymerResourcePackUtils.addModAssets(MoreTools.MOD_ID);
		PolymerResourcePackUtils.markAsRequired();

		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(MoreTools.MOD_ID, "act_as_base_tool"), MoreTools.ACT_AS_BASE_TOOL);
		PolymerComponent.registerDataComponent(MoreTools.ACT_AS_BASE_TOOL);

		// Register all items
		for (Field field : Items.class.getFields()) {
            try {
				Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(MoreTools.MOD_ID, field.getName().toLowerCase(Locale.ROOT)), (Item)field.get(null));
            } catch (Exception ignored) {
                MoreTools.LOGGER.error("Failed to register item: {}", field.getName());
			}
        }

		// Create an item group with all items
		PolymerCreativeModeTabUtils.registerPolymerCreativeModeTab(Identifier.fromNamespaceAndPath(MoreTools.MOD_ID,"items"), PolymerCreativeModeTabUtils.builder()
				.icon(() -> new ItemStack(Items.DIAMOND_HAMMER))
				.title(Component.nullToEmpty("More Tools"))
				.displayItems((_, entries) -> {
					entries.accept(Items.WOODEN_HAMMER);
					entries.accept(Items.STONE_HAMMER);
					entries.accept(Items.IRON_HAMMER);
					entries.accept(Items.GOLDEN_HAMMER);
					entries.accept(Items.DIAMOND_HAMMER);
					entries.accept(Items.NETHERITE_HAMMER);
					entries.accept(Items.WOODEN_EXCAVATOR);
					entries.accept(Items.STONE_EXCAVATOR);
					entries.accept(Items.IRON_EXCAVATOR);
					entries.accept(Items.GOLDEN_EXCAVATOR);
					entries.accept(Items.DIAMOND_EXCAVATOR);
					entries.accept(Items.NETHERITE_EXCAVATOR);
					entries.accept(Items.WOODEN_SAW);
					entries.accept(Items.STONE_SAW);
					entries.accept(Items.IRON_SAW);
					entries.accept(Items.GOLDEN_SAW);
					entries.accept(Items.DIAMOND_SAW);
					entries.accept(Items.NETHERITE_SAW);
					entries.accept(Items.WOODEN_VEIN_HAMMER);
					entries.accept(Items.STONE_VEIN_HAMMER);
					entries.accept(Items.IRON_VEIN_HAMMER);
					entries.accept(Items.GOLDEN_VEIN_HAMMER);
					entries.accept(Items.DIAMOND_VEIN_HAMMER);
					entries.accept(Items.NETHERITE_VEIN_HAMMER);
					entries.accept(Items.WOODEN_VEIN_EXCAVATOR);
					entries.accept(Items.STONE_VEIN_EXCAVATOR);
					entries.accept(Items.IRON_VEIN_EXCAVATOR);
					entries.accept(Items.GOLDEN_VEIN_EXCAVATOR);
					entries.accept(Items.DIAMOND_VEIN_EXCAVATOR);
					entries.accept(Items.NETHERITE_VEIN_EXCAVATOR);
				}).build());

		// Client compatibility stuff
		ServerPlayConnectionEvents.JOIN.register((ServerGamePacketListenerImpl _, PacketSender sender, MinecraftServer _) -> sender.sendPacket(new Payloads.S2CHandshake(true)));
		ServerPlayConnectionEvents.DISCONNECT.register((ServerGamePacketListenerImpl handler, MinecraftServer _) -> MoreTools.PLAYERS_WITH_CLIENT.remove(handler.player.getUUID()));
		PayloadTypeRegistry.clientboundPlay().register(Payloads.S2CHandshake.ID, Payloads.S2CHandshake.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(Payloads.C2SLegacyHandshakeCallback.ID, Payloads.C2SLegacyHandshakeCallback.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(Payloads.C2SHandshakeCallbackWithVersion.ID, Payloads.C2SHandshakeCallbackWithVersion.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(Payloads.C2SLegacyHandshakeCallback.ID, (_, context) -> context.server().execute(() -> MoreTools.handleClientCallback(context.player(), "1.7.3")));
		ServerPlayNetworking.registerGlobalReceiver(Payloads.C2SHandshakeCallbackWithVersion.ID, (payload, context) -> context.server().execute(() -> MoreTools.handleClientCallback(context.player(), payload.version)));

		ServerPlayConnectionEvents.DISCONNECT.register((ServerGamePacketListenerImpl handler, MinecraftServer _) -> MoreTools.PLAYERS_WITH_CLIENT.remove(handler.player.getUUID()));

		if (FabricLoader.getInstance().isModLoaded("geyser-fabric")) {
			MoreTools.bedrockPlayerCheckFunction = (uuid) -> GeyserApi.api().connectionByUuid(uuid) != null;
		}

        MoreTools.LOGGER.info("MoreTools loaded!");
	}

	private static void handleClientCallback(ServerPlayer player, String version) {
		if (!Objects.equals(version.charAt(3), MoreTools.VERSION.split("\\+")[0].charAt(3))) {
			player.sendSystemMessage(Component.translatable("moretools.client_version_mismatch.1"), false);
			player.sendSystemMessage(Component.translatable("moretools.client_version_mismatch.2"), false);
			player.sendSystemMessage(Component.translatable("moretools.client_version_mismatch.3").append(Component.literal(" " + version).withStyle(ChatFormatting.RED)), false);
			player.sendSystemMessage(Component.translatable("moretools.client_version_mismatch.4").append(Component.literal(" " + MoreTools.VERSION).withStyle(ChatFormatting.GREEN)), false);
			return;
		}
		MoreTools.LOGGER.info("Enabling client-side enhancements for player: {}", Objects.requireNonNull(player.getDisplayName()).getString());
		MoreTools.PLAYERS_WITH_CLIENT.add(player.getUUID());
		player.getInventory().setChanged();
	}

	public static class Items {
		public static final Item WOODEN_HAMMER = new HammerToolItem(net.minecraft.world.item.Items.WOODEN_PICKAXE, ToolMaterial.WOOD);
		public static final Item STONE_HAMMER = new HammerToolItem(net.minecraft.world.item.Items.STONE_PICKAXE, ToolMaterial.STONE);
		public static final Item IRON_HAMMER = new HammerToolItem(net.minecraft.world.item.Items.IRON_PICKAXE, ToolMaterial.IRON);
		public static final Item GOLDEN_HAMMER = new HammerToolItem(net.minecraft.world.item.Items.GOLDEN_PICKAXE, ToolMaterial.GOLD);
		public static final Item DIAMOND_HAMMER = new HammerToolItem(net.minecraft.world.item.Items.DIAMOND_PICKAXE, ToolMaterial.DIAMOND);
		public static final Item NETHERITE_HAMMER = new HammerToolItem(net.minecraft.world.item.Items.NETHERITE_PICKAXE, ToolMaterial.NETHERITE);
		public static final Item WOODEN_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.world.item.Items.WOODEN_SHOVEL, ToolMaterial.WOOD);
		public static final Item STONE_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.world.item.Items.STONE_SHOVEL, ToolMaterial.STONE);
		public static final Item IRON_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.world.item.Items.IRON_SHOVEL, ToolMaterial.IRON);
		public static final Item GOLDEN_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.world.item.Items.GOLDEN_SHOVEL, ToolMaterial.GOLD);
		public static final Item DIAMOND_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.world.item.Items.DIAMOND_SHOVEL, ToolMaterial.DIAMOND);
		public static final Item NETHERITE_EXCAVATOR = new ExcavatorToolItem((ShovelItem) net.minecraft.world.item.Items.NETHERITE_SHOVEL, ToolMaterial.NETHERITE);
		public static final Item WOODEN_SAW = new SawToolItem((AxeItem) net.minecraft.world.item.Items.WOODEN_AXE, ToolMaterial.WOOD);
		public static final Item STONE_SAW = new SawToolItem((AxeItem) net.minecraft.world.item.Items.STONE_AXE, ToolMaterial.STONE);
		public static final Item IRON_SAW = new SawToolItem((AxeItem) net.minecraft.world.item.Items.IRON_AXE, ToolMaterial.IRON);
		public static final Item GOLDEN_SAW = new SawToolItem((AxeItem) net.minecraft.world.item.Items.GOLDEN_AXE, ToolMaterial.GOLD);
		public static final Item DIAMOND_SAW = new SawToolItem((AxeItem) net.minecraft.world.item.Items.DIAMOND_AXE, ToolMaterial.DIAMOND);
		public static final Item NETHERITE_SAW = new SawToolItem((AxeItem) net.minecraft.world.item.Items.NETHERITE_AXE, ToolMaterial.NETHERITE);
		public static final Item WOODEN_VEIN_HAMMER = new VeinHammerToolItem(net.minecraft.world.item.Items.WOODEN_PICKAXE, ToolMaterial.WOOD);
		public static final Item STONE_VEIN_HAMMER = new VeinHammerToolItem(net.minecraft.world.item.Items.STONE_PICKAXE, ToolMaterial.STONE, 4);
		public static final Item IRON_VEIN_HAMMER = new VeinHammerToolItem(net.minecraft.world.item.Items.IRON_PICKAXE, ToolMaterial.IRON, 5);
		public static final Item GOLDEN_VEIN_HAMMER = new VeinHammerToolItem(net.minecraft.world.item.Items.GOLDEN_PICKAXE, ToolMaterial.GOLD, 6);
		public static final Item DIAMOND_VEIN_HAMMER = new VeinHammerToolItem(net.minecraft.world.item.Items.DIAMOND_PICKAXE, ToolMaterial.DIAMOND, 6);
		public static final Item NETHERITE_VEIN_HAMMER = new VeinHammerToolItem(net.minecraft.world.item.Items.NETHERITE_PICKAXE, ToolMaterial.NETHERITE, 7);
		public static final Item WOODEN_VEIN_EXCAVATOR = new VeinExcavatorToolItem((ShovelItem) net.minecraft.world.item.Items.WOODEN_SHOVEL, ToolMaterial.WOOD);
		public static final Item STONE_VEIN_EXCAVATOR = new VeinExcavatorToolItem((ShovelItem) net.minecraft.world.item.Items.STONE_SHOVEL, ToolMaterial.STONE, 4);
		public static final Item IRON_VEIN_EXCAVATOR = new VeinExcavatorToolItem((ShovelItem) net.minecraft.world.item.Items.IRON_SHOVEL, ToolMaterial.IRON, 5);
		public static final Item GOLDEN_VEIN_EXCAVATOR = new VeinExcavatorToolItem((ShovelItem) net.minecraft.world.item.Items.GOLDEN_SHOVEL, ToolMaterial.GOLD, 6);
		public static final Item DIAMOND_VEIN_EXCAVATOR = new VeinExcavatorToolItem((ShovelItem) net.minecraft.world.item.Items.DIAMOND_SHOVEL, ToolMaterial.DIAMOND, 6);
		public static final Item NETHERITE_VEIN_EXCAVATOR = new VeinExcavatorToolItem((ShovelItem) net.minecraft.world.item.Items.NETHERITE_SHOVEL, ToolMaterial.NETHERITE, 7);

	}

	public static class BlockTags {
		public static final TagKey<Block> SAW_MINEABLE = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MoreTools.MOD_ID, "saw_mineable"));
		public static final TagKey<Block> SAW_APPLICABLE = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MoreTools.MOD_ID, "saw_applicable"));
		public static final TagKey<Block> VEIN_HAMMER_APPLICABLE = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MoreTools.MOD_ID, "vein_hammer_applicable"));
		public static final TagKey<Block> VEIN_EXCAVATOR_APPLICABLE = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MoreTools.MOD_ID, "vein_excavator_applicable"));
	}

	public static class Payloads {
		public record S2CHandshake(boolean ignored) implements CustomPacketPayload {
			public static final CustomPacketPayload.Type<S2CHandshake> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MoreTools.MOD_ID, "s2c_handshake"));
			public static final StreamCodec<RegistryFriendlyByteBuf, S2CHandshake> CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, S2CHandshake::ignored, S2CHandshake::new);
			@Override
			public @NonNull Type<? extends CustomPacketPayload> type() {
				return S2CHandshake.ID;
			}
		}
		public record C2SLegacyHandshakeCallback(boolean ignored) implements CustomPacketPayload {
			public static final CustomPacketPayload.Type<C2SLegacyHandshakeCallback> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MoreTools.MOD_ID, "c2s_handshake_callback"));
			public static final StreamCodec<RegistryFriendlyByteBuf, C2SLegacyHandshakeCallback> CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, C2SLegacyHandshakeCallback::ignored, C2SLegacyHandshakeCallback::new);
			@Override
			public @NonNull Type<? extends CustomPacketPayload> type() {
				return C2SLegacyHandshakeCallback.ID;
			}
		}
		public record C2SHandshakeCallbackWithVersion(String version) implements CustomPacketPayload {
			public static final CustomPacketPayload.Type<C2SHandshakeCallbackWithVersion> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MoreTools.MOD_ID, "c2s_handshake_callback_with_version"));
			public static final StreamCodec<RegistryFriendlyByteBuf, C2SHandshakeCallbackWithVersion> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, C2SHandshakeCallbackWithVersion::version, C2SHandshakeCallbackWithVersion::new);
			@Override
			public @NonNull Type<? extends CustomPacketPayload> type() {
				return C2SHandshakeCallbackWithVersion.ID;
			}
		}
	}

	public static boolean isBedrockPlayer(Player player) {
		return MoreTools.bedrockPlayerCheckFunction.apply(player.getUUID());
	}
}