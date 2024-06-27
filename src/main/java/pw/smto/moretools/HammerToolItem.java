package pw.smto.moretools;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HammerToolItem extends MiningToolItem implements PolymerItem, MoreTools$CustomMiningToolItem.MoreTools$Interface {
    private final PickaxeItem base;
    private final float baseSpeed;
    private boolean actAsPickaxe = false;

    private final PolymerModelData model;

    public HammerToolItem(PickaxeItem base) {
        super(Math.max(base.getAttackDamage()-4, 1.0F), -3.0f, base.getMaterial(), BlockTags.PICKAXE_MINEABLE, new Item.Settings().maxDamage(base.getMaxDamage() * 3));
        this.base = base;
        this.baseSpeed = super.miningSpeed;
        this.model = PolymerResourcePackUtils.requestModel(base, Identifier.of(MoreTools.MOD_ID,
                "item/" + Registries.ITEM.getId(this.base).getPath().replace("pickaxe", "hammer")));
    }

    @Override
    public void modifyClientTooltip(List<Text> tooltip, ItemStack stack, @Nullable ServerPlayerEntity player) {
        tooltip.add(Text.literal("True Durability: " + (this.getMaxDamage() - stack.getDamage()) + " / " + this.getMaxDamage()).formatted(Formatting.WHITE));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (selected)
        {
            if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                if (serverPlayerEntity.isSneaking())
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
    }

    public void postBlockBreak(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world) {
        if (this.actAsPickaxe) {
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
                        if (blockBoxSelection.isIn(BlockTags.PICKAXE_MINEABLE))
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
        return this.model.item();
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.model.value();
    }
    @Override
    public Text getName(ItemStack stack) {
        return Text.of(this.base.getName().getString().replace("Pickaxe", "Hammer"));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Allows breaking blocks in a 3x3 radius.").formatted(Formatting.GOLD));
    }
}
