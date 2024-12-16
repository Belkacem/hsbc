package com.rebbouh.rand;

import java.util.Arrays;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * NOTE: in this class I ommited the normalization step for two reasons :
 *   1) Performance: computing new probabilities needs more computation.
 *   2) Rounding issues: for small probabilities normalizing may round the probability to 0 which is not intended.
 */
public class ProbabilisticRandomGen implements IProbabilisticRandomGen {
    private static final RandomGenerator random = RandomGenerator.getDefault();
    private final List<NumAndProbability> numsAndProbabilities;
    private final double[] cumulativeProbability;


    public ProbabilisticRandomGen(List<NumAndProbability> numsAndProbabilities) {
        if (numsAndProbabilities == null ) {
            throw new NullPointerException("numsAndProbabilities can be null !");
        }
        if (numsAndProbabilities.size() == 0) {
            throw new IllegalArgumentException("numsAndProbabilities can be empty !");
        }
        this.numsAndProbabilities = numsAndProbabilities;
        this.cumulativeProbability = numsAndProbabilities.stream().mapToDouble(NumAndProbability::getProbabilityOfSample).toArray();
        for (var i = 1; i < cumulativeProbability.length; i++) {
            cumulativeProbability[i] += cumulativeProbability[i - 1];
        }
    }


    @Override
    public int nextFromSample() {
        var value = random.nextDouble(this.cumulativeProbability[this.cumulativeProbability.length - 1]);
        for (var i = 0; i < this.cumulativeProbability.length; i++) {
            if (this.cumulativeProbability[i] > value) {
                return this.numsAndProbabilities.get(i).getNumber();
            }
        }
        throw new IllegalStateException("The probabilities are not valid, please check the data");
    }

    @Override
    public String toString() {
        return "ProbabilisticRandomGen{" +
                "numsAndProbabilities=" + numsAndProbabilities +
                ", cumulativeProbability=" + Arrays.toString(cumulativeProbability) +
                '}';
    }
}