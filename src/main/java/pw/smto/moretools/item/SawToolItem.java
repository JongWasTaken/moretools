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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

public class SawToolItem extends BaseToolItem implements PolymerItem, PolymerClientDecoded {
    private final Item baseItem;

    private static final List<Component> LORE = List.of(Component.translatable("item.moretools.saw.tooltip").withStyle(ChatFormatting.GOLD), Component.translatable("item.moretools.saw.tooltip.2").withStyle(ChatFormatting.GOLD));

    private static BaseToolSettings createSettings(Item base, ToolMaterial baseMaterial) {
        Identifier id = Identifier.fromNamespaceAndPath(MoreTools.MOD_ID, BuiltInRegistries.ITEM.getKey(base).getPath().replace("axe", "saw"));
        var config = ConfigManager.config.get(id.getPath());
        Properties settings = new Properties();
        if (config == null) {
            config = ToolConfigEntry.DEFAULT;
            settings.axe(CustomMaterial.of(baseMaterial).multiplyDurability(3).toVanilla(), Math.max(baseMaterial.attackDamageBonus()-4, 1.0F), -3.0f);
        } else {
            settings.axe(CustomMaterial.of(baseMaterial).multiplyDurability(config.durabilityMultiplier()).toVanilla(), Math.max(baseMaterial.attackDamageBonus() + config.attackDamageModifier(), 1.0F), config.attackSpeed());
        }
        settings.component(DataComponents.LORE, new ItemLore(SawToolItem.LORE));
        if (baseMaterial.equals(ToolMaterial.NETHERITE)) settings.fireResistant();
        return new BaseToolSettings(id, settings, config);
    }

    public SawToolItem(AxeItem base, ToolMaterial baseMaterial) {
        super(SawToolItem.createSettings(base, baseMaterial), baseMaterial, MoreTools.BlockTags.SAW_MINEABLE);
        this.baseItem = base;
    }

    @Override
    public List<Component> getLore() {
        return SawToolItem.LORE;
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
    public List<BlockPos> getAffectedArea(Level world, BlockPos pos, BlockState state, @Nullable Direction d, @Nullable Block target) {
        var list = new ArrayList<BlockPos>();
        if (world == null) return list;
        if (!state.is(MoreTools.BlockTags.SAW_APPLICABLE)) return list;
        list.addAll(BlockBoxUtils.getBlockCluster(target, pos, world, 30, BlockBoxUtils.DirectionSets.DOWN_RESTRICTED_EXTENDED));
        return list;
    }

    @Override
    public void doToolPower(BlockState state, BlockPos pos, Direction d, ServerPlayer player, Level world) {
        int damage = 0;
        int maxDamage = Math.abs(player.getMainHandItem().getMaxDamage() - player.getMainHandItem().getDamageValue());
        for (BlockPos blockPos : this.getAffectedArea(world, pos, state, d, state.getBlock())) {
            if (damage >= maxDamage-1) break;
            player.gameMode.destroyBlock(blockPos);
            damage++;
        }
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null) return InteractionResult.PASS;
        if (context.getPlayer().isShiftKeyDown()) return this.baseItem.useOn(context);
        for (BlockPos blockPos : this.getAffectedArea(context.getLevel(), context.getClickedPos(), context.getLevel().getBlockState(context.getClickedPos()), context.getClickedFace(), context.getLevel().getBlockState(context.getClickedPos()).getBlock())) {
            this.baseItem.useOn(new UseOnContext(context.getLevel(), context.getPlayer(), context.getHand(), context.getItemInHand(), new BlockHitResult(context.getClickLocation(), context.getClickedFace(), blockPos, context.isInside())));
        }
        return this.baseItem.useOn(context);
    }
}
