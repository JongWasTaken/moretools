package pw.smto.moretools.item;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import pw.smto.moretools.MoreTools;
import pw.smto.moretools.util.BlockBoxUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class VeinHammerToolItem extends PolyBaseToolItem {
    private final int range;

    private static final List<Component> LORE = List.of(Component.translatable("item.moretools.vein_hammer.tooltip").withStyle(ChatFormatting.GOLD));

    protected VeinHammerToolItem(Properties settings, float fastSpeed, float slowSpeed, Item baseItem) {
        this(settings, fastSpeed, slowSpeed, baseItem, 3);
    }

    protected VeinHammerToolItem(Properties settings, float fastSpeed, float slowSpeed, Item baseItem, int range) {
        super(settings, fastSpeed, slowSpeed, baseItem);
        this.range = range;
    }

    @Override
    public List<Component> getLore() {
        return VeinHammerToolItem.LORE;
    }

    @Override
    public List<BlockPos> getAffectedArea(@Nullable Level world, BlockPos pos, BlockState state, @Nullable Direction d, @Nullable Block target, boolean sneak) {
        if (sneak) return Collections.emptyList();
        var list = new ArrayList<BlockPos>();
        BlockState targetState = null;
        if (target != null) targetState = target.defaultBlockState();
        if (targetState == null) return list;

        List<BlockPos> result;
        result = BlockBoxUtils.getBlockCluster(
            target, pos, world, range,
            targetState.is(MoreTools.BlockTags.VEIN_HAMMER_APPLICABLE) ?
                BlockBoxUtils.DirectionSets.EXTENDED :
                BlockBoxUtils.DirectionSets.CARDINAL
        );

        if (world != null) {
            for (BlockPos blockPos : result) {
                if (world.getBlockState(blockPos).is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                    if (!(world.getBlockState(blockPos).getDestroySpeed(world, blockPos) > state.getDestroySpeed(world, pos))) list.add(blockPos);
                }
            }
        }
        return list;
    }
}
