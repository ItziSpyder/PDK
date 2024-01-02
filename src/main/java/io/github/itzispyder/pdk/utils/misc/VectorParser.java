package io.github.itzispyder.pdk.utils.misc;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.function.Function;
import java.util.function.Predicate;

public class VectorParser {

    private static final String REGEX_DECIMAL = "[^0-9 .-]";
    private static final String REGEX_RELATIVE = "[^0-9 ^~.-]";
    private static final Function<String, String> toNumber = s -> s.replaceAll(REGEX_DECIMAL, "").trim();
    private static final Function<String, String> toRelation = s -> s.replaceAll(REGEX_RELATIVE, "").trim();
    private static final Predicate<String> isRelative = s -> s.startsWith("~") || s.startsWith("^");
    private final double x, y, z;

    public VectorParser(String arg1, String arg2, String arg3) {
        arg1 = toNumber.apply(arg1);
        arg2 = toNumber.apply(arg2);
        arg3 = toNumber.apply(arg3);
        double argX = 0.0;
        double argY = 0.0;
        double argZ = 0.0;

        if (!arg1.isEmpty()) {
            argX = Double.parseDouble(arg1);
        }
        if (!arg2.isEmpty()) {
            argY = Double.parseDouble(arg2);
        }
        if (!arg3.isEmpty()) {
            argZ = Double.parseDouble(arg3);
        }

        this.x = argX;
        this.y = argY;
        this.z = argZ;
    }

    public VectorParser(String arg1, String arg2, String arg3, Entity relativeTo) {
        this(arg1, arg2, arg3, relativeTo.getLocation());
    }

    public VectorParser(String arg1, String arg2, String arg3, Location relativeTo) {
        arg1 = toRelation.apply(arg1);
        arg2 = toRelation.apply(arg2);
        arg3 = toRelation.apply(arg3);
        double argX = 0.0;
        double argY = 0.0;
        double argZ = 0.0;
        String argNum1 = toNumber.apply(arg1);
        String argNum2 = toNumber.apply(arg2);
        String argNum3 = toNumber.apply(arg3);

        if (!argNum1.isEmpty()) {
            argX = Double.parseDouble(argNum1);
        }
        if (!argNum2.isEmpty()) {
            argY = Double.parseDouble(argNum2);
        }
        if (!argNum3.isEmpty()) {
            argZ = Double.parseDouble(argNum3);
        }

        Vector result = relativeTo.getDirection();

        if (arg1.startsWith("~")) {
            result = result.add(new Vector(argX, 0, 0));
        }
        if (arg2.startsWith("~")) {
            result = result.add(new Vector(0, argY, 0));
        }
        if (arg3.startsWith("~")) {
            result = result.add(new Vector(0, 0, argZ));
        }

        float pitch = relativeTo.getPitch();
        float yaw = relativeTo.getPitch();

        if (arg1.startsWith("^")) {
            int angle = argX == 0 ? 0 : (argX < 0 ? -90 : 90);
            result = distInFront(result, 0, yaw + angle, Math.abs(argX));
        }
        if (arg2.startsWith("^")) {
            int angle = argY == 0 ? 0 : (argY < 0 ? 90 : -90);
            result = distInFront(result, pitch + angle, 0, Math.abs(argY));
        }
        if (arg3.startsWith("^")) {
            result = distInFront(result, pitch, yaw, argZ);
        }

        this.x = isRelative.test(arg1) ? result.getX() : argX;
        this.y = isRelative.test(arg2) ? result.getY() : argY;
        this.z = isRelative.test(arg3) ? result.getZ() : argZ;
    }

    public static Vector distInFront(Vector start, Vector dir, double dist) {
        return start.add(new Vector(dir.getX() * dist, dir.getY() * dist, dir.getZ() * dist));
    }

    public static Vector distInFront(Vector start, float pitch, float yaw, double dist) {
        return distInFront(start, new Location(null, 0, 0, 0, pitch, yaw).getDirection(), dist);
    }

    public Vector getVector() {
        return new Vector(x, y, z);
    }

    public Location getBlockPos(World world) {
        return new Location(world, x, y, z);
    }

    public Block getBlock(World world) {
        return getBlockPos(world).getBlock();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
