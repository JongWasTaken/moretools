package pw.smto.moretools;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.shape.VoxelShapes;
import pw.smto.moretools.item.BaseToolItem;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class MoreToolsClient implements ClientModInitializer {
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static final String VERSION = FabricLoader.getInstance().getModContainer(MoreTools.MOD_ID).get().getMetadata().getVersion().toString();
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(MoreTools.Payloads.S2CHandshake.ID, (payload, context) -> {
            // Using the context.client() autocloseable crashes minecraft clients lol?
            context.client().execute(() -> ClientPlayNetworking.send(new MoreTools.Payloads.C2SHandshakeCallbackWithVersion(MoreToolsClient.VERSION.split("\\+")[0])));
        });

        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((WorldRenderContext context, OutlineRenderState outlineRenderState) -> {
            if (context.matrices() == null) return true;
            if (context.consumers() == null) return true;

            ClientPlayerEntity player = context.gameRenderer().getClient().player;
            if(player == null) return true;

            var tickCounter = MinecraftClient.getInstance().getRenderTickCounter();
            HitResult hitResult = player.getCrosshairTarget(tickCounter.getTickProgress(true), context.gameRenderer().getClient().getCameraEntity());
            BlockHitResult rtr = hitResult instanceof BlockHitResult ? (BlockHitResult) hitResult : null;
            if(rtr == null) return true;

            if (rtr.isInsideBlock()) return true;
            if (player.isSpectator()) return true;
            if (player.isSneaking()) return true;

            if (player.getEntityWorld().getBlockState(rtr.getBlockPos()).isAir()) return true;
            ItemStack tool = MoreToolsClient.convertPolymerStack(player.getMainHandStack());
            if(tool.isEmpty()) return true;
            if (tool.getItem() instanceof BaseToolItem t) {
                var blocks = t.getAffectedArea(player.getEntityWorld(), rtr.getBlockPos(), player.getEntityWorld().getBlockState(rtr.getBlockPos()), rtr.getSide(), player.getEntityWorld().getBlockState(rtr.getBlockPos()).getBlock());
                if(blocks == null || blocks.isEmpty()) return true;

                double d0 = player.lastRenderX + (player.getX() - player.lastRenderX) * tickCounter.getTickProgress(true);
                double d1 = player.lastRenderY + player.getStandingEyeHeight() + (player.getY() - player.lastRenderY) * tickCounter.getTickProgress(true);
                double d2 = player.lastRenderZ + (player.getZ() - player.lastRenderZ) * tickCounter.getTickProgress(true);

                for(BlockPos block : blocks) {
                    VertexRendering.drawOutline(
                            context.matrices(),
                            context.consumers().getBuffer(RenderLayers.lines()),
                            VoxelShapes.fullCube(),
                            block.getX() -d0, block.getY() -d1, block.getZ() -d2,
                            ColorHelper.getArgb(255, 255, 255), 2F
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
            var nbt = Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt();
            if (nbt.contains("$polymer:stack")) {
                nbt = nbt.getCompound("$polymer:stack").orElse(new NbtCompound());
                if (nbt.contains("id")) {
                    Identifier id = Identifier.tryParse(nbt.getString("id").orElse(""));
                    if (id != null) {
                        Item item = Registries.ITEM.get(id);
                        ItemStack newStack = item.getDefaultStack();
                        try {
                            nbt = nbt.getCompound("components").orElse(new NbtCompound()).getCompound("minecraft:custom_data").orElse(new NbtCompound());
                        } catch (Exception ignored) {}
                        newStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
                        return newStack;
                    }
                }
            }
        }
        return stack;
    }
}
