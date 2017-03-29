package com.ramusthastudio.reversegame.model;

public class Postback {
  private String data;

  public String data() { return data; }

  @Override public String toString() {
    return "Postback{" +
        "data='" + data + '\'' +
        '}';
  }
}
