package io.github.itzispyder.pdk.utils.misc;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Rotations {

    public static Vector rotateVector(Vector subject, Vector pivot, float pitch, float yaw) {
        Quaternionf rotationPitch = new Quaternionf().rotationX((float) Math.toRadians(pitch));
        Quaternionf rotationYaw = new Quaternionf().rotationY((float) Math.toRadians(yaw));
        Quaternionf rotation = rotationPitch.mul(rotationYaw);

        Vector3f translated = subject.subtract(pivot).toVector3f();
        translated = rotation.transform(translated).add(pivot.toVector3f());

        return new Vector(translated.x, translated.y, translated.z);
    }

    public static Location rotateVector(Location subject, Location origin, float pitch, float yaw) {
        Quaternionf rotationPitch = new Quaternionf().rotationX((float) Math.toRadians(pitch));
        Quaternionf rotationYaw = new Quaternionf().rotationY((float) Math.toRadians(yaw));
        Quaternionf rotation = rotationPitch.mul(rotationYaw);

        Vector3f translated = subject.subtract(origin).toVector().toVector3f();
        translated = rotation.transform(translated).add(origin.toVector().toVector3f());

        return new Location(subject.getWorld(), translated.x, translated.y, translated.z);
    }

    public static Location rotateVector(Location subject, Location originAndRotation) {
        return rotateVector(subject, originAndRotation, originAndRotation.getPitch(), originAndRotation.getYaw());
    }
}