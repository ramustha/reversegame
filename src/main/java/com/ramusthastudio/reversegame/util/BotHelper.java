package com.ramusthastudio.reversegame.util;

import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.Multicast;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.message.template.Template;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.response.BotApiResponse;
import com.ramusthastudio.reversegame.model.GameLeaderboard;
import com.ramusthastudio.reversegame.model.UserLine;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import static com.ramusthastudio.reversegame.util.StickerHelper.JAMES_STICKER_TWO_THUMBS;
import static com.ramusthastudio.reversegame.util.WordsHelper.getRandomSmall;

public final class BotHelper {
  private static final Logger LOG = LoggerFactory.getLogger(BotHelper.class);

  public static final String SOURCE_USER = "user";
  public static final String SOURCE_GROUP = "group";
  public static final String SOURCE_ROOM = "room";

  public static final String JOIN = "join";
  public static final String FOLLOW = "follow";
  public static final String UNFOLLOW = "unfollow";
  public static final String MESSAGE = "message";
  public static final String LEAVE = "leave";
  public static final String POSTBACK = "postback";
  public static final String BEACON = "beacon";

  public static final String MESSAGE_TEXT = "text";
  public static final String MESSAGE_IMAGE = "image";
  public static final String MESSAGE_VIDEO = "video";
  public static final String MESSAGE_AUDIO = "audio";
  public static final String MESSAGE_LOCATION = "location";
  public static final String MESSAGE_STICKER = "sticker";

  public static final String KEY_START_GAME = "start";
  public static final String KEY_STOP_GAME = "stop";
  public static final String KEY_LEADERBOARD = "peringkat";
  public static final String KEY_HELP = "help";

  public static final String IMG_GOLD = "https://docs.google.com/uc?id=0B-F-b_ahxeRqRUgyWUo4WmVjY1k";
  public static final String IMG_SILVER = "https://docs.google.com/uc?id=0B-F-b_ahxeRqdXlVV1FPbEtUMlE";
  public static final String IMG_BRONZE = "https://docs.google.com/uc?id=0B-F-b_ahxeRqWm15dUhmN0hibU0";

  public static UserProfileResponse getUserProfile(String aChannelAccessToken,
      String aUserId) throws IOException {
    LOG.info("getUserProfile...");
    return LineMessagingServiceBuilder
        .create(aChannelAccessToken)
        .build().getProfile(aUserId).execute().body();
  }

  public static Response<BotApiResponse> replayMessage(String aChannelAccessToken, String aReplayToken,
      String aMsg) throws IOException {
    TextMessage message = new TextMessage(aMsg);
    ReplyMessage pushMessage = new ReplyMessage(aReplayToken, message);
    LOG.info("replayMessage...");
    return LineMessagingServiceBuilder
        .create(aChannelAccessToken)
        .build().replyMessage(pushMessage).execute();
  }

  public static Response<BotApiResponse> pushMessage(String aChannelAccessToken, String aUserId,
      String aMsg) throws IOException {
    TextMessage message = new TextMessage(aMsg);
    PushMessage pushMessage = new PushMessage(aUserId, message);
    LOG.info("pushMessage...");
    return LineMessagingServiceBuilder
        .create(aChannelAccessToken)
        .build().pushMessage(pushMessage).execute();
  }

  public static Response<BotApiResponse> multicastMessage(String aChannelAccessToken, Set<String> aUserIds,
      String aMsg) throws IOException {
    TextMessage message = new TextMessage(aMsg);
    Multicast pushMessage = new Multicast(aUserIds, message);
    LOG.info("multicastMessage...");
    return LineMessagingServiceBuilder
        .create(aChannelAccessToken)
        .build().multicast(pushMessage).execute();
  }

