package pw.smto.moretools.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import pw.smto.moretools.MoreTools;
import pw.smto.moretools.util.MiningBlockBreakingDirection;
import pw.smto.moretools.util.ToolConfigEntry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
    private final float fastSpeed;
    private final float slowSpeed;

    protected BaseToolItem(Properties settings, float fastSpeed, float slowSpeed) {
        super(settings);
        this.fastSpeed = fastSpeed;
        this.slowSpeed = slowSpeed;
    }

    public static Identifier attributeModifierID = MoreTools.id("sneak_dependent_speed");

    @Override
    public void inventoryTick(@NonNull ItemStack stack, @NonNull ServerLevel world, @NonNull Entity entity, @Nullable EquipmentSlot slot) {
        if (slot != null) {
            if (entity instanceof ServerPlayer serverPlayerEntity) {
                ArrayList<ItemAttributeModifiers.Entry> attributeModifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS).modifiers()
                    .stream()
                    .filter(attributeModifier -> !attributeModifier.matches(Attributes.MINING_EFFICIENCY, attributeModifierID))
                    .collect(ArrayList::new, List::add, List::addAll);
                attributeModifiers.add(
                    new ItemAttributeModifiers.Entry(
                        Attributes.MINING_EFFICIENCY,
                        new AttributeModifier(
                            attributeModifierID,
                            serverPlayerEntity.isShiftKeyDown() ? this.slowSpeed : this.fastSpeed,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                        ),
                        EquipmentSlotGroup.MAINHAND,
                        new ItemAttributeModifiers.Display.Hidden()
                    )
                );
                stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(attributeModifiers));
            }
        }
    }

    public abstract List<BlockPos> getAffectedArea(@Nullable Level world, BlockPos pos, BlockState state, @Nullable Direction d, @Nullable Block target, boolean isPlayerSneaking);

    private final ThreadLocal<Boolean> stopUsePropigation = ThreadLocal.withInitial(() -> false);
    private final ThreadLocal<Boolean> stopMinePropigation = ThreadLocal.withInitial(() -> false);

    @Override
    public @NonNull InteractionResult useOn(@NonNull UseOnContext context) {
        var clickedPosState = context.getLevel().getBlockState(context.getClickedPos());
        InteractionResult res = super.useOn(context);
        if (!stopUsePropigation.get() && context.getPlayer() instanceof ServerPlayer serverPlayer) {
            try {
                stopUsePropigation.set(true);
                for (var pos : this.getAffectedArea(
                    context.getLevel(),
                    context.getClickedPos(),
                    clickedPosState,
                    context.getClickedFace(),
                    clickedPosState.getBlock(),
                    serverPlayer.isShiftKeyDown()
                )) {
                    var out = serverPlayer.gameMode.useItemOn(serverPlayer, context.getLevel(), context.getItemInHand(), context.getHand(), new BlockHitResult(
                        context.getClickLocation(),
                        context.getClickedFace(),
                        pos,
                        context.isInside()
                    ));
                    if (out == InteractionResult.SUCCESS)
                        res = InteractionResult.SUCCESS;
                }
            } finally {
                stopUsePropigation.remove();
            }
        }
        return res;
    }

    @Override
    public boolean mineBlock(@NonNull ItemStack itemStack, @NonNull Level level, @NonNull BlockState state, @NonNull BlockPos pos, @NonNull LivingEntity owner) {
        var res = super.mineBlock(itemStack, level, state, pos, owner);
        var dir = MiningBlockBreakingDirection.direction.get();
        if (!stopMinePropigation.get() && owner instanceof ServerPlayer serverPlayer && dir != null) {
            try {
                stopMinePropigation.set(true);
                for (BlockPos blockPos : this.getAffectedArea(
                    level,
                    pos,
                    state,
                    dir,
                    state.getBlock(),
                    serverPlayer.isShiftKeyDown()
                )) {
                    res |= serverPlayer.gameMode.destroyBlock(blockPos);
                }
            } finally {
                stopMinePropigation.remove();
            }
        }
        return res;
    }
}
