package pw.smto.moretools.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
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
import pw.smto.moretools.util.MutableMaterial;

import java.util.List;

public class SawToolItem extends BaseToolItem implements PolymerItem {
    private final PolymerModelData model;

    public SawToolItem(AxeItem base) {
        super(base, BlockTags.AXE_MINEABLE);
        this.model = PolymerResourcePackUtils.requestModel(base, Identifier.of(MoreTools.MOD_ID,
                "item/" + Registries.ITEM.getId(base).getPath().replace("axe", "saw")));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
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
    public void doToolPower(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world) {
        if (state.isIn(BlockTags.LOGS)) {
            int damage = 1;

            final int maxDamage = Math.abs(player.getMainHandStack().getMaxDamage() - player.getMainHandStack().getDamage());

            BlockPos.Mutable up = pos.mutableCopy();
            while (world.getBlockState(up.move(Direction.UP)).isIn(BlockTags.LOGS) && damage < maxDamage) {
                world.breakBlock(up, !player.isCreative());
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
