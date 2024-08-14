package pw.smto.moretools.util;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import pw.smto.moretools.MoreTools;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

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

    public static List<BlockPos> getBlockCluster(Block toFind, BlockPos pos, World world, int limit, boolean useVanillaDirections) {
        Set<BlockPos> connectedBlocks = new HashSet<>();
        Set<BlockPos> visited = new HashSet<>();
        freeDfs(world, pos, toFind, connectedBlocks, visited, 0, limit, useVanillaDirections);
        return sortBlockSet(pos, connectedBlocks);
    }

    private static final List<Vec3i> DIRECTIONS = new ArrayList<>() {{
        // Vanilla directions
        add(new Vec3i(0, -1, 0)); // DOWN
        add(new Vec3i(0, 1, 0)); // UP
        add(new Vec3i(-1, 0, 0)); // WEST
        add(new Vec3i(1, 0, 0)); // EAST
        add(new Vec3i(0, 0, -1)); // SOUTH
        add(new Vec3i(0, 0, 1)); // NORTH
        // Corners
        add(new Vec3i(0, -1, 1)); // DOWN + NORTH
        add(new Vec3i(1, -1, 0)); // DOWN + EAST
        add(new Vec3i(0, -1, -1)); // DOWN + SOUTH
        add(new Vec3i(-1, -1, 0)); // DOWN + WEST
        add(new Vec3i(0, 1, 1)); // UP + NORTH
        add(new Vec3i(1, 1, 0)); // UP + EAST
        add(new Vec3i(0, 1, -1)); // UP + SOUTH
        add(new Vec3i(-1, 1, 0)); // UP + WEST
        // Corners of corners
        add(new Vec3i(1, -1, 1)); // DOWN + NORTH + EAST
        add(new Vec3i(-1, -1, 1)); // DOWN + NORTH + WEST
        add(new Vec3i(1, -1, -1)); // DOWN + SOUTH + EAST
        add(new Vec3i(-1, -1, -1)); // DOWN + SOUTH + WEST
        add(new Vec3i(1, 1, 1)); // UP + NORTH + EAST
        add(new Vec3i(-1, 1, 1)); // UP + NORTH + WEST
        add(new Vec3i(1, 1, -1)); // UP + SOUTH + EAST
        add(new Vec3i(-1, 1, -1)); // UP + SOUTH + WEST

    }};

    private static void freeDfs(World world, BlockPos currentPos, Block pickBlock, Set<BlockPos> connectedBlocks, Set<BlockPos> visited, int depth, int limit, boolean useVanillaDirections) {
        if (!visited.contains(currentPos) && depth < limit) {
            visited.add(currentPos);
            // Check if the current block matches the origin block type
            if (depth == 0 || world.getBlockState(currentPos).getBlock().equals(pickBlock)) {
                connectedBlocks.add(currentPos);
                if (useVanillaDirections) {
                    for (Direction direction : EnumSet.allOf(Direction.class)) {
                        BlockPos neighborPos = currentPos.offset(direction);
                        freeDfs(world, neighborPos, pickBlock, connectedBlocks, visited, depth + 1, limit, useVanillaDirections);
                    }
                } else {
                    for (Vec3i direction : DIRECTIONS) {
                        BlockPos neighborPos = offset(currentPos, direction);
                        freeDfs(world, neighborPos, pickBlock, connectedBlocks, visited, depth + 1, limit, useVanillaDirections);
                    }
                }
            }
        }
    }

    private static BlockPos offset(BlockPos pos, Vec3i direction) {
        return pos.add(direction);
    }

    private static List<BlockPos> sortBlockSet(BlockPos origin, Set<BlockPos> list) {
        List<BlockPos> sortedBlockPositionList = new ArrayList<>(list.stream().toList());
        sortedBlockPositionList.sort(new Comparator<BlockPos>() {
            private double distanceTo(BlockPos o1, BlockPos origin) {
                return Math.sqrt(Math.pow(o1.getX() - origin.getX(), 2) + Math.pow(origin.getY() - origin.getY(), 2) + Math.pow(origin.getZ() - origin.getZ(), 2));
            }

            public int compare(BlockPos o1, BlockPos o2) {
                double dist1 = distanceTo(o1, origin);
                double dist2 = distanceTo(o2, origin);
                return Double.compare(dist1, dist2);
            }
        });

        return sortedBlockPositionList;
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

        public List<BlockPos> toList() {
            var x = new ArrayList<BlockPos>();
            this.forEach(x::add);
            return x;
        }
    }
}
