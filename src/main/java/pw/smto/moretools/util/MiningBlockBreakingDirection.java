package pw.smto.moretools.util;

import net.minecraft.core.Direction;

public class MiningBlockBreakingDirection {
    public static final ThreadLocal<Direction> direction = new ThreadLocal<>();
}
