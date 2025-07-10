package com.ameliaWx.simSpike.optics;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Aperture {
    public float[][] apertureMap;
    public float xMax; // x maximum, inclusive
    public float yMax; // y maximum, inclusive

    public Aperture(BufferedImage img, float xMax, float yMax) {
        this.xMax = xMax;
        this.yMax = yMax;

        apertureMap = new float[img.getWidth()][img.getHeight()];
        for(int i = 0; i < apertureMap.length; i++) {
            for(int j = 0; j < apertureMap[i].length; j++) {
                Color c = new Color(img.getRGB(i, j));

                float luminance = (c.getRed() + c.getGreen() + c.getBlue())/3.0f;

                apertureMap[i][j] = luminance/255.0f;
            }
        }
    }
}
