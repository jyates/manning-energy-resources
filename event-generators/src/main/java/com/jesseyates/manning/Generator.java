package com.jesseyates.manning;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class Generator {

  public static final String EVENTS = "events";
  public static final String PRICING = "pricing";
  @Parameter(names = "--help", help = true)
  private boolean help;

  public static void main(String[] args) throws Exception {
    Generator main = new Generator();
    EventGenerator deviceEvents = new EventGenerator();
    PricingGenerator pricingEvents = new PricingGenerator();
    JCommander jc = JCommander.newBuilder()
                              .addObject(main)
                              .addCommand(EVENTS, deviceEvents)
                              .addCommand(PRICING, pricingEvents)
                              .build();
    try {
      jc.parse(args);

      if (main.help) {
        jc.usage();
        System.exit(0);
      }
      String command = jc.getParsedCommand();
      if (command == null) {
        command = "";
      }
      if (command.equals(EVENTS)) {
        deviceEvents.run();
      } else if (command.equals(PRICING)) {
        pricingEvents.run();
      } else {
        System.out.println("Unrecognized command: " + command);
        jc.usage();
        System.exit(2);
      }
    } catch (ParameterException e) {
      System.out.println(e.getMessage());
      jc.usage();
      System.exit(1);
    }
  }
}
