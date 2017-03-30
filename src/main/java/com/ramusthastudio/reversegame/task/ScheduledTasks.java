package com.ramusthastudio.reversegame.task;

import com.ramusthastudio.reversegame.database.Dao;
import com.ramusthastudio.reversegame.model.GameStatus;
import com.ramusthastudio.reversegame.model.GameWord;
import com.ramusthastudio.reversegame.model.UserChat;
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
import static com.ramusthastudio.reversegame.util.BotHelper.pushMessage;
import static com.ramusthastudio.reversegame.util.WordsHelper.getRandomSmall;

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
  Dao mDao;

  private static String fId;
  private static long fReplayTime;

  public static void setUserReplay(String aId, long aReplayTime){
    fId = aId;
    fReplayTime = aReplayTime;
  }

  @Scheduled(fixedRate = 1000)
  public void StartingGame() {
    List<GameStatus> gameStatuses = mDao.getAllGameStatus();
    if (gameStatuses != null && gameStatuses.size() > 0) {
      for (GameStatus gameStatus : gameStatuses) {
        if (gameStatus.getStatus().equalsIgnoreCase(KEY_START_GAME)) {
          String answer = getRandomSmall();
          String quest = new StringBuffer(answer).reverse().toString();
          LOG.info("StartingGame.... Quest : {} Answer : {}", quest, answer);
          LOG.info("User Id : {} Replay time : {}", fId, fReplayTime);

          // List<GameWord> gameWords = mDao.getAllGameWord();
          // if (gameWords != null && gameWords.size() > 0) {
          //
          // }
          // mDao.setGameWord(
          //     new GameWord(
          //         quest,
          //         answer,
          //
          //     ));
        }
      }
    }
  }

  @Scheduled(fixedRate = 5000)
  public void reportCurrentTime() {
    try {
      Date now = new Date();
      // LOG.info("The time is now {}", dateFormat.format(now));
      List<UserChat> userChat = mDao.getAllUserChat();
      if (userChat != null && userChat.size() > 0) {
        for (UserChat chat : userChat) {
          botChatOnTwoDay(now, chat);
        }
      }
    } catch (Exception aE) {
      LOG.error("ScheduledTasks error message : " + aE.getMessage());
    }
  }

  private void botChatOnTwoDay(Date aNow, UserChat chat) {
    Timestamp lastTimeChat = new Timestamp(chat.getLastTime());
    LocalDateTime timeLimit = lastTimeChat.toLocalDateTime().plusDays(2);
    if (timeLimit.isBefore(LocalDateTime.now())) {
      LOG.info("Start push message");
      try {
        String text = "Kmana aja ? kok gak ngobrol sama aku lagi ?";
        pushMessage(fChannelAccessToken, chat.getUserId(), text);
        mDao.updateUserChat(new UserChat(chat.getUserId(), text, aNow.getTime()));
      } catch (IOException aE) {
        LOG.error("Start push message error message : " + aE.getMessage());
      }
    }
  }
}
