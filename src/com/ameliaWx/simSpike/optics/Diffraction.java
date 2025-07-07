package com.ameliaWx.simSpike.optics;

import com.ameliaWx.simSpike.math.ComplexNumber;
import static com.ameliaWx.simSpike.math.ComplexNumber.I;
import static com.ameliaWx.simSpike.math.ComplexNumber.ZERO;

public class Diffraction {
    public float fieldAtDistance(Aperture a, float wavelengthNm, int x1, int y1, int z) {
        float dy0 = a.yMax / a.apertureMap[0].length;
        float dx0 = a.xMax / a.apertureMap.length;

        int i = 0;
        int j = 0;

        ComplexNumber sum = ZERO;
        for(float y0 = -a.yMax; y0 <= a.yMax; y0+=dy0) {
            for(float x0 = -a.xMax; x0 <= a.xMax; x0+=dx0) {
                ComplexNumber h = h(x1, x0, y1, y0, z, wavelengthNm);

                ComplexNumber integrand = h.mult(a.apertureMap[i][j] * dx0 * dy0);
                j++;
            }
            i++;
        }

        return sum.re;
    }

    public ComplexNumber h(float x1, float x0, float y1, float y0, float z, float wavelengthNm) {
        float wavelengthM = (float) (wavelengthNm / Math.pow(10, 9));
        float k = (float) (1 / wavelengthM); // wavenumber
        float r01 = r01(x1, x0, y1, y0, z);
        ComplexNumber euler = ComplexNumber.exp(I.mult(k * r01)).mult(1/r01);
        ComplexNumber coef = I.mult(wavelengthM).reciprocal();

        return euler.mult(coef);
    }

    public float r01(float x1, float x0, float y1, float y0, float z) {
        return (float) Math.sqrt(Math.pow(z, 2) + Math.pow(x0 - x1, 2) + Math.pow(y0 - y1, 2));
    }
}