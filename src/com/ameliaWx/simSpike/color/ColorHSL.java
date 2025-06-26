package com.ameliaWx.simSpike.color;

import java.awt.*;

public class ColorHSL {
    public float hue; // degrees
    public float saturation; // 0-1
    public float lightness; // 0-1

    public ColorHSL(float hue, float saturation, float lightness) {
        this.hue = hue;
        this.saturation = saturation;
        this.lightness = lightness;
    }

    public ColorHSL(Color c) {
        ColorHSL temp = ColorHSL.fromRGB(c);
        hue = temp.hue;
        saturation = temp.saturation;
        lightness = temp.lightness;
    }

    public static ColorHSL fromRGB(Color c) {
        float r = c.getRed() / 255.0f;
        float g = c.getGreen() / 255.0f;
        float b = c.getBlue() / 255.0f;

        float M = Float.max(r, Float.max(g, b));
        float m = Float.min(r, Float.min(g, b));
        float C = M - m;

        float huePrime = 0;
        if(C == 0) {
            huePrime = -1024;
        } else if (M == r) {
            huePrime = ((g - b) / C) % 2;
        } else if (M == g) {
            huePrime = ((b - r) / C) + 2;
        } else if (M == b) {
            huePrime = ((r - g) / C) + 4;
        }

        float hue, saturation, lightness;

        if(huePrime == -1024) {
            hue = Float.NaN;
        } else {
            hue = 60 * huePrime;
        }

        // range control
        while (hue < 0) {
            hue += 360;
        }
        while (hue >= 360) {
            hue -= 360;
        }

        lightness = 0.5f * (M + m);

        if (lightness <= 0.5) {
            saturation = C / (2 * lightness);
        } else {
            saturation = C / (2 - 2 * lightness);
        }

        return new ColorHSL(hue, saturation, lightness);
    }

    public Color toRGB() {
        float chroma = (1 - Math.abs(2 * lightness - 1)) * saturation;

        float hue = this.hue;
        if(Float.isNaN(hue)) {
            hue = 0;
        }

        float huePrime = hue / 60;
        float X = chroma * (1 - Math.abs((huePrime % 2) - 1));

        float m = lightness - chroma / 2;

        if(huePrime < 1) {
            return new Color(chroma + m, X + m, m);
        } else if(huePrime < 2) {
            return new Color(X + m, chroma + m, m);
        } else if(huePrime < 3) {
            return new Color(m,chroma + m, X + m);
        } else if(huePrime < 4) {
            return new Color(m, X + m, chroma + m);
        } else if(huePrime < 5) {
            return new Color(X + m, m, chroma + m);
        } else if(huePrime < 6) {
            return new Color(chroma + m, m, X + m);
        } else {
            return new Color(0, 0, 0); // should not be reached, for error detection only.
        }
    }

    public String toString() {
        return String.format("com.ameliaWx.simSpike.color.ColorHSL[h=%.2f,s=%.3f,l=%.3f]", hue, saturation, lightness);
    }
}
