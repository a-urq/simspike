package com.ameliaWx.simSpike.optics;

public class DiffractionKernel {
    // have red, yellow, green, cyan, blue, and violet kernels
    // should all be the same pattern, but a different scale
    // and acting on different channels of one RGB image

    // figure out scaling and testing and shit lol

    public float[][] kernelRed;
    public float[][] kernelYellow;
    public float[][] kernelGreen;
    public float[][] kernelCyan;
    public float[][] kernelBlue;
    public float[][] kernelViolet;

    // All in nanometers
    private static final float WAVELENGTH_RED = 599;
    private static final float WAVELENGTH_YELLOW = 578;
    private static final float WAVELENGTH_GREEN = 555;
    private static final float WAVELENGTH_CYAN = 498;
    private static final float WAVELENGTH_BLUE = 446;
    private static final float WAVELENGTH_VIOLET = 400;

    public DiffractionKernel(Aperture a) {
        kernelRed = new float[a.apertureMap.length][a.apertureMap[0].length];
        kernelYellow = new float[a.apertureMap.length][a.apertureMap[0].length];
        kernelGreen = new float[a.apertureMap.length][a.apertureMap[0].length];
        kernelCyan = new float[a.apertureMap.length][a.apertureMap[0].length];
        kernelBlue = new float[a.apertureMap.length][a.apertureMap[0].length];
        kernelViolet = new float[a.apertureMap.length][a.apertureMap[0].length];

        float dx1 = 2.0f * a.xMax / (a.apertureMap.length - 1);
        float dy1 = 2.0f * a.yMax / (a.apertureMap[0].length - 1);
        float x1_0 = -a.xMax;
        float y1_0 = -a.yMax;
        final float Z = 400.0f;
        for(int i = 0; i < kernelRed.length; i++) {
            for(int j = 0; j < kernelRed[0].length; j++) {
                System.out.println(i + "\t" + j);
                float x1 = x1_0 + i * dx1;
                float y1 = y1_0 + j * dy1;

                // can probably do some really good parallelization on this
                kernelRed[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, WAVELENGTH_RED, x1, y1, Z);
                kernelYellow[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, WAVELENGTH_YELLOW, x1, y1, Z);
                kernelGreen[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, WAVELENGTH_GREEN, x1, y1, Z);
                kernelCyan[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, WAVELENGTH_CYAN, x1, y1, Z);
                kernelBlue[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, WAVELENGTH_BLUE, x1, y1, Z);
                kernelViolet[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, WAVELENGTH_VIOLET, x1, y1, Z);
            }
        }

        // should probably divide by the max value in any kernel rather than just normalize within kernels but eh
        normalizeKernel(kernelRed);
        normalizeKernel(kernelYellow);
        normalizeKernel(kernelGreen);
        normalizeKernel(kernelCyan);
        normalizeKernel(kernelBlue);
        normalizeKernel(kernelViolet);
    }

    private void normalizeKernel(float[][] kernel) {
        float maxValue = max(kernel);

        for(int i = 0; i < kernel.length; i++) {
            for(int j = 0; j < kernel[i].length; j++) {
                kernel[i][j] /= maxValue;
            }
        }
    }

    private float max(float[][] arr) {
        float max = -Float.MAX_VALUE;

        for(int i = 0; i < arr.length; i++) {
            for(int j = 0; j < arr[i].length; j++) {
                max = Float.max(max, arr[i][j]);
            }
        }

        return max;
    }
}
