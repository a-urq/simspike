package com.ameliaWx.simSpike.math;

import static com.ameliaWx.simSpike.math.ComplexNumber.I;

public class FourierTransform1D {
    public ComplexNumber[] complexAmplitudes;
    public float[] amplitudes;
    public float[] phases; // units: radians
    public float dw; // frequency differential (w looks like a greek omega)
    public float w0; // lowest w value

    public FourierTransform1D(float[] samples, float dx) {
        this(samples, dx, 0, 1);
    }
    public FourierTransform1D(float[] samples, float dx, float x0) {
        this(samples, dx, x0, 1);
    }

    public FourierTransform1D(float[] samples, float dx, int supersampling) {
        this(samples, dx, 0, supersampling);
    }

    public FourierTransform1D(float[] samples, float dx, float x0, int supersampling) {
        this.complexAmplitudes = new ComplexNumber[samples.length * supersampling + 1];
        this.amplitudes = new float[samples.length * supersampling + 1];
        this.phases = new float[samples.length * supersampling + 1];
        this.dw = dx / (samples.length * supersampling);
        this.w0 = -0.5f * dw * (amplitudes.length - 1);

        for(int i = 0; i < complexAmplitudes.length; i++) {
            float w = i * dw + w0;
            complexAmplitudes[i] = complexAmplitudeAtFrequency(samples, dx, w);
            amplitudes[i] = complexAmplitudes[i].absoluteValue();
            phases[i] = (float) Math.atan2(complexAmplitudes[i].im, complexAmplitudes[i].re);
        }
    }

    public ComplexNumber complexAmplitudeAtFrequency(float[] samples, float dx, float w) {
        ComplexNumber amplitude = ComplexNumber.ZERO;

        for(int i = 0; i < samples.length; i++) {
            float x = i * dx;

            float f_x = samples[i];
            ComplexNumber phaseAngle = I.mult((float) (-2 * Math.PI * w * x));

            ComplexNumber e_phi = ComplexNumber.exp(phaseAngle);
            ComplexNumber dA = e_phi.mult(f_x * dx);

//            System.out.printf("f(x): %.2f xi: %.2f phi: %.2fi, e^phi: ", f_x, xi, phaseAngle.im);
//            System.out.println("e_phi: " + e_phi);

            amplitude = amplitude.add(dA);
        }

//        amplitude = amplitude.mult(1.0f / samples.length);

        return amplitude;
    }
}
