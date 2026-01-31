package com.me.coresmodule.utils.math;

import net.minecraft.util.math.Vec3d;

public class CmVectors {

    public double x;
    public double y;
    public double z;

    public CmVectors(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double distanceTo(CmVectors other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double dz = other.z - this.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double distanceTo(double x, double y, double z) {
        double dx = x - this.x;
        double dy = y - this.y;
        double dz = z - this.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public CmVectors add(CmVectors other) {
        return new CmVectors(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public CmVectors subtract(CmVectors other) {
        return new CmVectors(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public CmVectors multiply(double d) {
        return new CmVectors(this.x * d, this.y * d, this.z * d);
    }

    public CmVectors copy() {
        return new CmVectors(this.x, this.y, this.z);
    }

    public CmVectors down(double amount) {
        return new CmVectors(this.x, this.y - amount, this.z);
    }

    public CmVectors roundLocationToBlock() {
        return new CmVectors(
                Math.floor(this.x),
                Math.floor(this.y),
                Math.floor(this.z)
        );
    }

    public Vec3d toVec3d() {
        return new Vec3d(this.x, this.y, this.z);
    }

    public CmVectors center() {
        return new CmVectors(this.x + 0.5, this.y + 0.5, this.z + 0.5);
    }

    public String toCleanString() {
        return String.format("%.2f, %.2f, %.2f", this.x, this.y, this.z);
    }

    public double[] toDoubleArray() {
        return new double[]{this.x, this.y, this.z};
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public static CmVectors fromArray(double[] arr) {
        if (arr.length < 3) throw new IllegalArgumentException("Array must contain at least 3 elements.");
        return new CmVectors(arr[0], arr[1], arr[2]);
    }
}