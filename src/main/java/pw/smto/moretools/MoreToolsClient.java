package pw.smto.moretools;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import pw.smto.moretools.item.BaseToolItem;

@Environment(EnvType.CLIENT)
public class MoreToolsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(MoreTools.Payloads.S2CHandshake.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientPlayNetworking.send(new MoreTools.Payloads.C2SHandshakeCallback(true));
            });
        });

        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((WorldRenderContext context, HitResult hitResult) -> {
            BlockHitResult rtr = hitResult instanceof BlockHitResult ? (BlockHitResult) hitResult : null;
            if(rtr == null) return true;
            if (rtr.isInsideBlock()) return true;
            PlayerEntity player = context.gameRenderer().getClient().player;
            if(player == null) return true;
            if (player.isSneaking()) return true;
            if (player.getWorld().getBlockState(rtr.getBlockPos()).isAir()) return true;

            ItemStack tool = convertPolymerStack(player.getMainHandStack());
            if(tool.isEmpty()) return true;
            if (tool.getItem() instanceof BaseToolItem t) {
                var blocks = t.getAffectedArea(player.getWorld(), rtr.getBlockPos(), rtr.getSide(), player.getWorld().getBlockState(rtr.getBlockPos()).getBlock());
                if(blocks == null || blocks.isEmpty()) return true;

                double d0 = player.lastRenderX + (player.getX() - player.lastRenderX) * context.tickCounter().getTickDelta(false);
                double d1 = player.lastRenderY + player.getStandingEyeHeight() + (player.getY() - player.lastRenderY) * context.tickCounter().getTickDelta(false);
                double d2 = player.lastRenderZ + (player.getZ() - player.lastRenderZ) * context.tickCounter().getTickDelta(false);

                for(BlockPos block : blocks) {
                    WorldRenderer.drawBox(
                            context.matrixStack(),
                            context.consumers().getBuffer(RenderLayer.getLines()),
                            new Box(block).offset(-d0, -d1, -d2),
                            1, 1, 1, 0.4F
                    );
                }
                return false;
            }
            return true;
        });
    }

    public static ItemStack convertPolymerStack(ItemStack stack) {
        if (stack == null) return ItemStack.EMPTY;
        if (stack.getComponents().contains(DataComponentTypes.CUSTOM_DATA)) {
            var nbt = stack.get(DataComponentTypes.CUSTOM_DATA).copyNbt();
            if (nbt.contains("$polymer:stack")) {
                nbt = nbt.getCompound("$polymer:stack");
                if (nbt.contains("id")) {
                    Identifier id = Identifier.tryParse(nbt.getString("id"));
                    if (id != null) {
                        Item item = Registries.ITEM.get(id);
                        if (item != null) {
                            ItemStack newStack = item.getDefaultStack();
                            try {
                                nbt = nbt.getCompound("components").getCompound("minecraft:custom_data");
                            } catch (Exception ignored) {}
                            newStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
                            return newStack;
                        }
                    }
                }
            }
        }
        return stack;
    }
}
