package com.ameliaWx.simSpike.test;

import com.ameliaWx.simSpike.color.ColorRYGCBV;
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

    private static final String apertureName = "hst-aperture-thick-big";
    private static final boolean useGPU = true;
    public static void testHSTAperture() throws IOException {
        long startTime = System.currentTimeMillis();
        BufferedImage testAperture = ImageIO.read(new File("test-apertures/" + apertureName + ".png"));

        Aperture hstAperture = new Aperture(testAperture, 1, 1);

        PointSpreadFunction psf = new PointSpreadFunction(hstAperture, useGPU);

        String caseTestName = apertureName + (useGPU ? "-gpu" : "");

        ImageIO.write(renderPSF(psf, 1.0f), "PNG", new File("diffraction-tests/" + caseTestName + "-g1-0.png"));
        ImageIO.write(renderPSF(psf, 1.5f), "PNG", new File("diffraction-tests/" + caseTestName + "-g1-5.png"));
        ImageIO.write(renderPSF(psf, 2.2f), "PNG", new File("diffraction-tests/" + caseTestName + "-g2-2.png"));
        ImageIO.write(renderPSF(psf, 2.8f), "PNG", new File("diffraction-tests/" + caseTestName + "-g2-8.png"));
        ImageIO.write(renderPSF(psf, 3.5f), "PNG", new File("diffraction-tests/" + caseTestName + "-g3-5.png"));
        ImageIO.write(renderPSF(psf, 4.0f), "PNG", new File("diffraction-tests/" + caseTestName + "-g4-0.png"));
        long endTime = System.currentTimeMillis();

        System.out.printf("PSF Test Time: %.3f s", (endTime - startTime)/1000.0);
    }

    private static BufferedImage renderPSF(PointSpreadFunction psf) {
        return renderPSF(psf, 1.0f);
    }

    private static BufferedImage renderPSF(PointSpreadFunction psf, float gamma) {
        ColorRYGCBV[][] colors = new ColorRYGCBV[psf.psfRed.length][psf.psfRed[0].length];
        BufferedImage img = new BufferedImage(colors.length, colors[0].length, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = img.createGraphics();

        for(int i = 0; i < colors.length; i++) {
            for (int j = 0; j < colors[i].length; j++) {
                colors[i][j] = new ColorRYGCBV(gammaCorrect(psf.psfRed[i][j], gamma),
                        gammaCorrect(psf.psfYellow[i][j], gamma),
                        gammaCorrect(psf.psfGreen[i][j], gamma),
                        gammaCorrect(psf.psfCyan[i][j], gamma),
                        gammaCorrect(psf.psfBlue[i][j], gamma),
                        gammaCorrect(psf.psfViolet[i][j], gamma));

                g.setColor(correctGreenCast(colors[i][j].toRGB()));
                g.fillRect(i, j, 1, 1);
            }
        }

        return img;
    }

    private static Color correctGreenCast(Color c) {
        return new Color(c.getRed(), (int) (0.8 * c.getGreen()), c.getBlue());
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