package rebbouh.rand;

import com.rebbouh.rand.NumAndProbability;
import com.rebbouh.rand.ProbabilisticRandomGen;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProbabilisticRandomGenTest {

  private ProbabilisticRandomGen generator;

  @Test
  void testSingleElementProbability() {
    var list = new ArrayList<NumAndProbability>() {{
      add(new NumAndProbability(1, .1f));
    }};
    generator = new ProbabilisticRandomGen(list);

    for (var i = 0; i < 1000; i++) {
      var result = generator.nextFromSample();
      assertEquals(1, result, "The result should always be 5");
    }
  }

  @Test
  void testValidMultipleProbabilities() {
    var totals = new HashMap<Integer, Integer>();
    var list = new ArrayList<NumAndProbability>() {{
      add(new NumAndProbability(1, .4f));
      add(new NumAndProbability(2, .6f));
      add(new NumAndProbability(3, .7f));
      add(new NumAndProbability(4, .1f));
      add(new NumAndProbability(5, 7f));
      add(new NumAndProbability(6, .9f));
      add(new NumAndProbability(7, .3f));
      add(new NumAndProbability(8, 10f));
    }};
    // The total of the summed probabilities.
    var CUMULATIVE_PROBABILITY = 20d;

    generator = new ProbabilisticRandomGen(list);

    // Can be smaller with bigger samples.
    var THRESHOLD_LEVEL = .05d;
    var NB_ITERATIONS = 1_000_000;
    ProbabilisticRandomGen test = new ProbabilisticRandomGen(list);
    for (var i = 0; i < NB_ITERATIONS; i++) {
      var next = test.nextFromSample();
      totals.put(next, totals.getOrDefault(next, 0) + 1);
    }
    list.forEach(numAndProbability -> {
      var number = numAndProbability.getNumber();
      var frequency = (CUMULATIVE_PROBABILITY * totals.get(number))/ NB_ITERATIONS;
      var probabilityOfSample = numAndProbability.getProbabilityOfSample();
      assertTrue(
          Math.abs(frequency - probabilityOfSample) < THRESHOLD_LEVEL,
          "Number %d should appear with same probability as input, observed %s probability %s".formatted(number, frequency, probabilityOfSample)
      );
    });
  }

  @Test
  void testEmptyList() {
    List<NumAndProbability> list = List.of();

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      generator = new ProbabilisticRandomGen(list);
      generator.nextFromSample();
    });
    assertEquals("numsAndProbabilities can be empty !", exception.getMessage());
  }

  @Test
  void testToString() {
    List<NumAndProbability> list = List.of(new NumAndProbability(1, 0.5f), new NumAndProbability(2, 0.5f));
    generator = new ProbabilisticRandomGen(list);

    String expected = "ProbabilisticRandomGen{numsAndProbabilities=[NumAndProbability{number=1, probabilityOfSample=0.5}, NumAndProbability{number=2, probabilityOfSample=0.5}], cumulativeProbability=[0.5, 1.0]}";
    assertEquals(expected, generator.toString());
  }
}
