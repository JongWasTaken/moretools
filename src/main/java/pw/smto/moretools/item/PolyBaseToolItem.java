package pw.smto.moretools.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import pw.smto.moretools.MoreTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class PolyBaseToolItem extends BaseToolItem implements PolymerItem, PolymerClientDecoded {
    private final Item baseItem;

    protected PolyBaseToolItem(Properties settings, float fastSpeed, float slowSpeed, Item baseItem) {
        super(settings, fastSpeed, slowSpeed);
        this.baseItem = baseItem;
    }
    public abstract List<Component> getLore();

    @Override
    public void modifyClientTooltip(List<Component> tooltip, ItemStack stack, PacketContext context) {
        PolymerItem.super.modifyClientTooltip(tooltip, stack, context);
        tooltip.addAll(getLore());
        if (MoreTools.isBedrockPlayer(context.get(PacketContext.GAME_PROFILE).id())) {
            tooltip.add(Component.translatable("tooltip.moretools.durability").withStyle(ChatFormatting.GRAY).append(Component.literal(": " + (stack.getMaxDamage() - stack.getDamageValue())).withStyle(ChatFormatting.GRAY)));
        }
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        if (MoreTools.PLAYERS_WITH_CLIENT.contains(Objects.requireNonNull(context.get(PacketContext.GAME_PROFILE)).id())) {
            return this;
        }
        return baseItem;
    }
}
