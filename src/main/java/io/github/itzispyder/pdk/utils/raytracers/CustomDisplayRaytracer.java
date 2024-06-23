package io.github.itzispyder.pdk.utils.raytracers;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.bukkit.util.VoxelShape;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CustomDisplayRaytracer {

    public static final Predicate<Point> HIT_BLOCK = point -> {
        Block b = point.getBlock();
        Location l = point.getLoc();

        if (b == null || b.isEmpty() || !b.isCollidable())
            return false;

        Vector vec = l.toVector().subtract(b.getLocation().toVector());
        VoxelShape shape = b.getCollisionShape();

        for (BoundingBox box : shape.getBoundingBoxes())
            if (box.contains(vec))
                return true;
        return false;
    };

    public static final Predicate<Point> HIT_ENTITY = point -> {
        return !point.getNearbyEntities(null, 5, true, 0.1, e -> e instanceof LivingEntity le && !le.isDead()).isEmpty();
    };

    public static final Predicate<Point> HIT_BLOCK_OR_ENTITY = point -> {
        return HIT_BLOCK.test(point) || HIT_ENTITY.test(point);
    };

    public static final Predicate<Point> HIT_BLOCK_AND_ENTITY = point -> {
        return HIT_BLOCK.test(point) && HIT_ENTITY.test(point);
    };

    public static Predicate<Point> hitEntityExclude(Entity exclude) {
        return point -> !point.getNearbyEntities(exclude, 5, true, 0.1, e -> e instanceof LivingEntity le && !le.isDead()).isEmpty();
    }

    public static Predicate<Point> hitAnythingExclude(Entity exclude) {
        return point -> HIT_BLOCK.test(point) || !point.getNearbyEntities(exclude, 5, true, 0.1, e -> e instanceof LivingEntity le && !le.isDead()).isEmpty();
    }

    public static Predicate<Point> hitEverythingExclude(Entity exclude) {
        return point -> HIT_BLOCK.test(point) && !point.getNearbyEntities(exclude, 5, true, 0.1, e -> e instanceof LivingEntity le && !le.isDead()).isEmpty();
    }


    public static Point trace(Location start, Location end, Predicate<Point> hitCondition) {
        return trace(start, end, 0.5, hitCondition);
    }

    public static Point trace(Location start, Location end, double interval, Predicate<Point> hitCondition) {
        return trace(start, end.toVector().subtract(end.toVector()), end.distance(start), interval, hitCondition);
    }

    public static Point trace(Location start, Vector direction, double distance, Predicate<Point> hitCondition) {
        return trace(start, direction, distance, 0.5, hitCondition);
    }

    public static Point trace(Location start, Vector direction, double distance, double interval, Predicate<Point> hitCondition) {
        if (interval < 0) throw new IllegalArgumentException("interval cannot be zero!");
        if (distance < 0) throw new IllegalArgumentException("distance cannot be zero!");

        for (double i = 0.0; i < distance; i += interval) {
            Point point = blocksInFrontOf(start, direction, i, false);
            if (hitCondition.test(point)) {
                return point;
            }
        }
        return blocksInFrontOf(start, direction, distance, true);
    }

    public static Point blocksInFrontOf(Location loc, Vector dir, double blocks, boolean missed) {
        return new Point(loc.clone().add(dir.getX() * blocks, dir.getY() * blocks, dir.getZ() * blocks), blocks, missed);
    }

    public static class Point {
        private final Location loc;
        private final World world;
        private final Block block;
        private final boolean missed;
        private final double traveledDist;

        private Point(Location loc, double traveledDist, boolean missed) {
            this.loc = loc;
            this.world = loc.getWorld();
            this.block = loc.getBlock();
            this.missed = missed;
            this.traveledDist = traveledDist;

            if (world == null) {
                throw new IllegalArgumentException("point world cannot be null!");
            }
        }

        public List<Entity> getNearbyEntities(Entity exclude, int range, boolean requireContact, double expansionX, double expansionY, double expansionZ, Predicate<Entity> filter) {
            return new ArrayList<>(world.getNearbyEntities(loc, range, range, range, e -> {
                if (requireContact && !e.getBoundingBox().expand(expansionX, expansionY, expansionZ).contains(loc.toVector())) {
                    return false;
                }
                return filter.test(e) && e != exclude;
            }));
        }

        public List<Entity> getNearbyEntities(Entity exclude, int range, boolean requireContact, double expansion, Predicate<Entity> filter) {
            return getNearbyEntities(exclude, range, requireContact, expansion, expansion, expansion, filter);
        }

        public List<Entity> getNearbyEntities(Entity exclude, int range, boolean requireContact, Predicate<Entity> filter) {
            return getNearbyEntities(exclude, range, requireContact, 0, filter);
        }

        public List<Entity> getNearbyEntities(Entity exclude, int range, Predicate<Entity> filter) {
            return getNearbyEntities(exclude, range, false, filter);
        }

        public double getTraveledDist() {
            return traveledDist;
        }

        public boolean wasMissed() {
            return missed;
        }

        public Block getBlock() {
            return block;
        }

        public Location getLoc() {
            return loc;
        }

        public World getWorld() {
            return world;
        }

        public double distance(Location other) {
            return other.distance(loc);
        }
    }
}
