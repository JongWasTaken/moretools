package pw.smto.moretools.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EnchantableComponent;
import net.minecraft.component.type.RepairableComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pw.smto.moretools.MoreTools;
import pw.smto.moretools.util.CustomMaterial;

import java.util.ArrayList;
import java.util.List;

public class BaseToolItem extends MiningToolItem {
    private static ToolComponent createComponent(ToolMaterial m, TagKey<Block> tag, float multiplier) {
        RegistryEntryLookup<Block> registryEntryLookup = Registries.createEntryLookup(Registries.BLOCK);
        float speed = m.speed() * multiplier;
        return new ToolComponent(
                // 0.3F applies to all non-target blocks, e.g. grass for a hammer
                List.of(
                        ToolComponent.Rule.ofNeverDropping(
                                registryEntryLookup.getOrThrow(m.incorrectBlocksForDrops())
                        ),
                        ToolComponent.Rule.ofAlwaysDropping(
                                registryEntryLookup.getOrThrow(tag), speed)
                ), 1.0F, 1
        );
    }

    private final ToolComponent fastComponent;
    private final ToolComponent slowComponent;

    public final Identifier id;

    public BaseToolItem(Item base, Identifier id, ToolMaterial baseMaterial, TagKey<Block> targetBlocks) {
        super(
                // durability gets tripled
                CustomMaterial.of(baseMaterial).multiplyDurability(3).toVanilla(),
                targetBlocks,
                // damage and mining speed get nerfed
                Math.max(baseMaterial.attackDamageBonus()-4, 1.0F),
                -3.0f,
                new Item.Settings()
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, id))
                        .component(MoreTools.ACT_AS_BASE_TOOL, false)
        );
        this.id = id;
        this.fastComponent = BaseToolItem.createComponent(baseMaterial, targetBlocks, 1.0F);
        this.slowComponent = BaseToolItem.createComponent(baseMaterial, targetBlocks, 0.5F);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (selected)
        {
            if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                if (serverPlayerEntity.isSneaking())
                {
                    stack.set(DataComponentTypes.TOOL, this.fastComponent);
                    stack.set(MoreTools.ACT_AS_BASE_TOOL, true);
                }
                else {
                    stack.set(DataComponentTypes.TOOL, this.slowComponent);
                    stack.set(MoreTools.ACT_AS_BASE_TOOL, false);
                }
            }
        }
    }

    public void postBlockBreak(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world, ItemStack stack) {
        if (Boolean.TRUE.equals(stack.get(MoreTools.ACT_AS_BASE_TOOL))) return;
        this.doToolPower(state, pos, d, player, world);
    }

    public List<BlockPos> getAffectedArea(@Nullable World world, BlockPos pos, BlockState state, @Nullable Direction d, @Nullable Block target) {
        return new ArrayList<>();
    }

    public void doToolPower(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world) {}
}
