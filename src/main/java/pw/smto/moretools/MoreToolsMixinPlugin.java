package pw.smto.moretools;

import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

public class MoreToolsMixinPlugin implements IMixinConfigPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.equals("pw.smto.moretools.mixin.mod.PolyFactoryMinerMixin")) {
            return FabricLoader.getInstance().getModContainer("polyfactory").isPresent();
        }
        return IMixinConfigPlugin.super.shouldApplyMixin(targetClassName, mixinClassName);
    }
}
