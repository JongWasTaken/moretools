package pw.smto.moretools;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SawToolItem extends MiningToolItem implements PolymerItem, MoreTools$CustomMiningToolItem.MoreTools$Interface {
    private final AxeItem base;
    private final float baseSpeed;
    private boolean actAsAxe = false;
    private final PolymerModelData model;

    public SawToolItem(AxeItem base) {
        super(new CustomMaterial(base.getMaterial(), base.getDefaultStack().getMaxDamage() * 3),
                BlockTags.AXE_MINEABLE, new Settings().attributeModifiers(
                MiningToolItem.createAttributeModifiers(
                        base.getMaterial(),
                        Math.max(base.getMaterial().getAttackDamage()-4, 1.0F),
                        -3.0f
                ))
        );
        this.base = base;
        this.baseSpeed = base.getMaterial().getMiningSpeedMultiplier();

        this.model = PolymerResourcePackUtils.requestModel(base, Identifier.of(MoreTools.MOD_ID,
                "item/" + Registries.ITEM.getId(this.base).getPath().replace("axe", "saw")));

    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (selected)
        {
            if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                if (serverPlayerEntity.isSneaking())
                {
                    stack.get(DataComponentTypes.TOOL).defaultMiningSpeed = this.baseSpeed;
                    this.actAsAxe = true;
                }
                else {
                    stack.get(DataComponentTypes.TOOL).defaultMiningSpeed = this.baseSpeed - 3.0F;
                    if (stack.get(DataComponentTypes.TOOL).defaultMiningSpeed() < 1.0F) {
                        stack.get(DataComponentTypes.TOOL).defaultMiningSpeed = 1.1F;
                    }
                    this.actAsAxe = false;
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
        return Text.of(this.base.getName().getString().replace("Axe", "Saw"));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("Breaks as many logs as possible, but only upwards.").formatted(Formatting.GOLD));
    }

    @Override
    public void postBlockBreak(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world) {
        if (this.actAsAxe) {
            return;
        }

        if (state.isIn(BlockTags.LOGS)) {
            int damage = 1;

            final int maxDamage = Math.abs(player.getMainHandStack().getMaxDamage() - player.getMainHandStack().getDamage());

            BlockPos.Mutable up = pos.mutableCopy();
            while (world.getBlockState(up.move(Direction.UP)).isIn(BlockTags.LOGS) && damage < maxDamage) {
                world.breakBlock(up, !player.isCreative());
                damage++;
            }
            if (!player.isCreative()) {
                if (player.getActiveHand() == Hand.MAIN_HAND) {
                    player.getMainHandStack().damage(damage, player, EquipmentSlot.MAINHAND);
                }
                else {
                    player.getMainHandStack().damage(damage, player, EquipmentSlot.OFFHAND);
                }
            }
        }
    }
}
