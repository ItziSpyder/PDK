package io.github.itzispyder.pdk.utils.misc;

import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface Rotations {

    interface Axes {
        Axes X = ang -> new Quaternionf().rotateX(ang);
        Axes Y = ang -> new Quaternionf().rotateY(ang);
        Axes Z = ang -> new Quaternionf().rotateZ(ang);

        Quaternionf rotateRadians(float radians);

        default Quaternionf rotateDegrees(float degrees) {
            return rotateRadians((float) Math.toRadians(degrees));
        }

        default Vector rotateVectorRadians(Vector vector, float radians) {
            return rotateVectorRadians((float)vector.getX(), (float)vector.getY(), (float)vector.getZ(), radians);
        }

        default Vector rotateVectorDegrees(Vector vector, float degrees) {
            return rotateVectorDegrees((float)vector.getX(), (float)vector.getY(), (float)vector.getZ(), degrees);
        }

        default Vector rotateVectorRadians(float x, float y, float z, float radians) {
            Vector3f vec = rotateRadians(radians).transform(new Vector3f(x, y, z));
            return new Vector(vec.x, vec.y, vec.z);
        }

        default Vector rotateVectorDegrees(float x, float y, float z, float degrees) {
            Vector3f vec = rotateDegrees(degrees).transform(new Vector3f(x, y, z));
            return new Vector(vec.x, vec.y, vec.z);
        }
    }

    interface Vectors {
        Vectors XYZ = (x, y, z) -> new Quaternionf().rotateXYZ(x, y, z);

        Quaternionf rotateRadians(float x, float y, float z);

        default Quaternionf rotateDegrees(float x, float y, float z) {
            return rotateRadians((float)Math.toRadians(x), (float)Math.toRadians(y), (float)Math.toRadians(z));
        }

        default Vector rotateVectorRadians(Vector vector, float rx, float ry, float rz) {
            return rotateVectorRadians((float)vector.getX(), (float)vector.getY(), (float)vector.getZ(), rx, ry, rz, 0, 0, 0);
        }

        default Vector rotateVectorDegrees(Vector vector, float dx, float dy, float dz) {
            return rotateVectorDegrees((float)vector.getX(), (float)vector.getY(), (float)vector.getZ(), dx, dy, dz, 0, 0, 0);
        }

        default Vector rotateVectorRadians(Vector vector, float rx, float ry, float rz, float ox, float oy, float oz) {
            return rotateVectorRadians((float)vector.getX(), (float)vector.getY(), (float)vector.getZ(), rx, ry, rz, ox, oy, oz);
        }

        default Vector rotateVectorDegrees(Vector vector, float dx, float dy, float dz, float ox, float oy, float oz) {
            return rotateVectorDegrees((float)vector.getX(), (float)vector.getY(), (float)vector.getZ(), dx, dy, dz, ox, oy, oz);
        }

        default Vector rotateVectorRadians(float x, float y, float z, float rx, float ry, float rz, float ox, float oy, float oz) {
            Vector3f vecDiffer = new Vector3f(x, y, z).sub(ox, oy, oz);
            Vector3f vecDifferRot = rotateRadians(rx, ry, rz).transform(vecDiffer);
            Vector3f vec = vecDifferRot.add(ox, oy, oz);
            return new Vector(vec.x, vec.y, vec.z);
        }

        default Vector rotateVectorDegrees(float x, float y, float z, float dx, float dy, float dz, float ox, float oy, float oz) {
            Vector3f vecDiffer = new Vector3f(x, y, z).sub(ox, oy, oz);
            Vector3f vecDifferRot = rotateDegrees(dx, dy, dz).transform(vecDiffer);
            Vector3f vec = vecDifferRot.add(ox, oy, oz);
            return new Vector(vec.x, vec.y, vec.z);
        }
    }

    interface TriFunction<X, Y, Z, R> {
        R apply(X x, Y y, Z z);
    }
}
