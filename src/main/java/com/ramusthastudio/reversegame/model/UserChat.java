package com.ramusthastudio.reversegame.model;

public class UserChat {
  private String userId;
  private String lastChat;
  private long lastTime;
  private int falseCount;

  public UserChat(String aUserId, String aLastChat, long aLastTime) {
    userId = aUserId;
    lastChat = aLastChat;
    lastTime = aLastTime;
  }

  public UserChat(String aUserId, String aLastChat, long aLastTime, int aFalseCount) {
    userId = aUserId;
    lastChat = aLastChat;
    lastTime = aLastTime;
    falseCount = aFalseCount;
  }

  public String getUserId() { return userId; }
  public String getLastChat() { return lastChat; }
  public long getLastTime() { return lastTime; }
  public int getFalseCount() { return falseCount; }

  public UserChat setUserId(String aUserId) {
    userId = aUserId;
    return this;
  }
  public UserChat setLastChat(String aLastChat) {
    lastChat = aLastChat;
    return this;
  }
  public UserChat setLastTime(long aLastTime) {
    lastTime = aLastTime;
    return this;
  }

  public UserChat setFalseCount(int aFalseCount) {
    falseCount = aFalseCount;
    return this;
  }

  @Override public String toString() {
    return "UserChat{" +
        "userId='" + userId + '\'' +
        ", lastChat='" + lastChat + '\'' +
        ", lastTime=" + lastTime +
        ", falseCount=" + falseCount +
        '}';
  }
}
