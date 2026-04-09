package pw.smto.moretools.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.ExtraCodecs;
import pw.smto.moretools.MoreTools;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Gson JSON_BUILDER = new GsonBuilder().setPrettyPrinting().create();

    private static final Codec<Map<String, ToolConfigEntry>> CODEC = Codec.unboundedMap(ExtraCodecs.NON_EMPTY_STRING, ToolConfigEntry.CODEC);
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("moretools.json");

    public static Map<String, ToolConfigEntry> config = new HashMap<>() {{
        this.put("wooden_hammer", ToolConfigEntry.DEFAULT);
        this.put("stone_hammer", ToolConfigEntry.DEFAULT);
        this.put("iron_hammer", ToolConfigEntry.DEFAULT);
        this.put("golden_hammer", ToolConfigEntry.DEFAULT);
        this.put("diamond_hammer", ToolConfigEntry.DEFAULT);
        this.put("netherite_hammer", ToolConfigEntry.DEFAULT);

        this.put("wooden_excavator", ToolConfigEntry.DEFAULT);
        this.put("stone_excavator", ToolConfigEntry.DEFAULT);
        this.put("iron_excavator", ToolConfigEntry.DEFAULT);
        this.put("golden_excavator", ToolConfigEntry.DEFAULT);
        this.put("diamond_excavator", ToolConfigEntry.DEFAULT);
        this.put("netherite_excavator", ToolConfigEntry.DEFAULT);

        this.put("wooden_saw", ToolConfigEntry.DEFAULT);
        this.put("stone_saw", ToolConfigEntry.DEFAULT);
        this.put("iron_saw", ToolConfigEntry.DEFAULT);
        this.put("golden_saw", ToolConfigEntry.DEFAULT);
        this.put("diamond_saw", ToolConfigEntry.DEFAULT);
        this.put("netherite_saw", ToolConfigEntry.DEFAULT);

        this.put("wooden_vein_hammer", ToolConfigEntry.createDefaultWithRange(3));
        this.put("stone_vein_hammer", ToolConfigEntry.createDefaultWithRange(4));
        this.put("iron_vein_hammer", ToolConfigEntry.createDefaultWithRange(5));
        this.put("golden_vein_hammer", ToolConfigEntry.createDefaultWithRange(6));
        this.put("diamond_vein_hammer", ToolConfigEntry.createDefaultWithRange(6));
        this.put("netherite_vein_hammer", ToolConfigEntry.createDefaultWithRange(7));

        this.put("wooden_vein_excavator", ToolConfigEntry.createDefaultWithRange(3));
        this.put("stone_vein_excavator", ToolConfigEntry.createDefaultWithRange(4));
        this.put("iron_vein_excavator", ToolConfigEntry.createDefaultWithRange(5));
        this.put("golden_vein_excavator", ToolConfigEntry.createDefaultWithRange(6));
        this.put("diamond_vein_excavator", ToolConfigEntry.createDefaultWithRange(6));
        this.put("netherite_vein_excavator", ToolConfigEntry.createDefaultWithRange(7));
    }};

    public static void init() {
        if (Files.exists(ConfigManager.CONFIG_PATH)) {
            try {
                String json = Files.readString(ConfigManager.CONFIG_PATH);
                ConfigManager.config = ConfigManager.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json)).resultOrPartial((_) -> {}).orElseThrow();
            } catch (Exception ignored) {}
        } else {
            try {

                Files.writeString(ConfigManager.CONFIG_PATH, ConfigManager.JSON_BUILDER.toJson(ConfigManager.CODEC.encodeStart(JsonOps.INSTANCE, ConfigManager.config).resultOrPartial((_) -> {}).orElseThrow()));
            } catch (Exception ignored) {
                MoreTools.LOGGER.warn("Failed to write default config file!");
            }
        }
    }
}
