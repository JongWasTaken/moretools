package pw.smto.moretools;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExcavatorToolItem extends MiningToolItem implements PolymerItem, MoreTools$CustomMiningToolItem.MoreTools$Interface {
    private final ShovelItem base;
    private final float baseSpeed;
    private boolean actAsShovel = false;
    private final PolymerModelData model;

    public ExcavatorToolItem(ShovelItem base) {
        super(base.getMaterial(), BlockTags.SHOVEL_MINEABLE, new Item.Settings().attributeModifiers(
                MiningToolItem.createAttributeModifiers(
                        base.getMaterial(),
                        Math.max(base.getMaterial().getAttackDamage()-4, 1.0F),
                        -3.0f
                ))
        );
        this.base = base;
        this.baseSpeed = base.getMaterial().getMiningSpeedMultiplier();
        this.model = PolymerResourcePackUtils.requestModel(base, Identifier.of(MoreTools.MOD_ID,
                "item/" + Registries.ITEM.getId(this.base).getPath().replace("shovel", "excavator")));

    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (selected)
        {
            if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                if (serverPlayerEntity.isSneaking())
                {
                    stack.get(DataComponentTypes.TOOL).defaultMiningSpeed = this.baseSpeed;
                    this.actAsShovel = true;
                }
                else {
                    stack.get(DataComponentTypes.TOOL).defaultMiningSpeed = this.baseSpeed - 3.0F;
                    if (stack.get(DataComponentTypes.TOOL).defaultMiningSpeed() < 1.0F) {
                        stack.get(DataComponentTypes.TOOL).defaultMiningSpeed = 1.1F;
                    }
                    this.actAsShovel = false;
                }
            }
        }
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
    public Text getName(ItemStack stack) {
        return Text.of(this.base.getName().getString().replace("Shovel", "Excavator"));
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("Allows breaking blocks in a 3x3 radius.").formatted(Formatting.GOLD));
    }

    @Override
    public void postBlockBreak(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world) {
        if (this.actAsShovel) {
            return;
        }
        BlockBox selection = BlockBoxUtils.getSurroundingBlocks(pos, d, 1);
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
}