  public static Response<BotApiResponse> templateMessage(String aChannelAccessToken, String aUserId,
      Template aTemplate) throws IOException {
    TemplateMessage message = new TemplateMessage("Result", aTemplate);
    PushMessage pushMessage = new PushMessage(aUserId, message);
    LOG.info("templateMessage...");
    return LineMessagingServiceBuilder
        .create(aChannelAccessToken)
        .build().pushMessage(pushMessage).execute();
  }

  public static Response<BotApiResponse> stickerMessage(String aChannelAccessToken, String aUserId,
      StickerHelper.StickerMsg aSt) throws IOException {
    StickerMessage message = new StickerMessage(aSt.pkgId(), aSt.id());
    PushMessage pushMessage = new PushMessage(aUserId, message);
    LOG.info("stickerMessage...");
    return LineMessagingServiceBuilder
        .create(aChannelAccessToken)
        .build().pushMessage(pushMessage).execute();
  }

  public static Response<BotApiResponse> carouselMessage(String aChannelAccessToken,
      List<GameLeaderboard> aGameLeaderboards, String aUserId) throws IOException {
    List<CarouselColumn> carouselColumn = buildCarouselColumn(aGameLeaderboards);
    CarouselTemplate template = new CarouselTemplate(carouselColumn);
    return templateMessage(aChannelAccessToken, aUserId, template);
  }

  public static List<CarouselColumn> buildCarouselColumn(List<GameLeaderboard> aGameLeaderboards) {
    int index = 0;
    List<CarouselColumn> carouselColumn = new ArrayList<>();
    for (GameLeaderboard leaderboard : aGameLeaderboards) {
      String title = createTitle(leaderboard.getUsername());
      String desc = createTagline(leaderboard);
      String profile = leaderboard.getProfileUrl() == null ? IMG_GOLD : leaderboard.getProfileUrl();
      String poster = IMG_GOLD;
      if (index == 1) {
        poster = IMG_SILVER;
      } else if (index > 1) {
        poster = IMG_BRONZE;
      }
      index++;
      LOG.info("Result title {}\n desc {}\n poster {}\n", title, desc, poster);

      List<Action> buttons = Collections.singletonList(
          new URIAction("Profile ", profile));
      carouselColumn.add(new CarouselColumn(poster, title, desc, buttons));
    }

    return carouselColumn;
  }

  public static void greetingMessageGroup(String aChannelAccessToken, String aUserId) throws IOException {
    String greeting = "Hi manteman\n";
    greeting += "Makasih aku udah di invite disini!\n";
    greeting += "Biar lebih seru ajak donk teman teman lain ke sini supaya lebih seru.\n";
    greeting += "Ini id aku @gdo0972e";
    stickerMessage(aChannelAccessToken, aUserId, new StickerHelper.StickerMsg(JAMES_STICKER_TWO_THUMBS));
    pushMessage(aChannelAccessToken, aUserId, greeting);
  }

  public static void greetingMessage(String aChannelAccessToken, String aUserId) throws IOException {
    UserProfileResponse userProfile = getUserProfile(aChannelAccessToken, aUserId);
    String greeting = "Hi " + userProfile.getDisplayName() + "\n";
    greeting += "Makasih udah nambahin aku sebagai teman!\n";
    greeting += "Aku punya game simple nih buat kamu, biar lebih seru ajak donk teman teman kamu buat add aku sebagai teman.\n";
    greeting += "Ini id aku @gdo0972e";
    stickerMessage(aChannelAccessToken, aUserId, new StickerHelper.StickerMsg(JAMES_STICKER_TWO_THUMBS));
    pushMessage(aChannelAccessToken, aUserId, greeting);
  }

  public static void instructionMessage(String aChannelAccessToken, String aUserId) throws IOException {
    UserProfileResponse userProfile = getUserProfile(aChannelAccessToken, aUserId);
    String answer = getRandomSmall();
    String quest = new StringBuffer(answer).reverse().toString();

    String greeting = "Hi " + userProfile.getDisplayName() + "\n";
    greeting += "Aturannya simple kok, kamu tinggal jawab terbalik kata yang aku kasi ke kamu\n";
    greeting += "Contoh kata '" + quest + "' kamu jawab '" + answer + "'";
    pushMessage(aChannelAccessToken, aUserId, greeting);
  }

