package pw.smto.moretools.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
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
import pw.smto.moretools.MoreTools;
import pw.smto.moretools.util.BlockBoxUtils;
import pw.smto.moretools.util.CustomMaterial;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.List;

public class HammerToolItem extends BaseToolItem implements PolymerItem, PolymerKeepModel, PolymerClientDecoded {
    private final Item baseItem;

    private static Settings createSettings(ToolMaterial baseMaterial) {
        var settings = new Settings()
                .pickaxe(CustomMaterial.of(baseMaterial).multiplyDurability(3).toVanilla(), Math.max(baseMaterial.attackDamageBonus()-4, 1.0F), -3.0f)
                .component(DataComponentTypes.LORE, new LoreComponent(List.of(Text.translatable("item.moretools.hammer.tooltip").formatted(Formatting.GOLD))));
        if (baseMaterial.equals(ToolMaterial.NETHERITE)) settings.fireproof();
        return settings;
    }

    public HammerToolItem(Item base, ToolMaterial baseMaterial) {
        super(base, HammerToolItem.createSettings(baseMaterial), Identifier.of(MoreTools.MOD_ID, Registries.ITEM.getId(base).getPath().replace("pickaxe", "hammer")), baseMaterial, BlockTags.PICKAXE_MINEABLE);
        this.baseItem = base;
    }

    @Override
    public List<Text> getLore() {
        return List.of(Text.translatable("item.moretools.hammer.tooltip").formatted(Formatting.GOLD));
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
                if (world.getBlockState(blockPos).isIn(BlockTags.PICKAXE_MINEABLE)) {
                    if (!(world.getBlockState(blockPos).getHardness(world, blockPos) > state.getHardness(world, pos))) list.add(blockPos);
                }
            }
        }
        return list;
    }

    public void doToolPower(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world) {
        List<BlockPos> selection = this.getAffectedArea(world, pos, state, d, state.getBlock());
        for (BlockPos blockBoxSelectionPos : selection) {
            if (!blockBoxSelectionPos.equals(pos)) {
                player.interactionManager.tryBreakBlock(blockBoxSelectionPos);
            }
        }
    }
}
