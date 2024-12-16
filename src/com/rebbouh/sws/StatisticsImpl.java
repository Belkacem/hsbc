package com.rebbouh.sws;

import java.util.Objects;

public record StatisticsImpl(double mean, int mode, int[] pctiles) implements Statistics {

  /**
   * Returns the mean.
   */
  @Override
  public double getMean() {
    return this.mean;
  }

  /**
   * Gets the mode.
   */
  @Override
  public int getMode() {
    return this.mode;
  }

  /**
   * For percentiles I choose to include all the percentiles from 1 to 100 on an array, computations are made beforehand.
   * @param pctile
   * @return
   */
  @Override
  public int getPctile(int pctile) {
    if (pctile <1 || pctile > 100) {
      throw new IllegalArgumentException("Percentile should be between 1 and 100.");
    }
    return this.pctiles[pctile];
  }
}