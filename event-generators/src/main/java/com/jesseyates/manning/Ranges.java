package com.jesseyates.manning;

import java.util.Random;

public class Ranges {

  static int intInRange(Random r, int lower, int upper) {
    int width = Math.abs(lower - upper);
    return r.nextInt(width) + lower;
  }

  static float floatInRange(Random r, Float lower, Float upper) {
    float width = Math.abs(lower - upper);
    float percent = r.nextFloat();
    return (percent * width) + lower;
  }
}
