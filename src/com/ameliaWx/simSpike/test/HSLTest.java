package com.ameliaWx.simSpike.test;

import com.ameliaWx.simSpike.color.ColorHSL;

import java.awt.*;

public class HSLTest {
    // TODO: Make a test that splits an RGB image into H, S, and L components and then exports each channel into an image of its own. S and L will be grayscale while H will be very very multicolored.
    public static void main(String[] args) {
        testSingleHSLColor();
    }

    private static void testSingleHSLColor() {
        Color testColor = new Color(200, 0, 64);

        ColorHSL cHSL = ColorHSL.fromRGB(testColor);

        System.out.println(cHSL);

        Color testColor1 = cHSL.toRGB();

        System.out.println(testColor1);
    }
}
