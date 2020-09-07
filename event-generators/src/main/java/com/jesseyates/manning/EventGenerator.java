package com.jesseyates.manning;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;


@Parameters(commandDescription = "Run the device event generator")
public class EventGenerator extends BaseGenerator {

  @Parameter(names = {"-n", "--num-devices"}, description = "Number of unique devices")
  protected int deviceCount = 1_000;

  @Parameter(names = {"-e", "--max-events-per-request"},
             description = "Maximum number of events per request")
  protected int maxEvents = 100;

  @Parameter(names = {"-b", "--bad-events"}, description = "Corrupt the events")
  private boolean badEvents;
  @Parameter(names = {"-m", "--missing-region"},
             description = "Events are missing the region identifier")
  private boolean missingRegion;

  private static UUID randomUUID(Random r) {
    // copy the UUID logic, but use our own random
    byte[] data = new byte[16];
    r.nextBytes(data);
    data[6] &= 0x0f;  /* clear version        */
    data[6] |= 0x40;  /* set to version 4     */
    data[8] &= 0x3f;  /* clear variant        */
    data[8] |= 0x80;  /* set to IETF variant  */
    long msb = 0;
    long lsb = 0;
    for (int i = 0; i < 8; i++)
      msb = (msb << 8) | (data[i] & 0xff);
    for (int i = 8; i < 16; i++)
      lsb = (lsb << 8) | (data[i] & 0xff);
    return new UUID(msb, lsb);
  }

  private final Map<UUID, Integer> deviceToRegion = new HashMap<>();

  // setup devices and regions
  {
    for (int i = 0; i < deviceCount; i++) {
      int region = regions.get(r.nextInt(regions.size()));
      deviceToRegion.put(randomUUID(r), region);
    }
  }

  private final Map<String, Pair<Object, Object>> events = new HashMap<>();

  // setup event field values
  {
    events.put("charging", new ImmutablePair<>(-1000, 1000));
    events.put("charging_source", new ImmutablePair<>("solar", "utility"));
    events.put("current_capacity", new ImmutablePair<>(0, 13_000));
    // other fields like a real device would send
    events.put("moduleL_temp", new ImmutablePair<>(-5, 225));
    events.put("moduleR_temp", new ImmutablePair<>(-5, 225));
    events.put("processor1_temp", new ImmutablePair<>(-5, 225));
    events.put("processor2_temp", new ImmutablePair<>(-5, 225));
    events.put("processor3_temp", new ImmutablePair<>(-5, 225));
    events.put("processor4_temp", new ImmutablePair<>(-5, 225));
    events.put("inverter_state", new ImmutablePair<>(0, 15));
    events.put("SoC_regulator", new ImmutablePair<>(26.0f, 29.6f));
  }

  @Override
  public void nextEvent() throws Exception {
    UUID[] ids = deviceToRegion.keySet().toArray(new UUID[0]);
    UUID id = ids[r.nextInt(ids.length - 1)];

    int count = Math.max(1, r.nextInt(maxEvents));
    List<Object> events = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      Object event = getEvent(id);
      if (badEvents && r.nextBoolean()) {
        event = "garbage{\"/}" + MAPPER.writeValueAsString(event);
      }
      events.add(event);
    }

    // then we convert them to be one event per line
    String message = events.stream().map(event -> {
      try {
        return MAPPER.writeValueAsString(event);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }).collect(Collectors.joining("\n"));

    post(message, Optional.of(id.toString()));
  }

  private Map<String, Object> getEvent(UUID id) {
    Map<String, Object> event = new HashMap<>();
    int region = deviceToRegion.get(id);
    event.put("device_id", id.toString());
    if (missingRegion && r.nextBoolean()) {
      event.put("region", region);
    }
    for (Map.Entry<String, Pair<Object, Object>> entry : events.entrySet()) {
      Pair<Object, Object> range = entry.getValue();
      Object value = null;
      if (range.getKey() instanceof Integer) {
        value = intInRange((Integer) range.getKey(), (Integer) range.getValue());
      } else if (range.getKey() instanceof Float) {
        value = floatInRange((Float)range.getKey(), (Float) range.getValue());
      } else if (range.getKey() instanceof String) {
        value = r.nextBoolean() ? range.getKey() : range.getValue();
      }
      event.put(entry.getKey(), value);
    }

    return event;
  }
}
