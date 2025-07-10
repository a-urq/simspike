package com.ameliaWx.simSpike.optics;

import com.ameliaWx.simSpike.math.ComplexNumber;
import static com.ameliaWx.simSpike.math.ComplexNumber.I;
import static com.ameliaWx.simSpike.math.ComplexNumber.ZERO;

public class FraunhoferDiffraction {
    public static float irradianceAtDistance(Aperture a, float wavelengthNm, float x1, float y1, float z) {
        ComplexNumber field = fieldAtDistance(a, wavelengthNm, x1, y1, z);
        float fieldAmplitude = field.absoluteValue();
        return (float) Math.pow(fieldAmplitude, 2);
    }

    public static ComplexNumber fieldAtDistance(Aperture a, float wavelengthNm, float x1, float y1, float z) {
        float dy0 = 0.5f * a.yMax / a.apertureMap[0].length;
        float dx0 = 0.5f * a.xMax / a.apertureMap.length;

        float wavelengthM = (float) (wavelengthNm / Math.pow(10, 9));
        float k = (float) (2 * Math.PI / wavelengthM); // wavenumber

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

        return sum;
    }
}
