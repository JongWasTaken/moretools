package pw.smto.moretools.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record ToolConfigEntry(int durabilityMultiplier, float attackSpeed, float attackDamageModifier, float defaultSpeed, float sneakSpeed, Optional<Integer> range) {
    public static final ToolConfigEntry DEFAULT = new ToolConfigEntry(3, -3, -4, 1.0F, 0.5F, Optional.empty());
    public static ToolConfigEntry createDefaultWithRange(int range) {
        return new ToolConfigEntry(
                ToolConfigEntry.DEFAULT.durabilityMultiplier(),
                ToolConfigEntry.DEFAULT.attackSpeed(),
                ToolConfigEntry.DEFAULT.attackDamageModifier(),
                ToolConfigEntry.DEFAULT.defaultSpeed(),
                ToolConfigEntry.DEFAULT.sneakSpeed(),
                Optional.of(range)
        );
    }
    public static final Codec<ToolConfigEntry> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.fieldOf("durabilityMultiplier").forGetter(ToolConfigEntry::durabilityMultiplier),
            Codec.FLOAT.fieldOf("attackSpeed").forGetter(ToolConfigEntry::attackSpeed),
            Codec.FLOAT.fieldOf("attackDamageModifier").forGetter(ToolConfigEntry::attackDamageModifier),
            Codec.FLOAT.fieldOf("defaultSpeed").forGetter(ToolConfigEntry::defaultSpeed),
            Codec.FLOAT.fieldOf("sneakSpeed").forGetter(ToolConfigEntry::sneakSpeed),
            Codec.INT.optionalFieldOf("range").forGetter(ToolConfigEntry::range)
    ).apply(instance, ToolConfigEntry::new));
}
