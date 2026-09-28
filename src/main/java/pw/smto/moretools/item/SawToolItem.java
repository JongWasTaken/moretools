package pw.smto.moretools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.BlockTransformers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import pw.smto.moretools.MoreTools;
import pw.smto.moretools.util.BlockBoxUtils;

import java.util.Collections;
import java.util.List;

public class SawToolItem extends PolyBaseToolItem {

    private static final List<Component> LORE = List.of(Component.translatable("item.moretools.saw.tooltip").withStyle(ChatFormatting.GOLD), Component.translatable("item.moretools.saw.tooltip.2").withStyle(ChatFormatting.GOLD));

    protected SawToolItem(Properties settings, float fastSpeed, float slowSpeed, Item baseItem) {
        super(settings, fastSpeed, slowSpeed, baseItem);
    }

    public static Properties saw(Properties properties, ToolMaterial material, float attackDamageBaseline, float attackSpeedBaseline) {
        return properties.tool(material, MoreTools.BlockTags.SAW_MINEABLE, attackDamageBaseline, attackSpeedBaseline, 0).delayedComponent(DataComponents.BLOCK_TRANSFORMER, (context) -> context.getOrThrow(BlockTransformers.AXE));
    }

    @Override
    public List<Component> getLore() {
        return SawToolItem.LORE;
    }

    @Override
    public List<BlockPos> getAffectedArea(Level world, BlockPos pos, BlockState state, @Nullable Direction d, @Nullable Block target, boolean sneak) {

        if (
            sneak ||
                world == null ||
                !state.is(MoreTools.BlockTags.SAW_APPLICABLE)
        ) {
            IO.println(sneak);
            IO.println(world);
            IO.println(state);
            return Collections.emptyList();
        }
        List<BlockPos> blockCluster = BlockBoxUtils.getBlockCluster(target, pos, world, 30, BlockBoxUtils.DirectionSets.DOWN_RESTRICTED_EXTENDED);
        return blockCluster;
    }
}
