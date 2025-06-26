package com.ameliaWx.simSpike.math;

public class ComplexNumber {
    public float re; // real component
    public float im; // imaginary component

    public ComplexNumber(float re, float im) {
        this.re = re;
        this.im = im;
    }

    public void add(ComplexNumber z) {
        re += z.re;
        im += z.im;
    }

    public static ComplexNumber add(ComplexNumber z1, ComplexNumber z2) {
        return new ComplexNumber(z1.re + z2.re, z1.im + z2.im);
    }

    public void mult(ComplexNumber z) {
        float newRe = re * z.re - im * z.im;
        float newIm = re * z.im + im * z.re;

        re = newRe;
        im = newIm;
    }

    public static ComplexNumber mult(ComplexNumber z1, ComplexNumber z2) {
        float re = z1.re * z2.re - z1.im * z2.im;
        float im = z1.re * z2.im + z1.im * z2.re;

        return new ComplexNumber(re, im);
    }

    // theta in radians
    public static ComplexNumber exp(ComplexNumber z) {
        float expRe = (float) Math.exp(z.re);

        float re = expRe * (float) Math.cos(z.im);
        float im = expRe * (float) Math.sin(z.im);

        return new ComplexNumber(re, im);
    }
    // euler's formula, theta in radians
    public static ComplexNumber exp_i(float theta) {
        return ComplexNumber.exp(new ComplexNumber(0, theta));
    }
}
