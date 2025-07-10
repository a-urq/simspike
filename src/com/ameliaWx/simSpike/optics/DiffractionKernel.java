package com.ameliaWx.simSpike.optics;

public class DiffractionKernel {
    // have red, yellow, green, cyan, blue, and violet kernels
    // should all be the same pattern, but a different scale
    // and acting on different channels of one RGB image

    // figure out scaling and testing and shit lol

    float[][] kernelRed;
    float[][] kernelYellow;
    float[][] kernelGreen;
    float[][] kernelCyan;
    float[][] kernelBlue;
    float[][] kernelViolet;

    public DiffractionKernel(Aperture a) {
        kernelRed = new float[2 * a.apertureMap.length - 1][2 * a.apertureMap[0].length - 1];
        kernelYellow = new float[2 * a.apertureMap.length - 1][2 * a.apertureMap[0].length - 1];
        kernelGreen = new float[2 * a.apertureMap.length - 1][2 * a.apertureMap[0].length - 1];
        kernelCyan = new float[2 * a.apertureMap.length - 1][2 * a.apertureMap[0].length - 1];
        kernelBlue = new float[2 * a.apertureMap.length - 1][2 * a.apertureMap[0].length - 1];
        kernelViolet = new float[2 * a.apertureMap.length - 1][2 * a.apertureMap[0].length - 1];

        float x1_0 = -a.xMax * 2;
        float y1_0 = -a.yMax * 2;
        float dx1 = 0.5f * a.xMax / a.apertureMap.length;
        float dy1 = 0.5f * a.yMax / a.apertureMap[0].length;
        final float Z = 100.0f;
        for(int i = 0; i < kernelRed.length; i++) {
            for(int j = 0; j < kernelRed[0].length; j++) {
                float x1 = x1_0 + i * dx1;
                float y1 = y1_0 + i * dy1;

                kernelRed[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, 599, x1, y1, Z);
                kernelYellow[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, 578, x1, y1, Z);
                kernelGreen[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, 555, x1, y1, Z);
                kernelCyan[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, 498, x1, y1, Z);
                kernelBlue[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, 446, x1, y1, Z);
                kernelViolet[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, 400, x1, y1, Z);
            }
        }
    }
}
