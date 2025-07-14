package com.ameliaWx.simSpike.optics.gpuKernels;

import com.ameliaWx.simSpike.optics.Aperture;
import com.aparapi.Kernel;

public class PSF_GPUKernel extends Kernel {
    public float[] apertureMap1D;
    public int apertureMapLengthI;
    public int apertureMapLengthJ;
    public float a_xMax;
    public float a_yMax;
    public float x1;
    public float y1;
    public float z;

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

    private static final float[] ZERO = {0, 0};
    private static final float[] I = {0, 1};

    public void setup(Aperture a, float x1, float y1, float z) {
        // figure out how to convert between 1D and 2D
        this.a_xMax = a.xMax;
        this.a_yMax = a.yMax;
        this.x1 = x1;
        this.y1 = y1;
        this.z = z;
    }

    @Override
    public void run() {
        int gid = getGlobalId();
        int i0 = -1;
        int j0 = -1;

        float dx1 = 2.0f * a_xMax / (apertureMapLengthI - 1);
        float dy1 = 2.0f * a_yMax / (apertureMapLengthJ - 1);
        float x1_0 = -a_xMax;
        float y1_0 = -a_yMax;
        final float Z = 100.0f; // must be at least twice the pixel width of the aperture map to avoid nyquisting (at least before interpolation)
        for(int i = 0; i < apertureMapLengthI; i++) {
            for(int j = 0; j < apertureMapLengthJ; j++) {
                float x1 = x1_0 + i * dx1;
                float y1 = y1_0 + j * dy1;

                // can probably do some really good parallelization on this
                psfRed1D[gid] = irradianceAtDistance(WAVELENGTH_RED, x1, y1, Z);
                psfYellow1D[gid] = irradianceAtDistance(WAVELENGTH_YELLOW, x1, y1, Z);
                psfGreen1D[gid] = irradianceAtDistance(WAVELENGTH_GREEN, x1, y1, Z);
                psfCyan1D[gid] = irradianceAtDistance(WAVELENGTH_CYAN, x1, y1, Z);
                psfBlue1D[gid] = irradianceAtDistance(WAVELENGTH_BLUE, x1, y1, Z);
                psfViolet1D[gid] = irradianceAtDistance(WAVELENGTH_VIOLET, x1, y1, Z);
            }
        }
    }

    public float irradianceAtDistance(float wavelengthNm, float x1, float y1, float z) {
        float[] field = fieldAtDistance(wavelengthNm, x1, y1, z);
        float fieldAmplitude = complexAbs(field);
        return fieldAmplitude * fieldAmplitude;
    }

    public float[] fieldAtDistance(float wavelengthNm, float x1, float y1, float z) {
        float dy0 = 2.0f * a_yMax / (apertureMapLengthJ - 1);
        float dx0 = 2.0f * a_xMax / (apertureMapLengthI - 1);

        float wavelengthMm = wavelengthNm / 1000000;
        float k = (float) (2 * Math.PI / wavelengthMm); // wavenumber

        int i;
        int j = 0;

        // figure out how to handle the complex math
        float[] sum = ZERO;
        for(float y0 = -a_yMax; y0 <= a_yMax; y0+=dy0) {
            i = 0;
            for(float x0 = -a_xMax; x0 <= a_xMax; x0+=dx0) {
                float dotProd = x0 * x1 + y0 * y1;
                float kz = -k/z;
                float[] expI = complexExp(complexMult(I, kz*dotProd));

                float[] expI_A = complexMult(expI, accessArray(apertureMap1D, i, j, apertureMapLengthI));

                float[] integrand = complexMult(expI_A, dx0 * dy0);

                sum = complexAdd(sum, integrand);
                i++;
            }
            j++;
        }
        return sum;
    }

    public float[] complexAdd(float[] z1, float[] z2) {
        return new float[]{z1[0] + z2[0], z1[1] + z2[1]};
    }

    public float[] complexMult(float[] z1, float k) {
        float re = z1[0] * k;
        float im = z1[1] * k;

        return new float[]{re, im};
    }

    public float[] complexMult(float[] z1, float[] z2) {
        float re = z1[0] * z2[0] - z1[1] * z2[1];
        float im = z1[0] * z2[1] + z1[1] * z2[0];

        return new float[]{re, im};
    }

    public float[] complexExp(float[] z) {
        float expRe = exp(z[0]);

        float re = expRe * cos(z[1]);
        float im = expRe * sin(z[1]);

        return new float[]{re, im};
    }

    // theta in radians
    public float[] complexExpI(float theta) {
        return complexExp(new float[] {0, theta});
    }

    public float complexAbs(float[] z) {
        return sqrt(z[0] * z[0] + z[1] + z[1]);
    }

    public float accessArray(float[] arr, int i, int j, int apertureMapLengthI) {
        return arr[i * apertureMapLengthI + j];
    }
}
