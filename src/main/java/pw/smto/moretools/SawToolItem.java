package pw.smto.moretools;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SawToolItem extends MiningToolItem implements PolymerItem, MoreTools$CustomMiningToolItem.MoreTools$Interface {
    private final AxeItem base;
    private final float baseSpeed;
    private boolean actAsAxe = false;
    private final PolymerModelData model;

    public SawToolItem(AxeItem base) {
        super(Math.max(base.getAttackDamage()-7, 2.0F), -3.0f, base.getMaterial(), BlockTags.AXE_MINEABLE, new Item.Settings().maxDamage(base.getMaxDamage() * 3));
        this.base = base;
        this.baseSpeed = base.getMaterial().getMiningSpeedMultiplier();
        this.model = PolymerResourcePackUtils.requestModel(base, Identifier.of(MoreTools.MOD_ID,
                "item/" + Registries.ITEM.getId(this.base).getPath().replace("axe", "saw")));
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
                    this.actAsAxe = true;
                }
                else {
                    super.miningSpeed = this.baseSpeed - 3.0F;
                    if (super.miningSpeed < 1.0F) {
                        super.miningSpeed = 1.1F;
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
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
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
                player.getMainHandStack().damage(damage, player, (x) -> {
                   player.sendToolBreakStatus(player.getActiveHand());
                });
            }
        }
    }
}
