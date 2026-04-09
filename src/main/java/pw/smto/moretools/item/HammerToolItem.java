package pw.smto.moretools.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;
import pw.smto.moretools.MoreTools;
import pw.smto.moretools.util.BlockBoxUtils;
import pw.smto.moretools.util.ConfigManager;
import pw.smto.moretools.util.CustomMaterial;
import pw.smto.moretools.util.ToolConfigEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class HammerToolItem extends BaseToolItem implements PolymerItem, PolymerClientDecoded {
    private final Item baseItem;

    private static final List<Component> LORE = List.of(Component.translatable("item.moretools.hammer.tooltip").withStyle(ChatFormatting.GOLD));

    private static BaseToolSettings createSettings(Item base, ToolMaterial baseMaterial) {
        Identifier id = Identifier.fromNamespaceAndPath(MoreTools.MOD_ID, BuiltInRegistries.ITEM.getKey(base).getPath().replace("pickaxe", "hammer"));
        var config = ConfigManager.config.get(id.getPath());
        Properties settings = new Properties();
        if (config == null) {
            config = ToolConfigEntry.DEFAULT;
            settings.pickaxe(CustomMaterial.of(baseMaterial).multiplyDurability(3).toVanilla(), Math.max(baseMaterial.attackDamageBonus()-4, 1.0F), -3.0f);
        } else {
            settings.pickaxe(CustomMaterial.of(baseMaterial).multiplyDurability(config.durabilityMultiplier()).toVanilla(), Math.max(baseMaterial.attackDamageBonus() + config.attackDamageModifier(), 1.0F), config.attackSpeed());
        }
        settings.component(DataComponents.LORE, new ItemLore(HammerToolItem.LORE));
        if (baseMaterial.equals(ToolMaterial.NETHERITE)) settings.fireResistant();
        return new BaseToolSettings(id, settings, config);
    }

    public HammerToolItem(Item base, ToolMaterial baseMaterial) {
        super(HammerToolItem.createSettings(base, baseMaterial), baseMaterial, BlockTags.MINEABLE_WITH_PICKAXE);
        this.baseItem = base;
    }

    @Override
    public List<Component> getLore() {
        return HammerToolItem.LORE;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        if (MoreTools.PLAYERS_WITH_CLIENT.contains(Objects.requireNonNull(context.get(PacketContext.GAME_PROFILE)).id())) {
            return this;
        }
        return this.baseItem;
    }

    @Override
    public @org.jspecify.annotations.Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        return this.id;
    }

    @Override
    public List<BlockPos> getAffectedArea(@Nullable Level world, BlockPos pos, BlockState state, @Nullable Direction d, @Nullable Block target) {
        var list = new ArrayList<BlockPos>();
        List<BlockPos> result = new ArrayList<>();
        if (d != null) result = BlockBoxUtils.getSurroundingBlocks(pos, d, 1).toList();
        if (world != null) {
            for (BlockPos blockPos : result) {
                if (world.getBlockState(blockPos).is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                    if (!(world.getBlockState(blockPos).getDestroySpeed(world, blockPos) > state.getDestroySpeed(world, pos))) list.add(blockPos);
                }
            }
        }
        return list;
    }

    public void doToolPower(BlockState state, BlockPos pos, Direction d, ServerPlayer player, Level world) {
        List<BlockPos> selection = this.getAffectedArea(world, pos, state, d, state.getBlock());
        for (BlockPos blockBoxSelectionPos : selection) {
            if (!blockBoxSelectionPos.equals(pos)) {
                player.gameMode.destroyBlock(blockBoxSelectionPos);
            }
        }
    }
}
