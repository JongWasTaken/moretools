package pw.smto.moretools.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pw.smto.moretools.MoreTools;
import pw.smto.moretools.util.BlockBoxUtils;
import pw.smto.moretools.util.CustomMaterial;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.List;

public class SawToolItem extends BaseToolItem implements PolymerItem, PolymerKeepModel, PolymerClientDecoded {
    private final Item baseItem;

    private static Settings createSettings(ToolMaterial baseMaterial) {
        var settings = new Settings()
                .axe(CustomMaterial.of(baseMaterial).multiplyDurability(3).toVanilla(), Math.max(baseMaterial.attackDamageBonus()-4, 1.0F), -3.0f)
                .component(DataComponentTypes.LORE, new LoreComponent(List.of(Text.translatable("item.moretools.saw.tooltip").formatted(Formatting.GOLD), Text.translatable("item.moretools.saw.tooltip.2").formatted(Formatting.GOLD))));
        if (baseMaterial.equals(ToolMaterial.NETHERITE)) settings.fireproof();
        return settings;
    }

    public SawToolItem(AxeItem base, ToolMaterial baseMaterial) {
        super(base, SawToolItem.createSettings(baseMaterial), Identifier.of(MoreTools.MOD_ID, Registries.ITEM.getId(base).getPath().replace("axe", "saw")), baseMaterial, MoreTools.BlockTags.SAW_MINEABLE);
        this.baseItem = base;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        if (MoreTools.PLAYERS_WITH_CLIENT.contains(context.getPlayer())) {
            return this;
        }
        return this.baseItem;
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return super.id;
    }

    @Override
    public List<BlockPos> getAffectedArea(World world, BlockPos pos, BlockState state, @Nullable Direction d, @Nullable Block target) {
        var list = new ArrayList<BlockPos>();
        if (world == null) return list;
        if (!state.isIn(MoreTools.BlockTags.SAW_APPLICABLE)) return list;
        list.addAll(BlockBoxUtils.getBlockCluster(target, pos, world, 30, BlockBoxUtils.DirectionSets.DOWN_RESTRICTED_EXTENDED));
        return list;
    }

    @Override
    public void doToolPower(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world) {
        int damage = 0;
        int maxDamage = Math.abs(player.getMainHandStack().getMaxDamage() - player.getMainHandStack().getDamage());
        for (BlockPos blockPos : this.getAffectedArea(world, pos, state, d, state.getBlock())) {
            if (damage >= maxDamage-1) break;
            player.interactionManager.tryBreakBlock(blockPos);
            damage++;
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer() == null) return ActionResult.PASS;
        if (context.getPlayer().isSneaking()) return this.baseItem.useOnBlock(context);
        for (BlockPos blockPos : this.getAffectedArea(context.getWorld(), context.getBlockPos(), context.getWorld().getBlockState(context.getBlockPos()), context.getSide(), context.getWorld().getBlockState(context.getBlockPos()).getBlock())) {
            this.baseItem.useOnBlock(new ItemUsageContext(context.getWorld(), context.getPlayer(), context.getHand(), context.getStack(), new BlockHitResult(context.getHitPos(), context.getSide(), blockPos, context.hitsInsideBlock())));
        }
        return this.baseItem.useOnBlock(context);
    }
}
