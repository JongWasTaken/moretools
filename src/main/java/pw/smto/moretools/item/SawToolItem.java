package pw.smto.moretools.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pw.smto.moretools.MoreTools;
import pw.smto.moretools.util.BlockBoxUtils;
import pw.smto.moretools.util.MutableMaterial;

import java.util.ArrayList;
import java.util.List;

public class SawToolItem extends BaseToolItem implements PolymerItem, PolymerKeepModel, PolymerClientDecoded {
    private final PolymerModelData model;

    public SawToolItem(AxeItem base) {
        super(base, BlockTags.AXE_MINEABLE);
        this.model = PolymerResourcePackUtils.requestModel(base, Identifier.of(MoreTools.MOD_ID,
                "item/" + Registries.ITEM.getId(base).getPath().replace("axe", "saw")));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        if (MoreTools.PLAYERS_WITH_CLIENT.contains(player)) {
            return this;
        }
        return this.model.item();
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.model.value();
    }

    @Override
    public String getGimmickText() {
        return "Breaks as many logs as possible, but only upwards.";
    }

    @Override
    public List<BlockPos> getAffectedArea(@Nullable World world, BlockPos pos, @Nullable Direction d, @Nullable Block target) {
        var list = new ArrayList<BlockPos>() {{ add(pos); }};

        BlockPos.Mutable up = pos.mutableCopy();
        int limit = 0;
        while (world.getBlockState(up.move(Direction.UP)).isIn(BlockTags.LOGS) && limit < 127) {
            list.add(up.toImmutable());
            limit++;
        }

        return list;
    }

    @Override
    public void doToolPower(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world) {
        if (state.isIn(BlockTags.LOGS)) {
            int damage = 1;

            final int maxDamage = Math.abs(player.getMainHandStack().getMaxDamage() - player.getMainHandStack().getDamage());

            for (BlockPos blockPos : getAffectedArea(world, pos, d, state.getBlock())) {
                if (damage >= maxDamage-1) break;
                world.breakBlock(blockPos, !player.isCreative());
                damage++;
            }

            if (!player.isCreative()) {
                if (player.getActiveHand() == Hand.MAIN_HAND) {
                    player.getMainHandStack().damage(damage, player, EquipmentSlot.MAINHAND);
                }
                else {
                    player.getMainHandStack().damage(damage, player, EquipmentSlot.OFFHAND);
                }
            }
        }
    }
}
