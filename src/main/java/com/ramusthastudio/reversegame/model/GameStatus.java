package com.ramusthastudio.reversegame.model;

public class GameStatus {
  private String id;
  private String status;
  private int wordTrue;
  private int wordFalse;
  private long lastTime;
  private boolean isAnswer;

  public GameStatus(String aId, String aStatus) {
    id = aId;
    status = aStatus;
  }

  public GameStatus(String aId, String aStatus, long aLastTime) {
    id = aId;
    status = aStatus;
    lastTime = aLastTime;
  }

  public GameStatus(String aId, String aStatus, int aWordTrue, int aWordFalse, long aLastTime, boolean aIsAnswer) {
    id = aId;
    status = aStatus;
    wordTrue = aWordTrue;
    wordFalse = aWordFalse;
    lastTime = aLastTime;
    isAnswer = aIsAnswer;
  }

  public String getId() { return id; }
  public String getStatus() { return status; }
  public int getWordTrue() { return wordTrue; }
  public int getWordFalse() { return wordFalse; }
  public long getLastTime() { return lastTime; }
  public boolean isAnswer() { return isAnswer; }

  public GameStatus setId(String aId) {
    id = aId;
    return this;
  }
  public GameStatus setStatus(String aStatus) {
    status = aStatus;
    return this;
  }
  public GameStatus setWordTrue(int aWordTrue) {
    wordTrue = aWordTrue;
    return this;
  }
  public GameStatus setWordFalse(int aWordFalse) {
    wordFalse = aWordFalse;
    return this;
  }
  public GameStatus setLastTime(long aLastTime) {
    lastTime = aLastTime;
    return this;
  }
  public GameStatus setAnswer(boolean aAnswer) {
    isAnswer = aAnswer;
    return this;
  }
  @Override public String toString() {
    return "GameStatus{" +
        "id='" + id + '\'' +
        ", status='" + status + '\'' +
        ", wordTrue=" + wordTrue +
        ", wordFalse=" + wordFalse +
        ", lastTime=" + lastTime +
        ", isAnswer=" + isAnswer +
        '}';
  }
}
