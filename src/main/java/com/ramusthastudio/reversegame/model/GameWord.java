package com.ramusthastudio.reversegame.model;

public class GameWord {
  private String id;
  private String wordQuest;
  private String wordAnswer;
  private String wordCount;
  private String gameLevel;
  private long startQuest;
  private long startAnswer;

  public GameWord(String aId, String aWordQuest, String aWordAnswer, String aWordCount, String aGameLevel, long aStartQuest, long aStartAnswer) {
    id = aId;
    wordQuest = aWordQuest;
    wordAnswer = aWordAnswer;
    wordCount = aWordCount;
    gameLevel = aGameLevel;
    startQuest = aStartQuest;
    startAnswer = aStartAnswer;
  }

  public String getId() { return id; }
  public String getWordQuest() { return wordQuest; }
  public String getWordAnswer() { return wordAnswer; }
  public String getWordCount() { return wordCount; }
  public String getGameLevel() { return gameLevel; }
  public long getStartQuest() { return startQuest; }
  public long getStartAnswer() { return startAnswer; }

  public GameWord setId(String aId) {
    id = aId;
    return this;
  }
  public GameWord setWordQuest(String aWordQuest) {
    wordQuest = aWordQuest;
    return this;
  }
  public GameWord setWordAnswer(String aWordAnswer) {
    wordAnswer = aWordAnswer;
    return this;
  }
  public GameWord setWordCount(String aWordCount) {
    wordCount = aWordCount;
    return this;
  }
  public GameWord setGameLevel(String aGameLevel) {
    gameLevel = aGameLevel;
    return this;
  }
  public GameWord setStartQuest(long aStartQuest) {
    startQuest = aStartQuest;
    return this;
  }
  public GameWord setStartAnswer(long aStartAnswer) {
    startAnswer = aStartAnswer;
    return this;
  }

  @Override public String toString() {
    return "GameWord{" +
        "id='" + id + '\'' +
        ", wordQuest='" + wordQuest + '\'' +
        ", wordAnswer='" + wordAnswer + '\'' +
        ", wordCount='" + wordCount + '\'' +
        ", gameLevel='" + gameLevel + '\'' +
        ", startQuest=" + startQuest +
        ", startAnswer=" + startAnswer +
        '}';
  }
}
