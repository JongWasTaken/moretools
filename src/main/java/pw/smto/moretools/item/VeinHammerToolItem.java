package pw.smto.moretools.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pw.smto.moretools.MoreTools;
import pw.smto.moretools.util.BlockBoxUtils;

import java.util.List;

public class VeinHammerToolItem extends BaseToolItem implements PolymerItem {
    private final PolymerModelData model;
    private final int range;

    public VeinHammerToolItem(PickaxeItem base, int range) {
        super(base, BlockTags.PICKAXE_MINEABLE);
        this.model = PolymerResourcePackUtils.requestModel(base, Identifier.of(MoreTools.MOD_ID,
                "item/" + Registries.ITEM.getId(base).getPath().replace("pickaxe", "vein_hammer")));
        this.range = range;
    }

    public VeinHammerToolItem(PickaxeItem base) {
        this(base, 3);
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
        return "Allows breaking ore veins quickly.";
    }

    public void doToolPower(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world) {
        Block toFind = state.getBlock();
        int range = 3;
        if (state.isIn(BlockTags.COAL_ORES) ||
            state.isIn(BlockTags.COPPER_ORES) ||
            state.isIn(BlockTags.IRON_ORES) ||
            state.isIn(BlockTags.GOLD_ORES) ||
            state.isIn(BlockTags.DIAMOND_ORES) ||
            state.isIn(BlockTags.EMERALD_ORES) ||
            state.isIn(BlockTags.LAPIS_ORES) ||
            state.isIn(BlockTags.REDSTONE_ORES) ||
            state.getBlock().equals(Blocks.NETHER_QUARTZ_ORE) ||
            state.getBlock().equals(Blocks.ANCIENT_DEBRIS)) range = this.range;
        List<BlockPos> selection = BlockBoxUtils.getBlockCluster(toFind, pos, world, range);
        BlockState blockBoxSelection;
        for (BlockPos blockBoxSelectionPos : selection) {

            blockBoxSelection = world.getBlockState(blockBoxSelectionPos);
            if (!blockBoxSelectionPos.equals(pos)) {
                if (blockBoxSelection.isIn(BlockTags.PICKAXE_MINEABLE))
                {
                    blockBoxSelection.getBlock().onBreak(world, pos, blockBoxSelection, player);
                    boolean bl = world.breakBlock(blockBoxSelectionPos, false);
                    if (bl) {
                        blockBoxSelection.getBlock().onBroken(world, pos, blockBoxSelection);
                    }
                    if (!player.isCreative()) {
                        ItemStack itemStack = player.getMainHandStack();
                        ItemStack itemStack2 = itemStack.copy();
                        boolean bl2 = player.canHarvest(blockBoxSelection);
                        itemStack.postMine(world, blockBoxSelection, pos, player);
                        if (bl && bl2) {
                            blockBoxSelection.getBlock().afterBreak(world, player, pos, blockBoxSelection, world.getBlockEntity(blockBoxSelectionPos), itemStack2);
                        }
                    }
                }
            }
        }
    }
}
