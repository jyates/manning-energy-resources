package com.jesseyates.manning;

import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public abstract class BaseGenerator {

  @Parameter(names = {"-t", "--target"}, description = "Destination to send the events",
             required = true)
  private String path;

  @Parameter(names = {"--debug"}, description = "Enable debugging of requests and responses")
  private boolean debug;

  @Parameter(names = {"-w", "--wait-interval-ms"},
             description = "Milliseconds to wait between making successive requests")
  protected long waitMillis = 500;

  protected final Random r = new Random(319009871);
  protected List<Integer> regions = new ArrayList<>();

  {
    for (int i = 0; i < 1000; i++) {
      regions.add(r.nextInt(10000));
    }
  }

  public static final MediaType JSON
    = MediaType.get("application/json; charset=utf-8");

  protected static ObjectMapper MAPPER = new ObjectMapper();

  protected OkHttpClient client = new OkHttpClient.Builder()
    .connectTimeout(5, TimeUnit.SECONDS)
    .writeTimeout(5, TimeUnit.SECONDS)
    .readTimeout(5, TimeUnit.SECONDS)
    .callTimeout(10, TimeUnit.SECONDS)
    .build();


  public void run() throws Exception {
    while (true) {
      nextEvent();
      Thread.sleep(waitMillis);
    }
  }

  protected abstract void nextEvent() throws Exception;

  protected String post(Object body) throws Exception {
    return post(body, Optional.empty());
  }

  protected String post(Object body, Optional<String> suffix) throws Exception {
    return post(MAPPER.writeValueAsString(body), suffix);
  }

  protected String post(String json, Optional<String> suffix) throws IOException {
    RequestBody body = RequestBody.create(json, JSON);
    String url = suffix.map(s -> path + "/" + s).orElse(path);
    Request request = new Request.Builder()
      .url(url)
      .post(body)
      .build();
    if (debug) {
      System.out.println("Sending: " + request + " : " + json);
    }
    try (Response response = client.newCall(request).execute()) {
      if (debug) {
        System.out.println("Got response: " + response);
      }
      return response.body().string();
    }
  }

  protected int intInRange(int lower, int upper) {
    return Ranges.intInRange(r, lower, upper);
  }

  protected float floatInRange(Float lower, Float upper) {
    return Ranges.floatInRange(r, lower, upper);
  }
}
