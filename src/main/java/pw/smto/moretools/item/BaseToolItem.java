package pw.smto.moretools.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import pw.smto.moretools.Config;
import pw.smto.moretools.MoreTools;
import pw.smto.moretools.util.MutableMaterial;

import java.util.List;

public class BaseToolItem extends MiningToolItem {
    //private final float baseSpeed;
    public BaseToolItem(MiningToolItem baseItem, TagKey<Block> targetBlocks) {
        super(
                // durability gets tripled
                MutableMaterial.of(baseItem.getMaterial()).setDurability((int) (baseItem.getDefaultStack().getMaxDamage() * Config.Data.DURABILITY_MULTIPLIER)),
                // derive target blocks from baseItem
                targetBlocks,
                // damage and mining speed get nerfed
                new Item.Settings().attributeModifiers(
                        MiningToolItem.createAttributeModifiers(
                                baseItem.getMaterial(),
                                Math.max(baseItem.getMaterial().getAttackDamage()-4, 1.0F),
                                -3.0f
                        )
                )
        );
        //this.baseSpeed = baseItem.getMaterial().getMiningSpeedMultiplier();
    }

    private boolean actAsBaseTool = false;
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        MoreTools.LOGGER.warn(String.valueOf(stack.get(DataComponentTypes.TOOL).defaultMiningSpeed()));
        if (selected)
        {
            if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                if (serverPlayerEntity.isSneaking())
                {
                    stack.get(DataComponentTypes.TOOL).defaultMiningSpeed = 1.0F;
                    this.actAsBaseTool = true;
                }
                else {
                    stack.get(DataComponentTypes.TOOL).defaultMiningSpeed = 0.5F;
                    this.actAsBaseTool = false;
                }
            }
        }
    }

    public String getGimmickText() {
        return "";
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal(getGimmickText()).formatted(Formatting.GOLD));
    }

    public void postBlockBreak(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world) {
        if (this.actAsBaseTool) return;
        doToolPower(state, pos, d, player, world);
    }

    public void doToolPower(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world) {}
}
