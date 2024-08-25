package pw.smto.moretools.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pw.smto.moretools.util.MutableMaterial;

import java.util.ArrayList;
import java.util.List;

public class BaseToolItem extends MiningToolItem {
    private static ToolComponent createComponent(ToolMaterial m, TagKey<Block> tag, float multiplier) {
        float speed = m.getMiningSpeedMultiplier() * multiplier;
        return new ToolComponent(
                // 0.1F applies to all non-target blocks, e.g. grass for a hammer
                List.of(ToolComponent.Rule.ofNeverDropping(m.getInverseTag()), ToolComponent.Rule.ofAlwaysDropping(tag, speed)), 0.3F, 1
        );
    }

    private final ToolComponent fastComponent;
    private final ToolComponent slowComponent;

    public BaseToolItem(MiningToolItem baseItem, TagKey<Block> targetBlocks) {
        super(
                // durability gets tripled
                MutableMaterial.of(baseItem.getMaterial()).setDurability((baseItem.getDefaultStack().getMaxDamage() * 3)),
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
        this.fastComponent = createComponent(baseItem.getMaterial(), targetBlocks, 1F);
        this.slowComponent = createComponent(baseItem.getMaterial(), targetBlocks, 0.5F);
    }

    private boolean actAsBaseTool = false;
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (selected)
        {
            if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                if (serverPlayerEntity.isSneaking())
                {
                    stack.set(DataComponentTypes.TOOL, this.fastComponent);
                    this.actAsBaseTool = true;
                }
                else {
                    stack.set(DataComponentTypes.TOOL, this.slowComponent);
                    this.actAsBaseTool = false;
                }
            }
        }
    }

    public void postBlockBreak(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world) {
        if (this.actAsBaseTool) return;
        doToolPower(state, pos, d, player, world);
    }

    public List<BlockPos> getAffectedArea(@Nullable World world, BlockPos pos, BlockState state, @Nullable Direction d, @Nullable Block target) {
        return new ArrayList<>();
    }

    public void doToolPower(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world) {}
}
