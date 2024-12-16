package com.rebbouh.rand;

public class NumAndProbability {
    private final int number;

    private final float probabilityOfSample;

    public NumAndProbability(int number, float probabilityOfSample) {
        this.number = number;
        if (probabilityOfSample < 0) {
            throw new IllegalArgumentException("Probabilities should be positive floats");
        }
        this.probabilityOfSample = probabilityOfSample;
    }

    public int getNumber() {
        return number;
    }

    public float getProbabilityOfSample() {
        return probabilityOfSample;
    }

    @Override
    public String toString() {
        return "NumAndProbability{" +
                "number=" + number +
                ", probabilityOfSample=" + probabilityOfSample +
                '}';
    }
}