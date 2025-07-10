package com.ameliaWx.simSpike.test;

import com.ameliaWx.simSpike.optics.Aperture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ApertureTest {
    public static void main(String[] args) throws IOException {
        testHSTAperture();
    }

    public static void testHSTAperture() throws IOException {
        BufferedImage testAperture = ImageIO.read(new File("test-aperture.png"));

        Aperture hstAperture = new Aperture(testAperture, 30, 30);

        System.out.println(Arrays.deepToString(hstAperture.apertureMap));
    }
}