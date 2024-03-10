package pw.smto;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HammerToolItem extends MiningToolItem implements PolymerItem, MoreTools$CustomMiningToolItem {
    private final PickaxeItem base;
    private final float baseSpeed;
    private boolean actAsPickaxe = false;

    public HammerToolItem(PickaxeItem base) {
        super(base.getAttackDamage(), 1, base.getMaterial(), BlockTags.PICKAXE_MINEABLE, new Item.Settings());
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
                this.actAsPickaxe = true;
            }
            else {
                super.miningSpeed = this.baseSpeed - 3.0F;
                if (super.miningSpeed < 1.0F) {
                    super.miningSpeed = 1.1F;
                }
                this.actAsPickaxe = false;
            }
        }
    }

    public void breakBlocks(BlockPos pos, ServerPlayerEntity player, ServerWorld world) {
        if (this.actAsPickaxe) {
            return;
        }

        BlockBox selection = Structure.getSurroundingBlocks(pos,player,1, 35);
        BlockPos blockBoxSelection;
        for(var y = selection.getMinY(); y < selection.getMaxY()+1; y++)
        {
            for(var z = selection.getMinZ(); z < selection.getMaxZ()+1; z++)
            {
                for(var x = selection.getMinX(); x < selection.getMaxX()+1; x++)
                {
                    blockBoxSelection = new BlockPos(x,y,z);
                    if (world.getBlockState(blockBoxSelection).isIn(BlockTags.PICKAXE_MINEABLE))
                    {
                        world.breakBlock(blockBoxSelection, !player.isCreative());
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
        return MoreTools.MAGIC_NUMBER;
    }
    @Override
    public Text getName(ItemStack stack) {
        return Text.of(this.base.getName().getString().replace("Pickaxe", "Hammer"));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        TooltipHelper.setGimmickItemText(tooltip,null, "Allows breaking blocks in a 3x3 radius.");
    }

}
