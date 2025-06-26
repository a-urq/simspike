package com.ameliaWx.simSpike.math;

public class Vector2D {
    float x;
    float y;

    public Vector2D (float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }

    public Vector2D mult(float k) {
        return new Vector2D(k * x, k * y);
    }

    public float dot(Vector2D v) {
        return x * v.x + y * v.y;
    }
}
