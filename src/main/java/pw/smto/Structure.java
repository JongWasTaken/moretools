package pw.smto;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Structure {
    public static BlockBox getSurroundingBlocks(BlockPos pos, ServerPlayerEntity player, int radius) {
        // 45 is technically the "correct" pitchThreshold, but in testing I found 35 to be more intuitive
        return getSurroundingBlocks(pos, player, radius, 35);
    }

    public static BlockBox getSurroundingBlocks(BlockPos pos, ServerPlayerEntity player, int radius, int pitchThreshold) {
        Direction side = player.getHorizontalFacing().getOpposite();
        float pitch = player.getPitch();

        if (pitch > pitchThreshold) {
            side = Direction.DOWN;
        }
        else if (pitch < Integer.parseInt("-" + pitchThreshold)) {
            side = Direction.UP;
        }

        BlockPos firstCorner;
        BlockPos secondCorner;
        int negativeMaxBlocks = Integer.parseInt("-" + radius);

        if (side.equals(Direction.UP) || side.equals(Direction.DOWN)) { // not Y
            firstCorner = pos.add(negativeMaxBlocks,0,negativeMaxBlocks);
            secondCorner = pos.add(radius,0,radius);
        }
        else
        {
            if (side.equals(Direction.NORTH) || side.equals(Direction.SOUTH)) { // not Z
                firstCorner = pos.add(negativeMaxBlocks,negativeMaxBlocks,0);
                secondCorner = pos.add(radius,radius,0);
            }
            else // not X
            {
                firstCorner = pos.add(0,negativeMaxBlocks,negativeMaxBlocks);
                secondCorner = pos.add(0,radius,radius);
            }
        }

        return new BlockBox(
                firstCorner.getX(),firstCorner.getY(),firstCorner.getZ(),
                secondCorner.getX(),secondCorner.getY(),secondCorner.getZ()
        );
    }
}
