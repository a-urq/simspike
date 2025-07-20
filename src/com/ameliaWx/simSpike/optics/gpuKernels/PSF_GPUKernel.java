package com.ameliaWx.simSpike.optics.gpuKernels;

import com.ameliaWx.simSpike.optics.Aperture;
import com.aparapi.Kernel;

public class PSF_GPUKernel extends Kernel {
    public float[] apertureMap1D;
    public int apertureMapLengthI;
    public int apertureMapLengthJ;
    public float a_xMax;
    public float a_yMax;
    public float zRecip; // must be at least twice the pixel width of the aperture map to avoid nyquisting (at least before interpolation)

    public float[] psfRed1D;
    public float[] psfYellow1D;
    public float[] psfGreen1D;
    public float[] psfCyan1D;
    public float[] psfBlue1D;
    public float[] psfViolet1D;

    public float[][] psfRed;
    public float[][] psfYellow;
    public float[][] psfGreen;
    public float[][] psfCyan;
    public float[][] psfBlue;
    public float[][] psfViolet;

    // All in nanometers
    private static final float WAVELENGTH_RED = 599;
    private static final float WAVELENGTH_YELLOW = 578;
    private static final float WAVELENGTH_GREEN = 555;
    private static final float WAVELENGTH_CYAN = 498;
    private static final float WAVELENGTH_BLUE = 446;
    private static final float WAVELENGTH_VIOLET = 400;

    public void setup(Aperture a, float z) {
        this.a_xMax = a.xMax;
        this.a_yMax = a.yMax;
        this.apertureMap1D = flattenArray(a.apertureMap);
        this.apertureMapLengthI = a.apertureMap.length;
        this.apertureMapLengthJ = a.apertureMap[0].length;
        this.zRecip = 1.0f/z;

        psfRed1D = new float[apertureMapLengthI * apertureMapLengthJ];
        psfYellow1D = new float[apertureMapLengthI * apertureMapLengthJ];
        psfGreen1D = new float[apertureMapLengthI * apertureMapLengthJ];
        psfCyan1D = new float[apertureMapLengthI * apertureMapLengthJ];
        psfBlue1D = new float[apertureMapLengthI * apertureMapLengthJ];
        psfViolet1D = new float[apertureMapLengthI * apertureMapLengthJ];
    }

    public void finish() {
        psfRed = reconstructArray(psfRed1D, apertureMapLengthI, apertureMapLengthJ);
        psfYellow = reconstructArray(psfYellow1D, apertureMapLengthI, apertureMapLengthJ);
        psfGreen = reconstructArray(psfGreen1D, apertureMapLengthI, apertureMapLengthJ);
        psfCyan = reconstructArray(psfCyan1D, apertureMapLengthI, apertureMapLengthJ);
        psfBlue = reconstructArray(psfBlue1D, apertureMapLengthI, apertureMapLengthJ);
        psfViolet = reconstructArray(psfViolet1D, apertureMapLengthI, apertureMapLengthJ);
    }

    @Override
    public void run() {
        int gid = getGlobalId();
        int i0 = gid / apertureMapLengthJ;
        int j0 = gid % apertureMapLengthJ;

        float dx1 = 2.0f * a_xMax / (apertureMapLengthI - 1);
        float dy1 = 2.0f * a_yMax / (apertureMapLengthJ - 1);
        float x1_0 = -a_xMax;
        float y1_0 = -a_yMax;

        float x1 = x1_0 + i0 * dx1;
        float y1 = y1_0 + j0 * dy1;

        // can probably do some really good parallelization on this
        psfRed1D[gid] = irradianceAtDistance(WAVELENGTH_RED, x1, y1, zRecip);
        psfYellow1D[gid] = irradianceAtDistance(WAVELENGTH_YELLOW, x1, y1, zRecip);
        psfGreen1D[gid] = irradianceAtDistance(WAVELENGTH_GREEN, x1, y1, zRecip);
        psfCyan1D[gid] = irradianceAtDistance(WAVELENGTH_CYAN, x1, y1, zRecip);
        psfBlue1D[gid] = irradianceAtDistance(WAVELENGTH_BLUE, x1, y1, zRecip);
        psfViolet1D[gid] = irradianceAtDistance(WAVELENGTH_VIOLET, x1, y1, zRecip);
    }

    public float irradianceAtDistance(float wavelengthNm, float x1, float y1, float zRecip) {
        float dy0 = 2.0f * a_yMax / (apertureMapLengthJ - 1);
        float dx0 = 2.0f * a_xMax / (apertureMapLengthI - 1);

        float wavelengthMm = wavelengthNm / 1000000;
        float k = (2 * 3.1415926535897f / wavelengthMm); // wavenumber

        int i;
        int j = 0;

        // figure out how to handle the complex math
        float sumRe = 0;
        float sumIm = 0;
        for(float y0 = -a_yMax; y0 <= a_yMax; y0+=dy0) {
            i = 0;
            for(float x0 = -a_xMax; x0 <= a_xMax; x0+=dx0) {
                float dotProd = x0 * x1 + y0 * y1;
                float kz = -k*zRecip;
                float exponentOfExpI_re = 0;
                float exponentOfExpI_im = kz*dotProd;

                float expRe = exp(exponentOfExpI_re);

                float expI_re = expRe * cos(exponentOfExpI_im);
                float expI_im = expRe * sin(exponentOfExpI_im);

                float A = accessArray(apertureMap1D, i, j, apertureMapLengthJ);
                expI_re *= A;
                expI_im *= A;

                float dA = dx0 * dy0;
                float integrandRe = expI_re * dA;
                float integrandIm = expI_im * dA;

                sumRe += integrandRe;
                sumIm += integrandIm;
                i++;
            }
            j++;
        }
        return sumRe * sumRe + sumIm * sumIm;
    }

    public float accessArray(float[] arr, int i, int j, int apertureMapLengthJ) {
        return arr[i * apertureMapLengthJ + j];
    }

    public float[] flattenArray(float[][] arr2D) {
        float[] arr1D = new float[arr2D.length * arr2D[0].length];

        for (int i = 0; i < arr2D.length; i++) {
            for (int j = 0; j < arr2D[0].length; j++) {
                arr1D[i * arr2D[0].length + j] = arr2D[i][j];
            }
        }

        return arr1D;
    }

    public float[][] reconstructArray(float[] arr1D, int numRows, int numCols) {
        float[][] arr2D = new float[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                arr2D[i][j] = arr1D[i * numCols + j];
            }
        }

        return arr2D;
    }
}
