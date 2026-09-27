package pw.smto.moretools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import pw.smto.moretools.util.BlockBoxUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExcavatorToolItem extends PolyBaseToolItem {

    private static final List<Component> LORE = List.of(Component.translatable("item.moretools.excavator.tooltip").withStyle(ChatFormatting.GOLD));

    protected ExcavatorToolItem(Properties settings, float fastSpeed, float slowSpeed, Item baseItem) {
        super(settings, fastSpeed, slowSpeed, baseItem);
    }

    @Override
    public List<Component> getLore() {
        return ExcavatorToolItem.LORE;
    }

    @Override
    public List<BlockPos> getAffectedArea(@Nullable Level world, BlockPos pos, BlockState state, @Nullable Direction d, @Nullable Block target, boolean sneak) {
        if (sneak) return Collections.emptyList();
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
}