  public static void instructionMessageGroup(String aChannelAccessToken, String aUserId) throws IOException {
    String answer = getRandomSmall();
    String quest = new StringBuffer(answer).reverse().toString();
    String greeting = "Aturannya simple kok, kamu tinggal jawab terbalik kata yang aku kasi ke kamu\n";
    greeting += "Contoh kata '" + quest + "' kamu jawab '" + answer + "'";
    pushMessage(aChannelAccessToken, aUserId, greeting);
  }

  public static void unfollowMessage(String aChannelAccessToken, String aUserId) throws IOException {
    UserProfileResponse userProfile = getUserProfile(aChannelAccessToken, aUserId);
    String greeting = "Hi " + userProfile.getDisplayName() + "\n";
    greeting += "Udah bosen sama aku ?";
    pushMessage(aChannelAccessToken, aUserId, greeting);
  }

  public static Response<BotApiResponse> confirmStartGame(String aChannelAccessToken, String aUserId) throws IOException {
    ConfirmTemplate template = new ConfirmTemplate("Mulai permainan ?", Arrays.asList(
        new PostbackAction("Mulai", KEY_START_GAME),
        new PostbackAction("Peringkat", KEY_LEADERBOARD)
    ));
    return templateMessage(aChannelAccessToken, aUserId, template);
  }

  public static Response<BotApiResponse> confirmHelpGame(String aChannelAccessToken, String aUserId) throws IOException {
    ConfirmTemplate template = new ConfirmTemplate("Mulai permainan ?", Arrays.asList(
        new PostbackAction("Mulai", KEY_START_GAME),
        new PostbackAction("Bantuan", KEY_HELP)
    ));
    return templateMessage(aChannelAccessToken, aUserId, template);
  }

  public static int generateRandom(int min, int max) {
    Random r = new Random();
    return r.nextInt(max - min) + min;
  }

  public static String predictWord(String aText, String aFind) {
    Pattern word = Pattern.compile(aFind);
    Matcher match = word.matcher(aText);
    String result = "";
    while (match.find()) {
      String predictAfterKey = removeAnySymbol(aText.substring(match.end(), aText.length())).trim();

      if (predictAfterKey.length() > 0) {
        if (predictAfterKey.contains(" ")) {
          String[] predictAfterKeySplit = predictAfterKey.split(" ");
          result = predictAfterKeySplit[0];
        } else {
          result = predictAfterKey;
        }
        return result;
      }
    }
    return result;
  }

  public static String removeAnySymbol(String s) {
    Pattern pattern = Pattern.compile("[^a-z A-Z^0-9]");
    Matcher matcher = pattern.matcher(s);
    return matcher.replaceAll(" ");
  }

  public static String reverseString(String aString) {
    return String.valueOf(new StringBuffer(aString).reverse());
  }

  public static String createTitle(String aTitle) {
    String filterTitle;
    if (aTitle.length() > 30) {
      filterTitle = aTitle.substring(0, 30) + "...";
    } else {
      filterTitle = aTitle;
    }
    return filterTitle;
  }

  public static String createTagline(GameLeaderboard aGameLeaderboard) {
    int bestScore = aGameLeaderboard.getBestScore();
    int bestAnswerTime = aGameLeaderboard.getBestAnswerTime();
    int averageAnswerTime = aGameLeaderboard.getAverageAnswerTime();

    String decs = "Best score : " + bestScore + " Kata" + "\n" +
        "Best answer time : " + ((double) bestAnswerTime / 1000) + " detik";
    String filterTitle;
    if (decs.length() > 55) {
      filterTitle = decs.substring(0, 55) + "...";
    } else {
      filterTitle = decs;
    }
    return filterTitle;
  }
}
