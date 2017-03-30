package com.ramusthastudio.reversegame.model;

public class GameLeaderboard {
  private String id;
  private String username;
  private int bestScore;
  private int bestAnswerTime;
  private int averageAnswerTime;

  public GameLeaderboard(String aId, String aUsername, int aBestScore, int aBestAnswerTime, int aAverageAnswerTime) {
    id = aId;
    username = aUsername;
    bestScore = aBestScore;
    bestAnswerTime = aBestAnswerTime;
    averageAnswerTime = aAverageAnswerTime;
  }

  public String getId() { return id; }
  public String getUsername() { return username; }
  public int getBestScore() { return bestScore; }
  public int getBestAnswerTime() { return bestAnswerTime; }
  public int getAverageAnswerTime() { return averageAnswerTime; }

  public GameLeaderboard setId(String aId) {
    id = aId;
    return this;
  }
  public GameLeaderboard setUsername(String aUsername) {
    username = aUsername;
    return this;
  }
  public GameLeaderboard setBestScore(int aBestScore) {
    bestScore = aBestScore;
    return this;
  }
  public GameLeaderboard setBestAnswerTime(int aBestAnswerTime) {
    bestAnswerTime = aBestAnswerTime;
    return this;
  }
  public GameLeaderboard setAverageAnswerTime(int aAverageAnswerTime) {
    averageAnswerTime = aAverageAnswerTime;
    return this;
  }

  @Override public String toString() {
    return "GameLeaderboard{" +
        "id='" + id + '\'' +
        ", username='" + username + '\'' +
        ", bestScore=" + bestScore +
        ", bestAnswerTime=" + bestAnswerTime +
        ", averageAnswerTime=" + averageAnswerTime +
        '}';
  }
}
