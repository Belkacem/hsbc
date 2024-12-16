package com.rebbouh.rand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProbabilisticRandomGenTests {
    private static final double THRESHOLD_LEVEL = .05d;
    private static final int NB_ITERATIONS = 1_000_000;

    private static final double CUMULATIVE_PROBABILITY = 20;


    private static final HashMap<Integer, Integer> total = new HashMap<>();
    public static List<NumAndProbability> NUMS_AND_PROBABILITIES = new ArrayList<>() {{
        add(new NumAndProbability(1, .4f));
        add(new NumAndProbability(2, .6f));
        add(new NumAndProbability(3, .7f));
        add(new NumAndProbability(4, .1f));
        add(new NumAndProbability(5, 7f));
        add(new NumAndProbability(6, .9f));
        add(new NumAndProbability(7, .3f));
        add(new NumAndProbability(8, 10f));
    }};

    public static void test() {
        ProbabilisticRandomGen test = new ProbabilisticRandomGen(NUMS_AND_PROBABILITIES);
        for (int i = 0; i < NB_ITERATIONS; i++) {
            var next = test.nextFromSample();
            total.put(next, total.getOrDefault(next, 0) + 1);
        }
        System.out.println("distribution " + total);
        NUMS_AND_PROBABILITIES.forEach(numAndProbability -> {
            var frequency = total.get(numAndProbability.getNumber()) / (double) NB_ITERATIONS * CUMULATIVE_PROBABILITY;
            assert Math.abs(frequency - numAndProbability.getProbabilityOfSample()) < THRESHOLD_LEVEL;
        });
    }
}