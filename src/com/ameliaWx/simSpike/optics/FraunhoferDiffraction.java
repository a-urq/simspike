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
        float dy0 = 2.0f * a.yMax / (a.apertureMap[0].length - 1);
        float dx0 = 2.0f * a.xMax / (a.apertureMap.length - 1);

        float wavelengthMm = (float) (wavelengthNm / Math.pow(10, 6));
        float k = (float) (2 * Math.PI / wavelengthMm); // wavenumber

        int i;
        int j = 0;

        ComplexNumber sum = ZERO;
        for(float y0 = -a.yMax; y0 <= a.yMax; y0+=dy0) {
            i = 0;
            for(float x0 = -a.xMax; x0 <= a.xMax; x0+=dx0) {
                float dotProd = x0 * x1 + y0 * y1;
                float kz = -k/z;
                ComplexNumber expI = ComplexNumber.exp(I.mult(kz*dotProd));

                expI = expI.mult(a.apertureMap[i][j]);

                float E = 1; // constant if a plane wave
                expI = expI.mult(E);

                ComplexNumber integrand = expI.mult(dx0 * dy0); // mult by area element

                sum = sum.add(integrand);
                i++;
            }
            j++;
        }
        return sum;
    }
}
