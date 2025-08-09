package com.ameliaWx.simSpike.optics;

import com.ameliaWx.simSpike.math.ComplexNumber;
import com.ameliaWx.simSpike.optics.gpuKernels.PSF_GPUKernel;
import com.aparapi.Range;

import static com.ameliaWx.simSpike.math.ComplexNumber.I;
import static com.ameliaWx.simSpike.math.ComplexNumber.ZERO;

public class PointSpreadFunction {
    // have red, yellow, green, cyan, blue, and violet kernels
    // should all be the same pattern, but a different scale
    // and acting on different channels of one RGB image

    // consider custom wavelength setting
    // consider whether it would be best to do RGB customization or a full
    // RYGCBV thing, possibly needing a customizable conversion from RYGCBV to RGB

    // consider some sort of interpolation (bilinear? lanczos?) to fight nyquisting of high frequencies

    // figure out scaling and testing and shit lol

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

    public PointSpreadFunction(Aperture a) {
        this(a, false);
    }

    public PointSpreadFunction(Aperture a, boolean useGPU) {
        if(useGPU) {
            initializeWithGPU(a);
        } else {
            initializeWithCPU(a);
        }
    }

    public void initializeWithCPU(Aperture a) {
        initializeWithCPU(a, Method.NAIVE);
    }

    private void initializeWithCPU(Aperture a, Method m) {
        switch (m) {
            case NAIVE:
                initializeNaiveCPU(a);
                break;
            case ROW_COLUMN:
                initializeRowColumnCPU(a);
                break;
        }
    }

    private void initializeNaiveCPU(Aperture a) {
        psfRed = new float[a.apertureMap.length][a.apertureMap[0].length];
        psfYellow = new float[a.apertureMap.length][a.apertureMap[0].length];
        psfGreen = new float[a.apertureMap.length][a.apertureMap[0].length];
        psfCyan = new float[a.apertureMap.length][a.apertureMap[0].length];
        psfBlue = new float[a.apertureMap.length][a.apertureMap[0].length];
        psfViolet = new float[a.apertureMap.length][a.apertureMap[0].length];

        float dx1 = 2.0f * a.xMax / (a.apertureMap.length - 1);
        float dy1 = 2.0f * a.yMax / (a.apertureMap[0].length - 1);
        float x1_0 = -a.xMax;
        float y1_0 = -a.yMax;
        final float Z = 50.0f; // must be at least the pixel width of the aperture map to avoid nyquisting (at least before interpolation being implemented)
        for(int i = 0; i < psfRed.length; i++) {
            for(int j = 0; j < psfRed[0].length; j++) {
                System.out.println(i + "\t" + j);
                float x1 = x1_0 + i * dx1;
                float y1 = y1_0 + j * dy1;

                // can probably do some really good parallelization on this
                psfRed[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, WAVELENGTH_RED, x1, y1, Z);
                psfYellow[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, WAVELENGTH_YELLOW, x1, y1, Z);
                psfGreen[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, WAVELENGTH_GREEN, x1, y1, Z);
                psfCyan[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, WAVELENGTH_CYAN, x1, y1, Z);
                psfBlue[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, WAVELENGTH_BLUE, x1, y1, Z);
                psfViolet[i][j] = FraunhoferDiffraction.irradianceAtDistance(a, WAVELENGTH_VIOLET, x1, y1, Z);
            }
        }

//        normalizePSF(psfRed);
//        normalizePSF(psfYellow);
//        normalizePSF(psfGreen);
//        normalizePSF(psfCyan);
//        normalizePSF(psfBlue);
//        normalizePSF(psfViolet);

        normalizePSF(psfRed, psfYellow, psfGreen, psfCyan, psfBlue, psfViolet);
    }

    private void initializeRowColumnCPU(Aperture a) {
        final float Z = 100.0f; // must be at least the pixel width of the aperture map to avoid nyquisting (at least before interpolation being implemented)

        psfRed = transformForFrequencyRC(a, WAVELENGTH_RED, Z);
        psfYellow = transformForFrequencyRC(a, WAVELENGTH_YELLOW, Z);
        psfGreen = transformForFrequencyRC(a, WAVELENGTH_GREEN, Z);
        psfCyan = transformForFrequencyRC(a, WAVELENGTH_CYAN, Z);
        psfBlue = transformForFrequencyRC(a, WAVELENGTH_BLUE, Z);
        psfViolet = transformForFrequencyRC(a, WAVELENGTH_VIOLET, Z);
    }

