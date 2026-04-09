package pw.smto.moretools.item;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import pw.smto.moretools.MoreTools;
import pw.smto.moretools.util.ToolConfigEntry;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseToolItem extends Item {

    public record BaseToolSettings(Identifier id, Item.Properties settings, ToolConfigEntry config) {}

    private static Tool createComponent(ToolMaterial m, TagKey<Block> tag, float multiplier) {
        HolderGetter<Block> registryEntryLookup = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        float speed = m.speed() * multiplier;
        return new Tool(
                // 0.3F applies to all non-target blocks, e.g. grass for a hammer
                java.util.List.of(
                        Tool.Rule.deniesDrops(
                                registryEntryLookup.getOrThrow(m.incorrectBlocksForDrops())
                        ),
                        Tool.Rule.minesAndDrops(
                                registryEntryLookup.getOrThrow(tag), speed)
                ), 1.0F, 1, true
        );
    }

    private final Tool fastComponent;
    private final Tool slowComponent;

    public final Identifier id;

    protected BaseToolItem(BaseToolSettings ts, ToolMaterial baseMaterial, TagKey<Block> targetBlocks) {
        super(ts.settings().setId(ResourceKey.create(Registries.ITEM, ts.id())).component(MoreTools.ACT_AS_BASE_TOOL, false));
        this.id = ts.id();
        this.fastComponent = BaseToolItem.createComponent(baseMaterial, targetBlocks, ts.config().defaultSpeed());
        this.slowComponent = BaseToolItem.createComponent(baseMaterial, targetBlocks, ts.config().sneakSpeed());
    }

    public abstract List<Component> getLore();

    @Override
    public void inventoryTick(@NonNull ItemStack stack, @NonNull ServerLevel world, @NonNull Entity entity, @Nullable EquipmentSlot slot) {
        if (slot != null)
        {
            if (entity instanceof ServerPlayer serverPlayerEntity) {
                if (MoreTools.isBedrockPlayer(serverPlayerEntity)) {
                    var list = new ArrayList<>(this.getLore());
                    list.add(Component.translatable("tooltip.moretools.durability").withStyle(ChatFormatting.GRAY).append(Component.literal(": " + (stack.getMaxDamage() - stack.getDamageValue())).withStyle(ChatFormatting.GRAY)));
                    stack.set(DataComponents.LORE, new ItemLore(list));
                }
                if (serverPlayerEntity.isShiftKeyDown())
                {
                    stack.set(DataComponents.TOOL, this.fastComponent);
                    stack.set(MoreTools.ACT_AS_BASE_TOOL, true);
                }
                else {
                    stack.set(DataComponents.TOOL, this.slowComponent);
                    stack.set(MoreTools.ACT_AS_BASE_TOOL, false);
                }
            }
        }
    }

    public void doToolPowerIfAllowed(BlockState state, BlockPos pos, Direction d, ServerPlayer player, Level world, ItemStack stack) {
        if (Boolean.TRUE.equals(stack.get(MoreTools.ACT_AS_BASE_TOOL))) return;
        this.doToolPower(state, pos, d, player, world);
    }

    public abstract List<BlockPos> getAffectedArea(@Nullable Level world, BlockPos pos, BlockState state, @Nullable Direction d, @Nullable Block target);

    public abstract void doToolPower(BlockState state, BlockPos pos, Direction d, ServerPlayer player, Level world);
}
