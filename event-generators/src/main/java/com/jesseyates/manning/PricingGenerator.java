package com.jesseyates.manning;

import com.beust.jcommander.Parameters;

import java.util.HashMap;
import java.util.Map;

@Parameters(commandDescription = "Run the pricing event generator")
public class PricingGenerator extends BaseGenerator {
  @Override
  protected void nextEvent() throws Exception {
    Map<String, Object> event = new HashMap<>();
    int region = regions.get(r.nextInt(regions.size() - 1));
    float price = floatInRange(0.001f, .25f);
    event.put("region", region);
    event.put("kw_dollars", price);
    post(event);
  }
}
