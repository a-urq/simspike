package com.ameliaWx.simSpike.test;

import com.ameliaWx.simSpike.optics.Aperture;
import com.ameliaWx.simSpike.optics.DiffractionKernel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ApertureTest {
    public static void main(String[] args) throws IOException {
        testHSTAperture();
    }

    public static void testHSTAperture() throws IOException {
        BufferedImage testAperture = ImageIO.read(new File("test-apertures/circle-aperture-small.png"));

        Aperture hstAperture = new Aperture(testAperture, 1, 1);

        DiffractionKernel kernel = new DiffractionKernel(hstAperture);

        ImageIO.write(renderKernel(kernel.kernelRed), "PNG", new File("diffraction-tests/circleSmall-diffraction-red.png"));
        ImageIO.write(renderKernel(kernel.kernelYellow), "PNG", new File("diffraction-tests/circleSmall-diffraction-yellow.png"));
        ImageIO.write(renderKernel(kernel.kernelGreen), "PNG", new File("diffraction-tests/circleSmall-diffraction-green.png"));
        ImageIO.write(renderKernel(kernel.kernelCyan), "PNG", new File("diffraction-tests/circleSmall-diffraction-cyan.png"));
        ImageIO.write(renderKernel(kernel.kernelBlue), "PNG", new File("diffraction-tests/circleSmall-diffraction-blue.png"));
        ImageIO.write(renderKernel(kernel.kernelViolet), "PNG", new File("diffraction-tests/circleSmall-diffraction-violet.png"));
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

                luminosity = gammaCorrect(luminosity, 1.5f);
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