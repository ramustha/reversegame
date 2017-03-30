package com.ramusthastudio.reversegame.model;

public class GameWord {
  private String id;
  private String wordQuest;
  private String wordAnswer;
  private int wordCount;
  private int gameLevel;
  private long startQuest;
  private long startAnswer;

  public GameWord(String aId, int aWordCount, int aGameLevel) {
    id = aId;
    wordCount = aWordCount;
    gameLevel = aGameLevel;
  }

  public GameWord(String aId, String aWordQuest, String aWordAnswer, int aWordCount, int aGameLevel, long aStartQuest, long aStartAnswer) {
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
  public int getWordCount() { return wordCount; }
  public int getGameLevel() { return gameLevel; }
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
  public GameWord setWordCount(int aWordCount) {
    wordCount = aWordCount;
    return this;
  }
  public GameWord setGameLevel(int aGameLevel) {
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
