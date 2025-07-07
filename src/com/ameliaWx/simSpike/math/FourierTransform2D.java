package com.ameliaWx.simSpike.math;

import static com.ameliaWx.simSpike.math.ComplexNumber.I;

public class FourierTransform2D {
    public ComplexNumber[][] complexAmplitudes;
    public float[][] amplitudes;
    public float[][] phases; // units: radians
    public float dE; // frequency differential (E looks kinda like a greek xi I guess)

    public FourierTransform2D(float[][] samples, float dx) {
        this.complexAmplitudes = new ComplexNumber[samples.length][samples[0].length];
        this.amplitudes = new float[samples.length][samples[0].length];
        this.phases = new float[samples.length][samples[0].length];
        this.dE = dx / samples.length;

        for(int i = 0; i < complexAmplitudes.length; i++) {
            float xi = i * dE;
//            complexAmplitudes[i] = complexAmplitudeAtFrequency(samples, dx, xi);
//            amplitudes[i] = complexAmplitudes[i].absoluteValue();
//            phases[i] = (float) Math.atan2(complexAmplitudes[i].im, complexAmplitudes[i].re);
        }
    }

    public ComplexNumber complexAmplitudeAtFrequency(float[] samples, float dx, float xi) {
        ComplexNumber amplitude = ComplexNumber.ZERO;

        for(int i = 0; i < samples.length; i++) {
            float x = i * dx;

            float f_x = samples[i];
            ComplexNumber phaseAngle = I.mult((float) (-2 * Math.PI * xi * x));

            ComplexNumber e_phi = ComplexNumber.exp(phaseAngle);
            ComplexNumber dA = e_phi.mult(f_x * dx);

//            System.out.printf("f(x): %.2f xi: %.2f phi: %.2fi, e^phi: ", f_x, xi, phaseAngle.im);
            System.out.println(e_phi);

            amplitude = amplitude.add(dA);
        }

        amplitude = amplitude.mult(1.0f / samples.length);

        return amplitude;
    }
}
