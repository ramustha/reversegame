package com.ramusthastudio.reversegame.task;

import com.ramusthastudio.reversegame.database.Dao;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.ramusthastudio.reversegame.util.BotHelper.pushMessage;

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

  @Scheduled(fixedRate = 5000)
  public void reportCurrentTime() {
    // try {
    //   Date now = new Date();
    //   // LOG.info("The time is now {}", dateFormat.format(now));
    //   List<UserChat> userChat = mDao.getAllUserChat();
    //   if (userChat != null && userChat.size() > 0) {
    //     for (UserChat chat : userChat) {
    //       botChatOnTwoDay(now, chat);
    //     }
    //   }
    // } catch (Exception aE) {
    //   LOG.error("ScheduledTasks error message : " + aE.getMessage());
    // }
  }

  // private void botChatOnTwoDay(Date aNow, UserChat chat) {
  //   Timestamp lastTimeChat = new Timestamp(chat.getLastTime());
  //   LocalDateTime timeLimit = lastTimeChat.toLocalDateTime().plusDays(2);
  //   if (timeLimit.isBefore(LocalDateTime.now())) {
  //     LOG.info("Start push message");
  //     try {
  //       String text = "Kmana aja ? kok gak ngobrol sama aku lagi ?";
  //       pushMessage(fChannelAccessToken, chat.getUserId(), text);
  //       mDao.updateUserChat(new UserChat(chat.getUserId(), text, aNow.getTime()));
  //     } catch (IOException aE) {
  //       LOG.error("Start push message error message : " + aE.getMessage());
  //     }
  //   }
  // }
}
