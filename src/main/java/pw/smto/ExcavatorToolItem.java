package pw.smto;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExcavatorToolItem extends MiningToolItem implements PolymerItem, MoreTools$CustomMiningToolItem {
    private final ShovelItem base;
    private final float baseSpeed;
    private boolean actAsShovel = false;

    public ExcavatorToolItem(ShovelItem base) {
        super(base.getAttackDamage()-4, -3.0f, base.getMaterial(), BlockTags.SHOVEL_MINEABLE, new Settings());
        this.base = base;
        this.baseSpeed = super.miningSpeed;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (selected)
        {
            if (entity.isSneaking())
            {
                super.miningSpeed = this.baseSpeed;
                this.actAsShovel = true;
            }
            else {
                super.miningSpeed = this.baseSpeed - 3.0F;
                if (super.miningSpeed < 1.0F) {
                    super.miningSpeed = 1.1F;
                }
                this.actAsShovel = false;
            }
        }
    }

    public void breakBlocks(BlockPos pos, ServerPlayerEntity player, ServerWorld world) {
        if (this.actAsShovel) {
            return;
        }
        BlockBox selection = Structure.getSurroundingBlocks(pos,player,1, 35);
        BlockState blockBoxSelection;
        BlockPos blockBoxSelectionPos;
        for(var y = selection.getMinY(); y < selection.getMaxY()+1; y++)
        {
            for(var z = selection.getMinZ(); z < selection.getMaxZ()+1; z++)
            {
                for(var x = selection.getMinX(); x < selection.getMaxX()+1; x++)
                {
                    blockBoxSelectionPos = new BlockPos(x,y,z);
                    blockBoxSelection = world.getBlockState(blockBoxSelectionPos);
                    if (!blockBoxSelectionPos.equals(pos)) {
                        if (blockBoxSelection.isIn(BlockTags.SHOVEL_MINEABLE))
                        {
                            blockBoxSelection.getBlock().onBreak(world, pos, blockBoxSelection, player);
                            //boolean bl = world.removeBlock(pos, false);
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
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.base;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        //return PolymerResourcePackUtils.requestModel(this.base, new Identifier(MoreTools.MOD_ID, Registries.ITEM.getId(this.base).getPath().replace("shovel", "excavator"))).value();
        return MoreTools.MAGIC_NUMBER;
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.of(this.base.getName().getString().replace("Shovel", "Excavator"));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        TooltipHelper.setGimmickItemText(tooltip,null, "Allows breaking blocks in a 3x3 radius.");
    }
}
