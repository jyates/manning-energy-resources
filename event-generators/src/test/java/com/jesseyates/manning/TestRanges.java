package com.jesseyates.manning;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class TestRanges {

  @Test
  public void testFloat() throws Exception {
    Random r = new Random();
    for (int i = 0; i < 10000; i++) {
      float f = Ranges.floatInRange(r, 1.0f, 5.0f);
      assertNotNull(f);
      assertTrue("Bad range for " + f, f >= 1.0);
      assertTrue("Bad range for " + f, f <= 5.0);
    }
  }

  @Test
  public void testFloatWithNegativeStart() throws Exception {
    Random r = new Random();
    for (int i = 0; i < 10000; i++) {
      float f = Ranges.floatInRange(r, -1.0f, 5.0f);
      assertNotNull(f);
      assertTrue("Bad range for " + f, f >= -1.0);
      assertTrue("Bad range for " + f, f <= 5.0);
    }
  }

  @Test
  public void testFloatWithNegatives() throws Exception {
    Random r = new Random();
    for (int i = 0; i < 10000; i++) {
      float f = Ranges.floatInRange(r, -10.0f, -5.0f);
      assertNotNull(f);
      assertTrue("Bad range for " + f, f >= -10.0);
      assertTrue("Bad range for " + f, f <= -5.0);
    }
  }
}
