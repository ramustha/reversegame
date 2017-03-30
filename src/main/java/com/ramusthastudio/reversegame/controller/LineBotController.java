package com.ramusthastudio.reversegame.controller;

import com.google.gson.Gson;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.ramusthastudio.reversegame.database.Dao;
import com.ramusthastudio.reversegame.model.Events;
import com.ramusthastudio.reversegame.model.GameLeaderboard;
import com.ramusthastudio.reversegame.model.GameStatus;
import com.ramusthastudio.reversegame.model.GameWord;
import com.ramusthastudio.reversegame.model.Message;
import com.ramusthastudio.reversegame.model.Payload;
import com.ramusthastudio.reversegame.model.Postback;
import com.ramusthastudio.reversegame.model.Source;
import com.ramusthastudio.reversegame.model.UserChat;
import com.ramusthastudio.reversegame.model.UserLine;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.ramusthastudio.reversegame.util.BotHelper.FOLLOW;
import static com.ramusthastudio.reversegame.util.BotHelper.JOIN;
import static com.ramusthastudio.reversegame.util.BotHelper.KEY_HELP;
import static com.ramusthastudio.reversegame.util.BotHelper.KEY_LEADERBOARD;
import static com.ramusthastudio.reversegame.util.BotHelper.KEY_START_GAME;
import static com.ramusthastudio.reversegame.util.BotHelper.KEY_STOP_GAME;
import static com.ramusthastudio.reversegame.util.BotHelper.LEAVE;
import static com.ramusthastudio.reversegame.util.BotHelper.MESSAGE;
import static com.ramusthastudio.reversegame.util.BotHelper.MESSAGE_TEXT;
import static com.ramusthastudio.reversegame.util.BotHelper.POSTBACK;
import static com.ramusthastudio.reversegame.util.BotHelper.SOURCE_GROUP;
import static com.ramusthastudio.reversegame.util.BotHelper.SOURCE_ROOM;
import static com.ramusthastudio.reversegame.util.BotHelper.SOURCE_USER;
import static com.ramusthastudio.reversegame.util.BotHelper.UNFOLLOW;
import static com.ramusthastudio.reversegame.util.BotHelper.confirmStartGame;
import static com.ramusthastudio.reversegame.util.BotHelper.getUserProfile;
import static com.ramusthastudio.reversegame.util.BotHelper.greetingMessage;
import static com.ramusthastudio.reversegame.util.BotHelper.greetingMessageGroup;
import static com.ramusthastudio.reversegame.util.BotHelper.instructionMessage;
import static com.ramusthastudio.reversegame.util.BotHelper.replayMessage;
import static com.ramusthastudio.reversegame.util.BotHelper.unfollowMessage;

@RestController
@RequestMapping(value = "/linebot")
public class LineBotController {
  private static final Logger LOG = LoggerFactory.getLogger(LineBotController.class);

  @Autowired
  @Qualifier("line.bot.channelSecret")
  String fChannelSecret;
  @Autowired
  @Qualifier("line.bot.channelToken")
  String fChannelAccessToken;
  @Autowired
  Dao fDao;

  Thread fGameThread;

