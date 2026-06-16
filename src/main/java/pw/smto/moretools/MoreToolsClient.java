package pw.smto.moretools;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import pw.smto.moretools.item.BaseToolItem;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class MoreToolsClient implements ClientModInitializer {
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static final String VERSION = FabricLoader.getInstance().getModContainer(MoreTools.MOD_ID).get().getMetadata().getVersion().toString();
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(MoreTools.Payloads.S2CHandshake.ID, (_, context) -> context.client().execute(() -> ClientPlayNetworking.send(new MoreTools.Payloads.C2SHandshakeCallbackWithVersion(MoreToolsClient.VERSION.split("\\+")[0]))));

        LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register((LevelRenderContext context, BlockOutlineRenderState _) -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if(player == null) return true;

            var tickCounter = Minecraft.getInstance().getDeltaTracker();
            HitResult hitResult = player.raycastHitResult(tickCounter.getGameTimeDeltaPartialTick(true), Objects.requireNonNull(Minecraft.getInstance().getCameraEntity()));
            BlockHitResult rtr = hitResult instanceof BlockHitResult ? (BlockHitResult) hitResult : null;
            if(rtr == null) return true;

            if (rtr.isInside()) return true;
            if (player.isSpectator()) return true;
            if (player.isShiftKeyDown()) return true;

            if (player.level().getBlockState(rtr.getBlockPos()).isAir()) return true;
            ItemStack tool = MoreToolsClient.convertPolymerStack(player.getMainHandItem());
            if(tool.isEmpty()) return true;
            if (tool.getItem() instanceof BaseToolItem t) {
                var blocks = t.getAffectedArea(player.level(), rtr.getBlockPos(), player.level().getBlockState(rtr.getBlockPos()), rtr.getDirection(), player.level().getBlockState(rtr.getBlockPos()).getBlock());
                if(blocks == null || blocks.isEmpty()) return true;

                double d0 = player.xOld + (player.getX() - player.xOld) * tickCounter.getGameTimeDeltaPartialTick(true);
                double d1 = player.yOld + player.getEyeHeight() + (player.getY() - player.yOld) * tickCounter.getGameTimeDeltaPartialTick(true);
                double d2 = player.zOld + (player.getZ() - player.zOld) * tickCounter.getGameTimeDeltaPartialTick(true);

                for (BlockPos block : blocks) {
                    context.poseStack().pushPose();
                    context.poseStack().translate(block.getX() - d0, block.getY() - d1, block.getZ() - d2);
                    context.submitNodeCollector().submitShapeOutline(
                            context.poseStack(), Shapes.block(), RenderTypes.lines(), ARGB.color(255, 255, 255), 2.0F, true
                    );
                    context.poseStack().popPose();
                }

                return false;
            }
            return true;
        });
    }

    public static ItemStack convertPolymerStack(ItemStack stack) {
        if (stack == null) return ItemStack.EMPTY;
        if (stack.getComponents().has(DataComponents.CUSTOM_DATA)) {
            var nbt = Objects.requireNonNull(stack.get(DataComponents.CUSTOM_DATA)).copyTag();
            if (nbt.contains("$polymer:stack")) {
                nbt = nbt.getCompound("$polymer:stack").orElse(new CompoundTag());
                if (nbt.contains("id")) {
                    Identifier id = Identifier.tryParse(nbt.getString("id").orElse(""));
                    if (id != null) {
                        Item item = BuiltInRegistries.ITEM.getValue(id);
                        ItemStack newStack = item.getDefaultInstance();
                        try {
                            nbt = nbt.getCompound("components").orElse(new CompoundTag()).getCompound("minecraft:custom_data").orElse(new CompoundTag());
                        } catch (Exception ignored) {}
                        newStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
                        return newStack;
                    }
                }
            }
        }
        return stack;
    }
}
