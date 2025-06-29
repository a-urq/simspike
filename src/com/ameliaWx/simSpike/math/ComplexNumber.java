package com.ameliaWx.simSpike.math;

public class ComplexNumber {
    public float re; // real component
    public float im; // imaginary component

    public static final ComplexNumber ZERO = new ComplexNumber(0, 0);
    public static final ComplexNumber I = new ComplexNumber(0, 1);

    public ComplexNumber(float re, float im) {
        this.re = re;
        this.im = im;
    }

    public ComplexNumber add(ComplexNumber z) {
        float newRe = re + z.re;
        float newIm = im + z.im;

        return new ComplexNumber(newRe, newIm);
    }

    public static ComplexNumber add(ComplexNumber z1, ComplexNumber z2) {
        return new ComplexNumber(z1.re + z2.re, z1.im + z2.im);
    }

    public ComplexNumber mult(float k) {
        return mult(new ComplexNumber(k, 0));
    }

    public ComplexNumber mult(ComplexNumber z) {
        float newRe = re * z.re - im * z.im;
        float newIm = re * z.im + im * z.re;

        return new ComplexNumber(newRe, newIm);
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

    public float absoluteValue() {
        return (float) Math.hypot(re, im);
    }
}
