package pw.smto.moretools.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BlockBoxUtils {
    /**
     * Get all blocks in a radius around the given BlockPos. Useful for items such as hammers and excavators.
     * @param pos BlockPos to get the surrounding blocks from
     * @param side Direction facing the player, e.g. from a raycast
     * @param radius How far to add blocks, e.g. a radius of 1 will add 9 blocks
     * @return {@link IterableBlockBox} containing all surrounding blocks
     */
    public static IterableBlockBox getSurroundingBlocks(BlockPos pos, Direction side, int radius) {
        BlockPos firstCorner;
        BlockPos secondCorner;
        int negativeMaxBlocks = Integer.parseInt("-" + radius);

        if (side == Direction.UP || side == Direction.DOWN) {
            firstCorner = pos.offset(negativeMaxBlocks,0,negativeMaxBlocks);
            secondCorner = pos.offset(radius,0,radius);
        }
        else {
            if (side == Direction.NORTH || side == Direction.SOUTH) { // not Z
                firstCorner = pos.offset(negativeMaxBlocks,negativeMaxBlocks,0);
                secondCorner = pos.offset(radius,radius,0);
            }
            else // not X
            {
                firstCorner = pos.offset(0,negativeMaxBlocks,negativeMaxBlocks);
                secondCorner = pos.offset(0,radius,radius);
            }
        }
        return new IterableBlockBox(
                firstCorner.getX(), firstCorner.getY(), firstCorner.getZ(),
                secondCorner.getX(), secondCorner.getY(), secondCorner.getZ()
        );
    }

    public static List<BlockPos> getBlockCluster(Block toFind, BlockPos pos, Level world, int limit, DirectionSet directions) {
        Set<BlockPos> connectedBlocks = new HashSet<>();
        Set<BlockPos> visited = new HashSet<>();
        BlockBoxUtils.freeDfs(world, pos, toFind, connectedBlocks, visited, 0, limit, directions);
        return BlockBoxUtils.sortBlockSet(pos, connectedBlocks);
    }

    public record DirectionSet(List<Vec3i> directions) {
        public static DirectionSet of(Vec3i... directions) {
            return new DirectionSet(Arrays.asList(directions));
        }
    }

    public static class DirectionSets {
        public static final DirectionSet CARDINAL = new DirectionSet(EnumSet.allOf(Direction.class).stream().map(Direction::getUnitVec3i).toList());
        public static final DirectionSet EXTENDED = DirectionSet.of(
                // Vanilla directions
                new Vec3i(0, -1, 0), // DOWN
                new Vec3i(0, 1, 0), // UP
                new Vec3i(-1, 0, 0), // WEST
                new Vec3i(1, 0, 0), // EAST
                new Vec3i(0, 0, -1), // SOUTH
                new Vec3i(0, 0, 1), // NORTH
                // Corners
                new Vec3i(1, 0, 1), // NORTH + EAST
                new Vec3i(-1, 0, 1), // NORTH + WEST
                new Vec3i(1, 0, -1), // SOUTH + EAST
                new Vec3i(-1, 0, -1), // SOUTH + WEST
                new Vec3i(0, -1, 1), // DOWN + NORTH
                new Vec3i(1, -1, 0), // DOWN + EAST
                new Vec3i(0, -1, -1), // DOWN + SOUTH
                new Vec3i(-1, -1, 0), // DOWN + WEST
                new Vec3i(0, 1, 1), // UP + NORTH
                new Vec3i(1, 1, 0), // UP + EAST
                new Vec3i(0, 1, -1), // UP + SOUTH
                new Vec3i(-1, 1, 0), // UP + WEST
                // Corners of corners
                new Vec3i(1, -1, 1), // DOWN + NORTH + EAST
                new Vec3i(-1, -1, 1), // DOWN + NORTH + WEST
                new Vec3i(1, -1, -1), // DOWN + SOUTH + EAST
                new Vec3i(-1, -1, -1), // DOWN + SOUTH + WEST
                new Vec3i(1, 1, 1), // UP + NORTH + EAST
                new Vec3i(-1, 1, 1), // UP + NORTH + WEST
                new Vec3i(1, 1, -1), // UP + SOUTH + EAST
                new Vec3i(-1, 1, -1) // UP + SOUTH + WEST
        );
        public static final DirectionSet DOWN_RESTRICTED_EXTENDED = DirectionSet.of(
                // Vanilla directions
                //new Vec3i(0, -1, 0), // DOWN
                new Vec3i(0, 1, 0), // UP
                new Vec3i(-1, 0, 0), // WEST
                new Vec3i(1, 0, 0), // EAST
                new Vec3i(0, 0, -1), // SOUTH
                new Vec3i(0, 0, 1), // NORTH
                // Corners
                new Vec3i(1, 0, 1), // NORTH + EAST
                new Vec3i(-1, 0, 1), // NORTH + WEST
                new Vec3i(1, 0, -1), // SOUTH + EAST
                new Vec3i(-1, 0, -1), // SOUTH + WEST
                new Vec3i(0, -1, 1), // DOWN + NORTH
                new Vec3i(1, -1, 0), // DOWN + EAST
                new Vec3i(0, -1, -1), // DOWN + SOUTH
                new Vec3i(-1, -1, 0), // DOWN + WEST
                new Vec3i(0, 1, 1), // UP + NORTH
                new Vec3i(1, 1, 0), // UP + EAST
                new Vec3i(0, 1, -1), // UP + SOUTH
                new Vec3i(-1, 1, 0), // UP + WEST
                // Corners of corners
                new Vec3i(1, -1, 1), // DOWN + NORTH + EAST
                new Vec3i(-1, -1, 1), // DOWN + NORTH + WEST
                new Vec3i(1, -1, -1), // DOWN + SOUTH + EAST
                new Vec3i(-1, -1, -1), // DOWN + SOUTH + WEST
                new Vec3i(1, 1, 1), // UP + NORTH + EAST
                new Vec3i(-1, 1, 1), // UP + NORTH + WEST
                new Vec3i(1, 1, -1), // UP + SOUTH + EAST
                new Vec3i(-1, 1, -1) // UP + SOUTH + WEST
        );

    }



    private static void freeDfs(Level world, BlockPos currentPos, Block pickBlock, Set<BlockPos> connectedBlocks, Set<BlockPos> visited, int depth, int limit, DirectionSet directions) {
        if (!visited.contains(currentPos) && depth < limit) {
            visited.add(currentPos);
            // Check if the current block matches the origin block type
            if (depth == 0 || world.getBlockState(currentPos).getBlock().equals(pickBlock)) {
                connectedBlocks.add(currentPos);
                for (Vec3i direction : directions.directions()) {
                    BlockPos neighborPos = BlockBoxUtils.offset(currentPos, direction);
                    BlockBoxUtils.freeDfs(world, neighborPos, pickBlock, connectedBlocks, visited, depth + 1, limit, directions);
                }
            }
        }
    }

    private static BlockPos offset(BlockPos pos, Vec3i direction) {
        return pos.offset(direction);
    }

    private static List<BlockPos> sortBlockSet(BlockPos origin, Set<BlockPos> list) {
        List<BlockPos> sortedBlockPositionList = new ArrayList<>(list.stream().toList());
        sortedBlockPositionList.sort(new Comparator<>() {
            private double distanceTo(BlockPos o1, BlockPos origin) {
                return Math.sqrt(Math.pow(o1.getX() - origin.getX(), 2) + Math.pow(o1.getY() - origin.getY(), 2) + Math.pow(o1.getZ() - origin.getZ(), 2));
            }

            public int compare(BlockPos o1, BlockPos o2) {
                double dist1 = this.distanceTo(o1, origin);
                double dist2 = this.distanceTo(o2, origin);
                return Double.compare(dist1, dist2);
            }
        });

        return sortedBlockPositionList;
    }

    public static class IterableBlockBox extends BoundingBox implements Iterable<BlockPos> {
        public IterableBlockBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            super(minX, minY, minZ, maxX, maxY, maxZ);
        }

        @NotNull
        @Override
        public Iterator<BlockPos> iterator() {
            return this.toList().iterator();
        }

        @Override
        public void forEach(Consumer<? super BlockPos> action) {
            for(var y = this.minY(); y < this.maxY()+1; y++)
            {
                for(var z = this.minZ(); z < this.maxZ()+1; z++)
                {
                    for(var x = this.minX(); x < this.maxX()+1; x++)
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
