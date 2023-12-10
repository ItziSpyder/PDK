package io.github.itzispyder.pdk.utils.misc;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class LocationParser {

    private final double x, y, z;

    public LocationParser(String input) {
        String[] secs = input.replaceAll("[^0-9 -]", "").trim().split(" ");
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;

        for (int i = 0; i < secs.length; i++) {
            if (secs[i].isEmpty()) {
                continue;
            }

            switch (i) {
                case 0 -> x = Double.parseDouble(secs[i]);
                case 1 -> y = Double.parseDouble(secs[i]);
                case 2 -> z = Double.parseDouble(secs[i]);
            }
            if (i >= 3) {
                break;
            }
        }

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public LocationParser(String input, Location relativeTo) {
        String[] secs = input.replaceAll("[^0-9 ~-]", "").trim().split(" ");
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;

        for (int i = 0; i < secs.length; i++) {
            switch (i) {
                case 0 -> {
                    String parsing = secs[i].replaceAll("[^0-9-]", "");
                    if (!parsing.isEmpty()) {
                        x = Double.parseDouble(parsing);
                    }
                    x = secs[i].contains("~") ? relativeTo.getX() + x : x;
                }
                case 1 -> {
                    String parsing = secs[i].replaceAll("[^0-9-]", "");
                    if (!parsing.isEmpty()) {
                        y = Double.parseDouble(parsing);
                    }
                    y = secs[i].contains("~") ? relativeTo.getY() + y : y;
                }
                case 2 -> {
                    String parsing = secs[i].replaceAll("[^0-9-]", "");
                    if (!parsing.isEmpty()) {
                        z = Double.parseDouble(parsing);
                    }
                    z = secs[i].contains("~") ? relativeTo.getZ() + z : z;
                }
            }
            if (i >= 3) {
                break;
            }
        }

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public LocationParser(String x, String y, String z) {
        this(x + " " + y + " " + z);
    }

    public LocationParser(String x, String y, String z, Location relativeTo) {
        this(x + " " + y + " " + z, relativeTo);
    }

    public Location getLocation(World world) {
        return new Location(world, x, y, z);
    }

    public Block getBlock(World world) {
        return getLocation(world).getBlock();
    }

    public Location getBlockLocation(World world) {
        return getBlock(world).getLocation();
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