    private float[][] transformForFrequencyRC(Aperture a, float wavelength, float z) {
        // here w represents x-frequency, f represents y-frequency. don't question it i just gotta have something
        ComplexNumber[][] aperture_xf = transformRows(a.apertureMap, a.yMax, wavelength, z);
        ComplexNumber[][] aperture_fx = transpose(aperture_xf);
        ComplexNumber[][] aperture_fw = transformRows(aperture_fx, a.xMax, wavelength, z);
        ComplexNumber[][] aperture_wf = transpose(aperture_fw);

        ComplexNumber[][] rawFourierTransform = aperture_wf;
        //something something coefficients or some shit

        float[][] psf = new float[rawFourierTransform.length][rawFourierTransform[0].length];
        return psf;
    }

    private ComplexNumber[][] transformRows(float[][] a, float axisMax, float wavelengthNm, float z) {
        ComplexNumber[][] c = new ComplexNumber[a.length][a[0].length];
        for(int i = 0; i < c.length; i++) {
            for(int j = 0; j < c[i].length; j++) {
                c[i][j] = new ComplexNumber(a[i][j], 0);
            }
        }

        return transformRows(c, axisMax, wavelengthNm, z);
    }

    private ComplexNumber[][] transformRows(ComplexNumber[][] a, float axisMax, float wavelengthNm, float z) {
        ComplexNumber[][] ret = new ComplexNumber[a.length][a[0].length];

        float dx1 = 2.0f * axisMax / (a.length - 1);
        float x1_0 = -axisMax;

        float dx0 = 2.0f * axisMax / (a.length - 1);

        float wavelengthMm = (float) (wavelengthNm / Math.pow(10, 6));
        float k = (float) (2 * Math.PI / wavelengthMm); // wavenumber

        for(int i = 0; i < psfRed.length; i++) {
            for (int j = 0; j < psfRed[0].length; j++) {
                float x1 = x1_0 + j * dx1;

                ComplexNumber sum = ZERO;
                int j0 = 0;
                for (float x0 = -axisMax; x0 <= axisMax; x0 += dx0) {
                    float dotProd = x0 * x1;
                    float kz = -k / z;

                    ComplexNumber expI = ComplexNumber.exp(I.mult(kz*dotProd));

                    expI = expI.mult(a[i][j0]);

                    float E = 1; // constant if a plane wave
                    expI = expI.mult(E);

                    ComplexNumber integrand = expI.mult(dx0); // mult by area element

                    sum = sum.add(integrand);
                    j0++;
                }
            }
        }

        return ret;
    }

    private ComplexNumber[][] transpose(ComplexNumber[][] a) {
        ComplexNumber[][] b = new ComplexNumber[a[0].length][a.length];

        for(int i = 0; i < a.length; i++) {
            for(int j = 0; j < a[0].length; j++) {
                b[j][i] = a[i][j];
            }
        }

        return b;
    }

    public void initializeWithGPU(Aperture a) {
        PSF_GPUKernel kernel = new PSF_GPUKernel();

        Range range = Range.create(a.apertureMap.length * a.apertureMap[0].length);

        long gpuStart = System.currentTimeMillis();

//        kernel.execute(Range.create(1));

        long gpuEnd = System.currentTimeMillis();

        System.out.println("gpu kernel init: " + (gpuEnd - gpuStart) + " ms");

        gpuStart = System.currentTimeMillis();

        kernel.setup(a, 400);
        kernel.execute(range);
        kernel.finish();
        kernel.dispose();

        psfRed = kernel.psfRed;
        psfYellow = kernel.psfYellow;
        psfGreen = kernel.psfGreen;
        psfCyan = kernel.psfCyan;
        psfBlue = kernel.psfBlue;
        psfViolet = kernel.psfViolet;

        gpuEnd = System.currentTimeMillis();

        System.out.println("gpu: " + (gpuEnd - gpuStart) + " ms");
        kernel.getTargetDevice();
        System.out.println("Execution mode: " + kernel.getExecutionMode());

        long normStart = System.currentTimeMillis();
        normalizePSF(psfRed, psfYellow, psfGreen, psfCyan, psfBlue, psfViolet);
        long normEnd = System.currentTimeMillis();
        System.out.println("normalization: " + (normEnd - normStart) + " ms");
    }

    private void normalizePSF(float[][]... psfs) {
        float maxValue = -Float.MAX_VALUE;

        for(float[][] psf : psfs) {
            maxValue = Float.max(maxValue, max(psf));
        }

        for(float[][] kernel : psfs) {
            for (int i = 0; i < kernel.length; i++) {
                for (int j = 0; j < kernel[i].length; j++) {
                    kernel[i][j] /= maxValue;
                }
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

    private enum Method {
        NAIVE, ROW_COLUMN;
    }
}
