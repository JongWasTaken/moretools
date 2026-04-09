package pw.smto.moretools.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import pw.smto.moretools.MoreTools;
import pw.smto.moretools.util.BlockBoxUtils;
import pw.smto.moretools.util.ConfigManager;
import pw.smto.moretools.util.CustomMaterial;
import pw.smto.moretools.util.ToolConfigEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExcavatorToolItem extends BaseToolItem implements PolymerItem, PolymerClientDecoded {
    private final Item baseItem;

    private static final List<Component> LORE = List.of(Component.translatable("item.moretools.excavator.tooltip").withStyle(ChatFormatting.GOLD));

    private static BaseToolSettings createSettings(Item base, ToolMaterial baseMaterial) {
        Identifier id = Identifier.fromNamespaceAndPath(MoreTools.MOD_ID, BuiltInRegistries.ITEM.getKey(base).getPath().replace("shovel", "excavator"));
        var config = ConfigManager.config.get(id.getPath());
        Properties settings = new Properties();
        if (config == null) {
            config = ToolConfigEntry.DEFAULT;
            settings.pickaxe(CustomMaterial.of(baseMaterial).multiplyDurability(3).toVanilla(), Math.max(baseMaterial.attackDamageBonus()-4, 1.0F), -3.0f);
        } else {
            settings.pickaxe(CustomMaterial.of(baseMaterial).multiplyDurability(config.durabilityMultiplier()).toVanilla(), Math.max(baseMaterial.attackDamageBonus() + config.attackDamageModifier(), 1.0F), config.attackSpeed());
        }
        settings.component(DataComponents.LORE, new ItemLore(ExcavatorToolItem.LORE));
        if (baseMaterial.equals(ToolMaterial.NETHERITE)) settings.fireResistant();
        return new BaseToolSettings(id, settings, config);
    }

    public ExcavatorToolItem(ShovelItem base, ToolMaterial baseMaterial) {
        super(ExcavatorToolItem.createSettings(base, baseMaterial), baseMaterial, BlockTags.MINEABLE_WITH_SHOVEL);
        this.baseItem = base;
    }

    @Override
    public List<Component> getLore() {
        return ExcavatorToolItem.LORE;
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
                if (world.getBlockState(blockPos).is(BlockTags.MINEABLE_WITH_SHOVEL)) {
                    list.add(blockPos);
                }
            }
        }
        return list;
    }

    @Override
    public void doToolPower(BlockState state, BlockPos pos, Direction d, ServerPlayer player, Level world) {
        List<BlockPos> selection = this.getAffectedArea(world, pos, state, d, state.getBlock());
        for (BlockPos blockBoxSelectionPos : selection) {
            if (!blockBoxSelectionPos.equals(pos)) {
                player.gameMode.destroyBlock(blockBoxSelectionPos);
            }
        }
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null) return InteractionResult.PASS;
        if (context.getPlayer().isShiftKeyDown()) return this.baseItem.useOn(context);
        InteractionResult res = InteractionResult.PASS;
        for (BlockPos blockPos : this.getAffectedArea(context.getLevel(), context.getClickedPos(), context.getLevel().getBlockState(context.getClickedPos()), context.getClickedFace(), context.getLevel().getBlockState(context.getClickedPos()).getBlock())) {
            var out = this.baseItem.useOn(new UseOnContext(context.getLevel(), context.getPlayer(), context.getHand(), context.getItemInHand(), new BlockHitResult(context.getClickLocation(), context.getClickedFace(), blockPos, context.isInside())));
            if (out == InteractionResult.SUCCESS) res = InteractionResult.SUCCESS;
        }
        return res;
    }
}
