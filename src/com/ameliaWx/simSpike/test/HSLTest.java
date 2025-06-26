package com.ameliaWx.simSpike.test;

import com.ameliaWx.simSpike.color.ColorHSL;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

public class HSLTest {
    // TODO: Find a way to estimate color temperature from RGB
    public static void main(String[] args) throws IOException {
        testSingleHSLColor();
        splitImage();
    }

    private static void testSingleHSLColor() {
        Color testColor = new Color(200, 0, 64);

        ColorHSL cHSL = ColorHSL.fromRGB(testColor);

        System.out.println(cHSL);

        Color testColor1 = cHSL.toRGB();

        System.out.println(testColor1);
    }

    private static void splitImage() throws IOException {
        BufferedImage image = ImageIO.read(new File("/media/nvme1/Astrophotography/2025-06-23 [EotER] Rho Ophiuchi 135mm/result_9660s_final_siril_q95.jpg"));

        ColorHSL[][] hsl = new ColorHSL[image.getWidth()][image.getHeight()];

        for(int i = 0; i < hsl.length; i++) {
            for(int j = 0; j < hsl[i].length; j++) {
                hsl[i][j] = ColorHSL.fromRGB(new Color(image.getRGB(i, j)));
            }
        }

        BufferedImage hueImg = new BufferedImage(hsl.length, hsl[0].length, BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage saturationImg = new BufferedImage(hsl.length, hsl[0].length, BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage lightnessImg = new BufferedImage(hsl.length, hsl[0].length, BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage hueSatImg = new BufferedImage(hsl.length, hsl[0].length, BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D h = hueImg.createGraphics();
        Graphics2D s = saturationImg.createGraphics();
        Graphics2D l = lightnessImg.createGraphics();
        Graphics2D hs = hueSatImg.createGraphics();

        for(int i = 0; i < hsl.length; i++) {
            for (int j = 0; j < hsl[i].length; j++) {
                h.setColor(new ColorHSL(hsl[i][j].hue, 1.0f, 0.5f).toRGB());
                h.fillRect(i, j, 1, 1);

                s.setColor(new ColorHSL(0.0f, 0.0f, hsl[i][j].saturation).toRGB());
                s.fillRect(i, j, 1, 1);

                l.setColor(new ColorHSL(0.0f, 0.0f, hsl[i][j].lightness).toRGB());
                l.fillRect(i, j, 1, 1);

                hs.setColor(new ColorHSL(hsl[i][j].hue, hsl[i][j].saturation, 0.5f).toRGB());
                hs.fillRect(i, j, 1, 1);
            }
        }

        ImageIO.write(hueImg, "JPG", new File("split-hue.jpg"));
        ImageIO.write(saturationImg, "JPG", new File("split-saturation.jpg"));
        ImageIO.write(lightnessImg, "JPG", new File("split-lightness.jpg"));
        ImageIO.write(hueSatImg, "JPG", new File("split-hue-sat.jpg"));
    }
}
