package com.ameliaWx.simSpike.math;

import static com.ameliaWx.simSpike.math.ComplexNumber.I;

public class FourierTransform1D {
    private ComplexNumber[] amplitudes;
    private float dE; // frequency differential (E looks kinda like a greek xi I guess)

    public FourierTransform1D(float[] samples, float dx) {
        new FourierTransform1D(samples, dx, 1);
    }

    public FourierTransform1D(float[] samples, float dx, float deltaX) {
        this.amplitudes = new ComplexNumber[samples.length];
        this.dE = deltaX / samples.length;

        for(int i = 0; i < amplitudes.length; i++) {
            float xi = i * dE;
            amplitudes[i] = testAtFrequency(samples, dx, xi);
        }
    }

    public ComplexNumber testAtFrequency(float[] samples, float dx, float xi) {
        ComplexNumber amplitude = ComplexNumber.ZERO;

        for(int i = 0; i < samples.length; i++) {
            float x = i * dx;

            float f_x = samples[i];
            ComplexNumber phaseAngle = I.mult((float) (-2 * Math.PI * xi * x));

            ComplexNumber e_phi = ComplexNumber.exp(phaseAngle);
            ComplexNumber dA = e_phi.mult(f_x * dx);

            amplitude = amplitude.add(dA);
        }

        return amplitude;
    }
}
