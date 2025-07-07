package com.ameliaWx.simSpike.optics;

import com.ameliaWx.simSpike.math.ComplexNumber;
import static com.ameliaWx.simSpike.math.ComplexNumber.I;
import static com.ameliaWx.simSpike.math.ComplexNumber.ZERO;

public class FraunhoferDiffraction {
    public float fieldAtDistance(Aperture a, float wavelengthNm, int x1, int y1, int z) {
        float dy0 = a.yMax / a.apertureMap[0].length;
        float dx0 = a.xMax / a.apertureMap.length;

        float wavelengthM = (float) (wavelengthNm / Math.pow(10, 9));
        float k = (float) (1 / wavelengthM); // wavenumber

        int i = 0;
        int j = 0;

        ComplexNumber sum = ZERO;
        for(float y0 = -a.yMax; y0 <= a.yMax; y0+=dy0) {
            for(float x0 = -a.xMax; x0 <= a.xMax; x0+=dx0) {
                float dotProd = x0 * x1 + y0 * y1;
                float kz = -k/z;
                ComplexNumber expI = ComplexNumber.exp(I.mult(kz*dotProd));

                expI = expI.mult(a.apertureMap[i][j]);

                float E = 1; // constant if a plane wave
                expI = expI.mult(E);

                ComplexNumber integrand = expI.mult(dx0 * dy0); // mult by area element

                sum.add(integrand);
                j++;
            }
            i++;
        }

        return sum.re;
    }
}
