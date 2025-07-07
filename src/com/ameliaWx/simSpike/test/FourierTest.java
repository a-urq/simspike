package com.ameliaWx.simSpike.test;

import com.ameliaWx.simSpike.math.ComplexNumber;
import com.ameliaWx.simSpike.math.FourierTransform1D;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class FourierTest {
    public static void main(String[] args) throws FileNotFoundException {
//        testComplexMath();
        testFourier1D();
    }

    public static void testComplexMath() {
        ComplexNumber zero = ComplexNumber.ZERO;
        ComplexNumber i = ComplexNumber.I;
        ComplexNumber iPi = ComplexNumber.I.mult((float) Math.PI);
        ComplexNumber z1 = new ComplexNumber(3, 2);
        ComplexNumber z2 = new ComplexNumber(1, 1);
        ComplexNumber z3 = new ComplexNumber((float) Math.sqrt(0.5), (float) Math.sqrt(0.5));

        System.out.println("z1 + z2: " + z1.add(z2));
        System.out.println("z1 * z2: " + z1.mult(z2));
        System.out.println("2*z1: " + z1.mult(2));
        System.out.println("z3*z3: " + z3.mult(z3));

        System.out.println("e^0: " + ComplexNumber.exp(zero));
        System.out.println("e^i: " + ComplexNumber.exp(i));
        System.out.println("e^iPi: " + ComplexNumber.exp(iPi));
        System.out.println("e^z1: " + ComplexNumber.exp(z1));
        System.out.println("e^z2: " + ComplexNumber.exp(z2));
        System.out.println("e^z3: " + ComplexNumber.exp(z3));
    }

    public static void testFourier1D() throws FileNotFoundException {
//        float[] samples = generateSamplesFromWave(100, 1, 3, 5);
        float[] samples = generateSamplesFromRect(100);

//        System.out.println(Arrays.toString(samples));

        FourierTransform1D transform = new FourierTransform1D(samples, 1, 0.0f, 10);

//        System.out.println(Arrays.toString(transform.amplitudes));
//        System.out.println(Arrays.toString(transform.phases));

        new File("fourier-test-outputs/").mkdirs();

        try (PrintWriter pw = new PrintWriter("fourier-test-outputs/inputs.csv")) {
            for(int i = 0; i < samples.length; i++) {
                pw.printf("%.6f, %.6f\n", (float) i, samples[i]);
            }
        }

        try (PrintWriter pw = new PrintWriter("fourier-test-outputs/amplitudes.csv")) {
            for (int i = 0; i < transform.amplitudes.length; i++) {
                pw.printf("%.6f, %.6f\n", (float) i * transform.dw + transform.w0, transform.amplitudes[i]);
            }
        }

        try (PrintWriter pw = new PrintWriter("fourier-test-outputs/phases.csv")) {
            for (int i = 0; i < transform.amplitudes.length; i++) {
                pw.printf("%.6f, %.6f\n", (float) i * transform.dw + transform.w0, transform.phases[i]);
            }
        }

        try (PrintWriter pw = new PrintWriter("fourier-test-outputs/amplitudes_re.csv")) {
            for (int i = 0; i < transform.amplitudes.length; i++) {
                pw.printf("%.6f, %.6f\n", (float) i * transform.dw + transform.w0, transform.complexAmplitudes[i].re);
            }
        }

        try (PrintWriter pw = new PrintWriter("fourier-test-outputs/amplitudes_im.csv")) {
            for (int i = 0; i < transform.amplitudes.length; i++) {
                pw.printf("%.6f, %.6f\n", (float) i * transform.dw + transform.w0, transform.complexAmplitudes[i].im);
            }
        }
    }

    private static float[] generateSamplesFromWave(int length, int... freqs) {
        float[] samples = new float[length];

        for(int i = 0; i < samples.length; i++) {
            float phaseAngle = (float) (2.0f * Math.PI / length * i);

            float sample = 0;
            for(int f = 0; f < freqs.length; f++) {
                sample += (float) (1.0f / freqs[f] * Math.sin(freqs[f] * phaseAngle));
            }

            samples[i] = sample;
        }

        return samples;
    }

    private static float[] generateSamplesFromRect(int length) {
        float[] samples = new float[length];

        for(int i = 0; i < samples.length; i++) {
            if(i >= length * 3 / 8 && i <= length * 5 / 8) {
                samples[i] = 1;
            } else {
                samples[i] = 0;
            }
        }

        return samples;
    }
}
