package com.ramusthastudio.reversegame.task;

import com.ramusthastudio.reversegame.database.Dao;
import com.ramusthastudio.reversegame.model.GameLeaderboard;
import com.ramusthastudio.reversegame.model.GameStatus;
import com.ramusthastudio.reversegame.model.GameWord;
import com.ramusthastudio.reversegame.model.UserChat;
import com.ramusthastudio.reversegame.util.StickerHelper;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.ramusthastudio.reversegame.util.BotHelper.KEY_START_GAME;
import static com.ramusthastudio.reversegame.util.BotHelper.KEY_STOP_GAME;
import static com.ramusthastudio.reversegame.util.BotHelper.confirmStartGame;
import static com.ramusthastudio.reversegame.util.BotHelper.pushMessage;
import static com.ramusthastudio.reversegame.util.BotHelper.stickerMessage;
import static com.ramusthastudio.reversegame.util.StickerHelper.JAMES_STICKER_USELESS;
import static com.ramusthastudio.reversegame.util.WordsHelper.getRandomLarge;
import static com.ramusthastudio.reversegame.util.WordsHelper.getRandomMedium;
import static com.ramusthastudio.reversegame.util.WordsHelper.getRandomSmall;
import static java.lang.System.currentTimeMillis;
import static java.time.LocalDateTime.now;

@Component
public class ScheduledTasks {
  private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss");

  @Autowired
  @Qualifier("line.bot.channelSecret")
  String fChannelSecret;

  @Autowired
  @Qualifier("line.bot.channelToken")
  String fChannelAccessToken;

  @Autowired
  Dao fDao;

  int maxLevel = 7;

  @Scheduled(fixedRate = 8000)
  public void StartingGame() throws IOException {
    List<GameStatus> gameStatuses = fDao.getAllGameStatus();
    if (gameStatuses != null) {
      for (GameStatus gameStatus : gameStatuses) {
        if (gameStatus.getStatus().equalsIgnoreCase(KEY_START_GAME)) {
          String userId = gameStatus.getId();
          String status = gameStatus.getStatus();
          int wordTrue = gameStatus.getWordTrue();
          int wordFalse = gameStatus.getWordFalse();
          long lastTime = gameStatus.getLastTime();
          boolean isAnswer = gameStatus.isAnswer();

          if (wordFalse > 3) {
            pushMessage(fChannelAccessToken, userId, "Game over...\nKamu salah menebak sebanyak " + wordFalse + " kali");
            stickerMessage(fChannelAccessToken, userId, new StickerHelper.StickerMsg(JAMES_STICKER_USELESS));
            confirmStartGame(fChannelAccessToken, userId);

            LOG.info("Game over....");
            fDao.updateGameStatus(new GameStatus(userId, KEY_STOP_GAME));

            LOG.info("Update Leaderboard");
            GameLeaderboard lb = fDao.getGameLeaderboardById(userId);
            String username = lb.getUsername();
            String profileUrl = lb.getProfileUrl();
            int bestScore = lb.getBestScore() > wordTrue ? lb.getBestScore() : wordTrue;
            int bestTime = (int) (currentTimeMillis() - lastTime);
            int bestAnswerTime = lb.getBestAnswerTime() < bestTime ? lb.getBestAnswerTime() : bestTime;
            fDao.updateGameLeaderboard(new GameLeaderboard(
                userId,
                username,
                profileUrl,
                bestScore,
                bestAnswerTime, 0));
          } else {
            GameWord gameWord = fDao.getGameWordById(userId);
            int wordCount = gameWord.getWordCount();
            int gameLevel = gameWord.getGameLevel();
            if (wordCount == maxLevel) {
              gameLevel++;
              wordCount = 0;
            } else {
              wordCount++;
            }

            String answer = getRandomSmall();
            String quest = new StringBuffer(answer).reverse().toString();
            if (gameLevel == 1) {
              answer = getRandomSmall();
              quest = new StringBuffer(answer).reverse().toString();
            } else if (gameLevel == 2) {
              answer = getRandomMedium();
              quest = new StringBuffer(answer).reverse().toString();
              maxLevel = 20;
            } else if (gameLevel >= 3) {
              answer = getRandomLarge();
              quest = new StringBuffer(answer).reverse().toString();
              maxLevel = 1000;
            }

            LOG.info("StartingGame.... Quest : {} Answer : {} level {} ", quest, answer, gameLevel);
            pushMessage(fChannelAccessToken, userId, quest);
            fDao.updateGameWord(new GameWord(userId, quest, answer, wordCount, gameLevel, currentTimeMillis(), 0));

            if (!isAnswer) { wordFalse++; }
            fDao.updateGameStatus(new GameStatus(userId, status, wordTrue, wordFalse, lastTime, false));
          }
        }
      }
    }
  }

  @Scheduled(fixedRate = 5000)
  public void reportCurrentTime() {
    try {
      Date now = new Date();
      // LOG.info("The time is now {}", dateFormat.format(now));
      List<UserChat> userChat = fDao.getAllUserChat();
      if (userChat != null && userChat.size() > 0) {
        for (UserChat chat : userChat) {
          botChatOnceDay(now, chat);
        }
      }
    } catch (Exception aE) {
      LOG.error("ScheduledTasks error message : " + aE.getMessage());
    }
  }

  private void botChatOnceDay(Date aNow, UserChat chat) {
    Timestamp lastTimeChat = new Timestamp(chat.getLastTime());
    LocalDateTime timeLimit = lastTimeChat.toLocalDateTime().plusDays(1);
    if (timeLimit.isBefore(now())) {
      LOG.info("Start push message");
      try {
        String text = "Kmana aja ? kok gak main lagi ?";
        pushMessage(fChannelAccessToken, chat.getUserId(), text);
        confirmStartGame(fChannelAccessToken, chat.getUserId());
        fDao.updateUserChat(new UserChat(chat.getUserId(), text, aNow.getTime()));
      } catch (IOException aE) {
        LOG.error("Start push message error message : " + aE.getMessage());
      }
    }
  }
}
