package io.github.itzispyder.pdk.utils.raytracers;

import io.github.itzispyder.pdk.Global;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.bukkit.util.VoxelShape;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class BlockDisplayRaytracer {

    public static List<BlockDisplay> outline(Material display, Location location, long stayTime) {
        return outline(display, location, 0.05, stayTime);
    }

    public static List<BlockDisplay> outline(Material display, Location location, double thickness, long stayTime) {
        Location og = location.getBlock().getLocation();

        Location a1 = og.clone().add(0, 0, 0);
        Location a2 = og.clone().add(1, 0, 0);
        Location a3 = og.clone().add(1, 0, 1);
        Location a4 = og.clone().add(0, 0, 1);

        Location b1 = og.clone().add(0, 1, 0);
        Location b2 = og.clone().add(1, 1, 0);
        Location b3 = og.clone().add(1, 1, 1);
        Location b4 = og.clone().add(0, 1, 1);

        List<BlockDisplay> a = new ArrayList<>();

        a.add(trace(display, a1, a2, thickness, stayTime));
        a.add(trace(display, a2, a3, thickness, stayTime));
        a.add(trace(display, a3, a4, thickness, stayTime));
        a.add(trace(display, a4, a1, thickness, stayTime));

        a.add(trace(display, b1, b2, thickness, stayTime));
        a.add(trace(display, b2, b3, thickness, stayTime));
        a.add(trace(display, b3, b4, thickness, stayTime));
        a.add(trace(display, b4, b1, thickness, stayTime));

        a.add(trace(display, a1, b1, thickness, stayTime));
        a.add(trace(display, a2, b2, thickness, stayTime));
        a.add(trace(display, a3, b3, thickness, stayTime));
        a.add(trace(display, a4, b4, thickness, stayTime));

        return a;
    }

    public static void highlightCollisions(Block block, Color color, long stayTime) {
        if (block == null || block.isEmpty() || !block.isCollidable())
            return;

        VoxelShape shape = block.getCollisionShape();
        World world = block.getWorld();
        Vector offset = block.getLocation().toVector();

        for (BoundingBox box : shape.getBoundingBoxes()) {
            highlight(box, offset, world, color, stayTime);
        }
    }

    public static void highlight(BoundingBox box, Vector offset, World world, Color color, long stayTime) {
        double x1 = box.getMinX() + offset.getX();
        double y1 = box.getMinY() + offset.getY();
        double z1 = box.getMinZ() + offset.getZ();
        double x2 = box.getMaxX() + offset.getX();
        double y2 = box.getMaxY() + offset.getY();
        double z2 = box.getMaxZ() + offset.getZ();

        traceGlowing(world, x1, y1, z1, x2, y1, z1, color, stayTime);
        traceGlowing(world, x2, y1, z1, x2, y1, z2, color, stayTime);
        traceGlowing(world, x2, y1, z2, x1, y1, z2, color, stayTime);
        traceGlowing(world, x1, y1, z2, x1, y1, z1, color, stayTime);

        traceGlowing(world, x1, y2, z1, x2, y2, z1, color, stayTime);
        traceGlowing(world, x2, y2, z1, x2, y2, z2, color, stayTime);
        traceGlowing(world, x2, y2, z2, x1, y2, z2, color, stayTime);
        traceGlowing(world, x1, y2, z2, x1, y2, z1, color, stayTime);

        traceGlowing(world, x1, y1, z1, x1, y2, z1, color, stayTime);
        traceGlowing(world, x2, y1, z1, x2, y2, z1, color, stayTime);
        traceGlowing(world, x2, y1, z2, x2, y2, z2, color, stayTime);
        traceGlowing(world, x1, y1, z2, x1, y2, z2, color, stayTime);
    }

    public static void traceGlowing(World world, double x1, double y1, double z1, double x2, double y2, double z2, Color color, long stayTime) {
        Location loc1 = new Location(world, x1, y1, z1);
        Location loc2 = new Location(world, x2, y2, z2);
        BlockDisplay ent = trace(Material.WHITE_CONCRETE, loc1, loc2, 0.01, stayTime);
        ent.setGlowColorOverride(color);
        ent.setGlowing(true);
    }

    public static BlockDisplay trace(Material display, Location start, Location end, long stayTime) {
        return trace(display, start, end.toVector().subtract(start.toVector()), 0.05, end.distance(start), stayTime);
    }

    public static BlockDisplay trace(Material display, Location start, Location end, double thickness, long stayTime) {
        return trace(display, start, end.toVector().subtract(start.toVector()), thickness, end.distance(start), stayTime);
    }

    public static BlockDisplay trace(Material display, Location start, Vector direction, double thickness, double distance, long stayTime) {
        World world = start.getWorld();

        BlockDisplay entity = world.spawn(start, BlockDisplay.class);
        AxisAngle4f angle = new AxisAngle4f(0, 0, 0, 1);
        Vector3f transition = new Vector3f(-(float)(thickness / 2F));
        Vector3f scale = new Vector3f((float)thickness, (float)thickness, (float)distance);
        Transformation trans = new Transformation(transition, angle, scale, angle);
        Location vector = entity.getLocation();

        vector.setDirection(direction);
        entity.teleport(vector);
        entity.setBlock(display.createBlockData());
        entity.setBrightness(new Display.Brightness(15, 15));
        entity.setInterpolationDelay(0);
        entity.setTransformation(trans);

        JavaPlugin plugin = Global.instance.getPlugin();
        Bukkit.getScheduler().runTaskLater(plugin, entity::remove, stayTime);
        return entity;
    }
}
