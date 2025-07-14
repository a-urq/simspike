package com.ameliaWx.simSpike.test;

import com.ameliaWx.simSpike.optics.Aperture;
import com.ameliaWx.simSpike.optics.PointSpreadFunction;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ApertureTest {
    public static void main(String[] args) throws IOException {
        testHSTAperture();
    }

    public static void testHSTAperture() throws IOException {
        BufferedImage testAperture = ImageIO.read(new File("test-apertures/double-slit-aperture.png"));

        Aperture hstAperture = new Aperture(testAperture, 1, 1);

        PointSpreadFunction kernel = new PointSpreadFunction(hstAperture, true);

        System.exit(0);

        ImageIO.write(renderKernel(kernel.psfRed), "PNG", new File("diffraction-tests/double-slit-diffraction-red.png"));
        ImageIO.write(renderKernel(kernel.psfYellow), "PNG", new File("diffraction-tests/double-slit-diffraction-yellow.png"));
        ImageIO.write(renderKernel(kernel.psfGreen), "PNG", new File("diffraction-tests/double-slit-diffraction-green.png"));
        ImageIO.write(renderKernel(kernel.psfCyan), "PNG", new File("diffraction-tests/double-slit-diffraction-cyan.png"));
        ImageIO.write(renderKernel(kernel.psfBlue), "PNG", new File("diffraction-tests/double-slit-diffraction-blue.png"));
        ImageIO.write(renderKernel(kernel.psfViolet), "PNG", new File("diffraction-tests/double-slit-diffraction-violet.png"));
    }

    private static BufferedImage renderKernel(float[][] kernel) {
        BufferedImage kernelImg = new BufferedImage(kernel.length, kernel[0].length, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = kernelImg.createGraphics();

        for(int i = 0; i < kernel.length; i++) {
            for (int j = 0; j < kernel[i].length; j++) {
                float luminosity = kernel[i][j];
                if(luminosity > 1) {
                    System.out.println(luminosity);
                    luminosity = 1;
                }

                luminosity = gammaCorrect(luminosity, 2.2f);
                g.setColor(new Color(luminosity, luminosity, luminosity));
                g.fillRect(i, j, 1, 1);
            }
        }

        return kernelImg;
    }

    private static float gammaCorrect(float val, float gamma) {
        return (float) Math.pow(val, 1 / gamma);
    }
}