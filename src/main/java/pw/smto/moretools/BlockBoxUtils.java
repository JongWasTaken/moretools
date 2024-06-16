package pw.smto.moretools;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

// Taken from IndexLib, my shared library I am currently working on
public class BlockBoxUtils {

    /**
     * You probably want {@link #getSurroundingBlocks} instead, as it behaves much more intuitively.
     * Only use this if you don't have a direction and cannot raycast from the player for some reason.
     * This is a convenience method that hard-codes the pitchThreshold to 35.
     * @param pos BlockPos to get the surrounding blocks from
     * @param player PlayerEntity to get the pitch from
     * @param radius How far to add blocks, e.g. a radius of 1 will add 9 blocks
     * @return a {@link IterableBlockBox} containing all surrounding blocks
     */
    public static IterableBlockBox getSurroundingBlocksUsingPitch(BlockPos pos, PlayerEntity player, int radius) {
        return getSurroundingBlocksUsingPitch(pos, player, radius, 35);
    }

    /**
     * You probably want {@link #getSurroundingBlocks} instead, as it behaves much more intuitively.
     * Only use this if you don't have a direction and cannot raycast from the player for some reason.
     * @param pos BlockPos to get the surrounding blocks from
     * @param player PlayerEntity to get the pitch from
     * @param radius How far to add blocks, e.g. a radius of 1 will add 9 blocks
     * @param pitchThreshold The pitch threshold at which the player is considered to be looking "up", 35 is the recommended value
     * @return a {@link IterableBlockBox} containing all surrounding blocks
     */
    public static IterableBlockBox getSurroundingBlocksUsingPitch(BlockPos pos, PlayerEntity player, int radius, int pitchThreshold) {
        Direction side = player.getHorizontalFacing().getOpposite();
        float pitch = player.getPitch();
        BlockPos firstCorner;
        BlockPos secondCorner;
        int negativeMaxBlocks = Integer.parseInt("-" + radius);

        if (pitch > pitchThreshold || pitch < Integer.parseInt("-" + pitchThreshold)) {
            firstCorner = pos.add(negativeMaxBlocks,0,negativeMaxBlocks);
            secondCorner = pos.add(radius,0,radius);
        }
        else {
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
        return new IterableBlockBox(
                firstCorner.getX(), firstCorner.getY(), firstCorner.getZ(),
                secondCorner.getX(), secondCorner.getY(), secondCorner.getZ()
        );
    }

    /**
     * Get all blocks in a radius around the given BlockPos. Useful for items such as hammers and excavators.
     * @param pos BlockPos to get the surrounding blocks from
     * @param side Direction facing the player, e.g. from a raycast
     * @param radius How far to add blocks, e.g. a radius of 1 will add 9 blocks
     * @return a {@link IterableBlockBox} containing all surrounding blocks
     */
    public static IterableBlockBox getSurroundingBlocks(BlockPos pos, Direction side, int radius) {
        BlockPos firstCorner;
        BlockPos secondCorner;
        int negativeMaxBlocks = Integer.parseInt("-" + radius);

        if (side.equals(Direction.UP) || side.equals(Direction.DOWN)) {
            firstCorner = pos.add(negativeMaxBlocks,0,negativeMaxBlocks);
            secondCorner = pos.add(radius,0,radius);
        }
        else {
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
        return new IterableBlockBox(
                firstCorner.getX(), firstCorner.getY(), firstCorner.getZ(),
                secondCorner.getX(), secondCorner.getY(), secondCorner.getZ()
        );
    }

    public static class IterableBlockBox extends BlockBox implements Iterable<BlockPos> {
        public static IterableBlockBox of(BlockBox b) {
            return new IterableBlockBox(b.getMinX(), b.getMinY(), b.getMinZ(), b.getMaxX(), b.getMaxY(), b.getMaxZ());
        }
        public IterableBlockBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            super(minX, minY, minZ, maxX, maxY, maxZ);
        }

        @NotNull
        @Override
        public Iterator<BlockPos> iterator() {
            var x = new ArrayList<BlockPos>();
            this.forEach(x::add);
            return x.iterator();
        }

        @Override
        public void forEach(Consumer<? super BlockPos> action) {
            for(var y = this.getMinY(); y < this.getMaxY()+1; y++)
            {
                for(var z = this.getMinZ(); z < this.getMaxZ()+1; z++)
                {
                    for(var x = this.getMinX(); x < this.getMaxX()+1; x++)
                    {
                        action.accept(new BlockPos(x, y, z));
                    }
                }
            }
        }
    }
}