  @RequestMapping(value = "/callback", method = RequestMethod.POST)
  public ResponseEntity<String> callback(
      @RequestHeader("X-Line-Signature") String aXLineSignature,
      @RequestBody String aPayload) {

    LOG.info("XLineSignature: {} ", aXLineSignature);
    LOG.info("Payload: {} ", aPayload);

    LOG.info("The Signature is: {} ", (aXLineSignature != null && aXLineSignature.length() > 0) ? aXLineSignature : "N/A");
    final boolean valid = new LineSignatureValidator(fChannelSecret.getBytes()).validateSignature(aPayload.getBytes(), aXLineSignature);
    LOG.info("The Signature is: {} ", valid ? "valid" : "tidak valid");

    if (aPayload != null && aPayload.length() > 0) {
      Gson gson = new Gson();
      Payload payload = gson.fromJson(aPayload, Payload.class);

      Events event = payload.events()[0];

      String eventType = event.type();
      String replayToken = event.replyToken();
      Source source = event.source();
      long timestamp = event.timestamp();
      Message message = event.message();
      Postback postback = event.postback();

      String userId = source.userId();
      String sourceType = source.type();

      switch (sourceType) {
        case SOURCE_USER:
          sourceUserProccess(eventType, replayToken, timestamp, message, postback, userId);
          break;
        case SOURCE_GROUP:
          // sourceGroupProccess(eventType, replayToken, postback, message, source);
          break;
        case SOURCE_ROOM:
          // sourceGroupProccess(eventType, replayToken, postback, message, source);
          break;
      }
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  private void sourceGroupProccess(String aEventType, String aReplayToken, Postback aPostback, Message aMessage, Source aSource) {
    try {
      switch (aEventType) {
        case LEAVE:
          break;
        case JOIN:
          LOG.info("Greeting Message join group");
          greetingMessageGroup(fChannelAccessToken, aSource.groupId());
          break;
        case MESSAGE:
          if (aMessage.type().equals(MESSAGE_TEXT)) {
            String text = aMessage.text();
          }
          break;
        case POSTBACK:
          String pd = aPostback.data();

          break;
      }
    } catch (IOException aE) { LOG.error("Message {}", aE.getMessage()); }
  }

  private void sourceUserProccess(String aEventType, String aReplayToken, long aTimestamp, Message aMessage, Postback aPostback, String aUserId) {
    try {
      LOG.info("Start setup database...");
      UserProfileResponse profile = getUserProfile(fChannelAccessToken, aUserId);
      UserLine userLineDb = fDao.getUserLineById(profile.getUserId());
      UserChat userChatDb = fDao.getUserChatById(profile.getUserId());
      GameStatus gameStatusDb = fDao.getGameStatusById(profile.getUserId());
      GameWord gameWordDb = fDao.getGameWordById(profile.getUserId());
      GameLeaderboard gameLeaderboardDb = fDao.getGameLeaderboardById(profile.getUserId());
      LOG.info("End setup database...");

      switch (aEventType) {
        case UNFOLLOW:
          unfollowMessage(fChannelAccessToken, aUserId);
          break;
        case FOLLOW:
          LOG.info("Greeting Message");
          greetingMessage(fChannelAccessToken, aUserId);
          instructionMessage(fChannelAccessToken, aUserId);
          confirmStartGame(fChannelAccessToken, aUserId);

          if (userLineDb == null) {
            LOG.info("Start save userLineDb to database...");
            fDao.setUserLine(profile);
          }
          if (userChatDb == null) {
            LOG.info("Start save userChatDb to database...");
            fDao.setUserChat(new UserChat(aUserId, "Greeting Message", aTimestamp));
          }
          if (gameStatusDb == null) {
            LOG.info("Start save gameStatusDb to database...");
            fDao.setGameStatus(new GameStatus(aUserId, KEY_STOP_GAME, aTimestamp));
          }
          if (gameWordDb == null) {
            LOG.info("Start save gameStatusDb to database...");
            fDao.setGameWord(new GameWord(aUserId, 0, 0));
          }
          if (gameLeaderboardDb == null) {
            LOG.info("Start save gameLeaderboardDb to database...");
            fDao.setGameLeaderboard(new GameLeaderboard(aUserId, profile.getDisplayName()));
          }

          break;
        case MESSAGE:
          String type = aMessage.type();
          String text = aMessage.text();
          if (type.equals(MESSAGE_TEXT)) {
            if (text.contains(KEY_STOP_GAME)) {
              if (gameStatusDb.getStatus().equalsIgnoreCase(KEY_START_GAME)) {
                replayMessage(fChannelAccessToken, aReplayToken, "Game berhenti...");
                confirmStartGame(fChannelAccessToken, aUserId);

                LOG.info("Start update GameStatus...");
                fDao.updateGameStatus(new GameStatus(aUserId, KEY_STOP_GAME, aTimestamp));
                LOG.info("Start update GameWord...");
                fDao.updateGameWord(new GameWord(aUserId, "", "", 0, 0, 0, 0));
              } else {
                replayMessage(fChannelAccessToken, aReplayToken, "Game nya udah berhenti...");
              }
            } else {
              if (gameStatusDb.getStatus().equalsIgnoreCase(KEY_START_GAME)) {
                LOG.info("User answer..." + text);
                GameWord gameWord = fDao.getGameWordById(aUserId);
                String answer = gameWord.getWordAnswer().trim();
                String userAnswer = text.trim();
                int correct = gameStatusDb.getWordTrue();
                int incorrect = gameStatusDb.getWordFalse();
                if (answer.equalsIgnoreCase(userAnswer)) {
                  LOG.info("Correct answer..." + answer);
                  fDao.updateGameStatus(new GameStatus(aUserId, KEY_START_GAME, ++correct, incorrect, aTimestamp));
                } else {
                  LOG.info("Incorrect answer..." + answer);
                  fDao.updateGameStatus(new GameStatus(aUserId, KEY_START_GAME, correct, ++incorrect, aTimestamp));
                }
              }
            }

          }

          LOG.info("isValidMessage...");
          LOG.info("Start UserChat history...");
          fDao.updateUserChat(new UserChat(aUserId, aMessage.text(), aTimestamp));
          break;
        case POSTBACK:
          String pd = aPostback.data();
          if (pd.contains(KEY_START_GAME)) {
            LOG.info("Start update GameStatus...");
            fDao.updateGameStatus(new GameStatus(aUserId, KEY_START_GAME, aTimestamp));
            LOG.info("Start update GameWord...");
            fDao.updateGameWord(new GameWord(aUserId, 0, 1));

            replayMessage(fChannelAccessToken, aReplayToken, "Game dimulai...");
          } else if (pd.contains(KEY_LEADERBOARD)) {
            replayMessage(fChannelAccessToken, aReplayToken, pd);
          } else if (pd.contains(KEY_HELP)) {
            instructionMessage(fChannelAccessToken, aUserId);
          }
          break;
      }

    } catch (IOException aE) { LOG.error("Message {}", aE.getMessage()); }
  }
}
