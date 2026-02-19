package pw.smto.moretools.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pw.smto.moretools.MoreTools;
import pw.smto.moretools.util.BlockBoxUtils;
import pw.smto.moretools.util.ConfigManager;
import pw.smto.moretools.util.CustomMaterial;
import pw.smto.moretools.util.ToolConfigEntry;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.List;

public class ExcavatorToolItem extends BaseToolItem implements PolymerItem, PolymerKeepModel, PolymerClientDecoded {
    private final Item baseItem;

    private static final List<Text> LORE = List.of(Text.translatable("item.moretools.excavator.tooltip").formatted(Formatting.GOLD));

    private static BaseToolSettings createSettings(Item base, ToolMaterial baseMaterial) {
        Identifier id = Identifier.of(MoreTools.MOD_ID, Registries.ITEM.getId(base).getPath().replace("shovel", "excavator"));
        var config = ConfigManager.config.get(id.getPath());
        Settings settings = new Settings();
        if (config == null) {
            config = ToolConfigEntry.DEFAULT;
            settings.pickaxe(CustomMaterial.of(baseMaterial).multiplyDurability(3).toVanilla(), Math.max(baseMaterial.attackDamageBonus()-4, 1.0F), -3.0f);
        } else {
            settings.pickaxe(CustomMaterial.of(baseMaterial).multiplyDurability(config.durabilityMultiplier()).toVanilla(), Math.max(baseMaterial.attackDamageBonus() + config.attackDamageModifier(), 1.0F), config.attackSpeed());
        }
        settings.component(DataComponentTypes.LORE, new LoreComponent(ExcavatorToolItem.LORE));
        if (baseMaterial.equals(ToolMaterial.NETHERITE)) settings.fireproof();
        return new BaseToolSettings(id, settings, config);
    }

    public ExcavatorToolItem(ShovelItem base, ToolMaterial baseMaterial) {
        super(ExcavatorToolItem.createSettings(base, baseMaterial), baseMaterial, BlockTags.SHOVEL_MINEABLE);
        this.baseItem = base;
    }

    @Override
    public List<Text> getLore() {
        return ExcavatorToolItem.LORE;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        if (MoreTools.PLAYERS_WITH_CLIENT.contains(context.getPlayer())) {
            return this;
        }
        return this.baseItem;
    }


    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return super.id;
    }

    @Override
    public List<BlockPos> getAffectedArea(@Nullable World world, BlockPos pos, BlockState state, @Nullable Direction d, @Nullable Block target) {
        var list = new ArrayList<BlockPos>();
        List<BlockPos> result = new ArrayList<>();
        if (d != null) result = BlockBoxUtils.getSurroundingBlocks(pos, d, 1).toList();
        if (world != null) {
            for (BlockPos blockPos : result) {
                if (world.getBlockState(blockPos).isIn(BlockTags.SHOVEL_MINEABLE)) {
                    list.add(blockPos);
                }
            }
        }
        return list;
    }

    @Override
    public void doToolPower(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world) {
        List<BlockPos> selection = this.getAffectedArea(world, pos, state, d, state.getBlock());
        for (BlockPos blockBoxSelectionPos : selection) {
            if (!blockBoxSelectionPos.equals(pos)) {
                player.interactionManager.tryBreakBlock(blockBoxSelectionPos);
            }
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer() == null) return ActionResult.PASS;
        if (context.getPlayer().isSneaking()) return this.baseItem.useOnBlock(context);
        ActionResult res = ActionResult.PASS;
        for (BlockPos blockPos : this.getAffectedArea(context.getWorld(), context.getBlockPos(), context.getWorld().getBlockState(context.getBlockPos()), context.getSide(), context.getWorld().getBlockState(context.getBlockPos()).getBlock())) {
            var out = this.baseItem.useOnBlock(new ItemUsageContext(context.getWorld(), context.getPlayer(), context.getHand(), context.getStack(), new BlockHitResult(context.getHitPos(), context.getSide(), blockPos, context.hitsInsideBlock())));
            if (out == ActionResult.SUCCESS) res = ActionResult.SUCCESS;
        }
        return res;
    }
}
