package com.ameliaWx.simSpike.color;

import java.awt.*;

// Supports six-channel colors and provides easy conversion to RGB
public class ColorRYGCBV {
    public float r;
    public float y;
    public float g;
    public float c;
    public float b;
    public float v;

    private static final float[][] RGB_CONVERSION_MATRIX = new float[][] {
            new float[] {0.33f, 0,     0},     // R channel to RGB
            new float[] {0.25f, 0.25f, 0},     // Y channel to RGB
            new float[] {0,     0.5f,  0},     // G channel to RGB
            new float[] {0,     0.25f, 0.25f}, // C channel to RGB
            new float[] {0,     0,     0.5f},  // B channel to RGB
            new float[] {0.17f, 0,     0.25f}, // V channel to RGB
    };

    public ColorRYGCBV(float r, float y, float g, float c, float b, float v) {
        this.r = r;
        this.y = y;
        this.g = g;
        this.c = c;
        this.b = b;
        this.v = v;
    }

    public Color toRGB() {
        float R = RGB_CONVERSION_MATRIX[0][0] * r + RGB_CONVERSION_MATRIX[1][0] * y + RGB_CONVERSION_MATRIX[2][0] * g + RGB_CONVERSION_MATRIX[3][0] * c + RGB_CONVERSION_MATRIX[4][0] * b + RGB_CONVERSION_MATRIX[5][0] * v;
        float G = RGB_CONVERSION_MATRIX[0][1] * r + RGB_CONVERSION_MATRIX[1][1] * y + RGB_CONVERSION_MATRIX[2][1] * g + RGB_CONVERSION_MATRIX[3][1] * c + RGB_CONVERSION_MATRIX[4][1] * b + RGB_CONVERSION_MATRIX[5][1] * v;
        float B = RGB_CONVERSION_MATRIX[0][2] * r + RGB_CONVERSION_MATRIX[1][2] * y + RGB_CONVERSION_MATRIX[2][2] * g + RGB_CONVERSION_MATRIX[3][2] * c + RGB_CONVERSION_MATRIX[4][2] * b + RGB_CONVERSION_MATRIX[5][2] * v;

        if(R < 0) {
            R = 0;
        }
        if(R > 1) {
            R = 1;
        }
        if(G < 0) {
            G = 0;
        }
        if(G > 1) {
            G = 1;
        }
        if(B < 0) {
            B = 0;
        }
        if(B > 1) {
            B = 1;
        }

        return new Color(R, G, B);
    }
}
