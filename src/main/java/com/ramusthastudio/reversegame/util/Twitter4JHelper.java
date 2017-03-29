package com.ramusthastudio.reversegame.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.Strings;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class Twitter4JHelper implements RateLimitStatusListener {
  private static final Logger LOG = LoggerFactory.getLogger(Twitter4JHelper.class);
  String fTwitterConsumerKey;
  String fTwitterConsumerSecret;
  String fTwitterAccessToken;
  String fTwitterAccessSecret;

  Twitter twitter = null;

  boolean rateLimited = false;
  long rateLimitResetTime = -1;

  public Twitter4JHelper() {
    // Validate that these are set and throw an error if they are not
    ArrayList<String> nullPropNames = new ArrayList<>();

    fTwitterConsumerKey = System.getenv("oauth.consumerKey");
    fTwitterConsumerSecret = System.getenv("oauth.consumerSecret");
    fTwitterAccessToken = System.getenv("oauth.accessToken");
    fTwitterAccessSecret = System.getenv("oauth.accessTokenSecret");

    if (Strings.isNullOrEmpty(fTwitterConsumerKey)) { nullPropNames.add(fTwitterConsumerKey); }
    if (Strings.isNullOrEmpty(fTwitterConsumerSecret)) {
      nullPropNames.add(fTwitterConsumerSecret);
    }
    if (Strings.isNullOrEmpty(fTwitterAccessToken)) { nullPropNames.add(fTwitterAccessToken); }
    if (Strings.isNullOrEmpty(fTwitterAccessSecret)) { nullPropNames.add(fTwitterAccessSecret); }
    if (nullPropNames.size() > 0) {
      LOG.error(
          "Cannot load the twitter credentials from the properties. The properties "
              + " are null or empty");
    }

    LOG.info("TwitterConsumerKey :" + fTwitterConsumerKey);
    LOG.info("TwitterConsumerSecret :" + fTwitterConsumerSecret);
    LOG.info("TwitterAccessToken :" + fTwitterAccessToken);
    LOG.info("TwitterAccessSecret :" + fTwitterAccessSecret);

    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setDebugEnabled(true)
        .setOAuthConsumerKey(fTwitterConsumerKey)
        .setOAuthConsumerSecret(fTwitterConsumerSecret)
        .setOAuthAccessToken(fTwitterAccessToken)
        .setOAuthAccessTokenSecret(fTwitterAccessSecret);
    TwitterFactory tf = new TwitterFactory(cb.build());
    twitter = tf.getInstance();
    twitter.addRateLimitStatusListener(this);
  }

  public String getUserImage(Status status) {
    return status.getUser().getProfileImageURL();
  }

  public Twitter getTwitter() {
    return twitter;
  }
  public String convertTweetsToPIContentItems(List<Status> tweets) throws Exception {
    Writer content = new StringWriter();
    JsonFactory factory = new JsonFactory();
    JsonGenerator gen = factory.createGenerator(content);
    gen.writeStartObject();
    gen.writeArrayFieldStart("contentItems");

    if (tweets.size() > 0) {
      String userIdStr = Long.toString(tweets.get(0).getUser().getId());
      for (Status status : tweets) {
        // Add the tweet text to the contentItems
        gen.writeStartObject();
        gen.writeStringField("userid", userIdStr);
        gen.writeStringField("id", Long.toString(status.getId()));
        gen.writeStringField("sourceid", "twitter4j");
        gen.writeStringField("contenttype", "text/plain");
        gen.writeStringField("language", status.getLang());
        gen.writeStringField("content", status.getText().replaceAll("[^(\\x20-\\x7F)]*", ""));
        gen.writeNumberField("created", status.getCreatedAt().getTime());
        gen.writeBooleanField("reply", (status.getInReplyToScreenName() != null));
        gen.writeBooleanField("forward", status.isRetweet());
        gen.writeEndObject();
      }
    }
    gen.writeEndArray();
    gen.writeEndObject();
    gen.flush();

    return content.toString();
  }

  public ResponseList<User> searchUsers(String idOrHandle) throws Exception {
    return twitter.searchUsers(idOrHandle, 20);
  }

  public String profileImgUser(String idOrHandle) throws Exception {
    String path = "";
    if (idOrHandle.startsWith("@")) {
      // Check rate limit
      checkRateLimitAndThrow();
      User user = twitter.showUser(idOrHandle.substring(1));
      if (user == null) {
        throw new Exception("Handle " + idOrHandle + " is not a valid twitter handle.");
      }
      path = user.getOriginalProfileImageURL();
    } else if (!idOrHandle.startsWith("@")) {
      // Check rate limit
      checkRateLimitAndThrow();
      User user = twitter.showUser(idOrHandle);
      if (user == null) {
        throw new Exception("Handle " + idOrHandle + " is not a valid twitter handle.");
      }
      path = user.getOriginalProfileImageURL();
    }
    return path;
  }

  public User checkUsers(String idOrHandle) throws Exception {
    long userId = -1;
    if (idOrHandle.startsWith("@")) {
      // Check rate limit
      checkRateLimitAndThrow();
      User user = twitter.showUser(idOrHandle.substring(1));
      if (user == null) {
        throw new Exception("Handle " + idOrHandle + " is not a valid twitter handle.");
      }
      return user;
    } else if (!idOrHandle.startsWith("@")) {
      // Check rate limit
      checkRateLimitAndThrow();
      User user = twitter.showUser(idOrHandle);
      if (user == null) {
        throw new Exception("Handle " + idOrHandle + " is not a valid twitter handle.");
      }
      return user;
    }

    return null;
  }

  public List<Status> getTweets(String idOrHandle, Set<String> langs, int numberOfNonRetweets) throws Exception {
    List<Status> retval = new ArrayList<>();
    long userId = -1;
    if (idOrHandle.startsWith("@")) {
      // Check rate limit
      checkRateLimitAndThrow();
      User user = twitter.showUser(idOrHandle.substring(1));
      if (user == null) {
        throw new Exception("Handle " + idOrHandle + " is not a valid twitter handle.");
      }
      userId = user.getId();
    } else if (!idOrHandle.startsWith("@")) {
      // Check rate limit
      checkRateLimitAndThrow();
      User user = twitter.showUser(idOrHandle);
      if (user == null) {
        throw new Exception("Handle " + idOrHandle + " is not a valid twitter handle.");
      }
      userId = user.getId();
    } else {
      userId = Long.valueOf(idOrHandle);
    }

    long cursor = -1;
    Paging page = new Paging(1, 200);
    do {
      checkRateLimitAndThrow();
      ResponseList<Status> tweets = twitter.getUserTimeline(userId, page);
      if (tweets == null || tweets.size() == 0) break;
      for (int i = 0; i < tweets.size(); i++) {
        Status status = tweets.get(i);
        cursor = status.getId() - 1;

        // Ignore retweets
        if (status.isRetweet()) continue;
        // Language
        if (!langs.contains(status.getLang())) continue;
        retval.add(status);
        if (retval.size() >= numberOfNonRetweets) return retval;
      }
      page.maxId(cursor);
    } while (true);
    return retval;
  }

  public static HashSet<String> langs() {
    HashSet<String> langs = new HashSet<>();
    langs.add("en");
    langs.add("es");
    langs.add("ar");
    langs.add("ja");
    return langs;
  }

  public String getTweets(String idOrHandle, int numberOfNonRetweets) throws Exception {
    HashSet<String> langs = langs();
    List<Status> retval = new ArrayList<>();
    long userId = -1;
    if (idOrHandle.startsWith("@")) {
      // Check rate limit
      checkRateLimitAndThrow();
      User user = twitter.showUser(idOrHandle.substring(1));
      if (user == null) {
        throw new Exception("Handle " + idOrHandle + " is not a valid twitter handle.");
      }
      userId = user.getId();
    } else if (!idOrHandle.startsWith("@")) {
      // Check rate limit
      checkRateLimitAndThrow();
      User user = twitter.showUser(idOrHandle);
      if (user == null) {
        throw new Exception("Handle " + idOrHandle + " is not a valid twitter handle.");
      }
      userId = user.getId();
    } else {
      userId = Long.valueOf(idOrHandle);
    }

    long cursor = -1;
    Paging page = new Paging(1, 200);
    do {
      checkRateLimitAndThrow();
      ResponseList<Status> tweets = twitter.getUserTimeline(userId, page);
      if (tweets == null || tweets.size() == 0) break;
      for (int i = 0; i < tweets.size(); i++) {
        Status status = tweets.get(i);
        cursor = status.getId() - 1;

        // Ignore retweets
        if (status.isRetweet()) continue;
        // Language
        if (!langs.contains(status.getLang())) continue;
        retval.add(status);
        if (retval.size() >= numberOfNonRetweets) return convertTweetsToPIContentItems(retval);
      }
      page.maxId(cursor);
    } while (true);
    return convertTweetsToPIContentItems(retval);
  }

  public List<Status> getTweets(String idOrHandle, Paging paging) throws Exception {
    List<Status> retval = new ArrayList<>();
    long userId = -1;
    if (idOrHandle.startsWith("@")) {
      // Check rate limit
      checkRateLimitAndThrow();
      User user = twitter.showUser(idOrHandle.substring(1));
      if (user == null) {
        throw new Exception("Handle " + idOrHandle + " is not a valid twitter handle.");
      }
      userId = user.getId();
    } else if (!idOrHandle.startsWith("@")) {
      // Check rate limit
      checkRateLimitAndThrow();
      User user = twitter.showUser(idOrHandle);
      if (user == null) {
        throw new Exception("Handle " + idOrHandle + " is not a valid twitter handle.");
      }
      userId = user.getId();
    } else {
      userId = Long.valueOf(idOrHandle);
    }

    long cursor = -1;
    do {
      checkRateLimitAndThrow();
      ResponseList<Status> tweets = twitter.getUserTimeline(userId, paging);
      if (tweets == null || tweets.size() == 0) break;
      for (int i = 0; i < tweets.size(); i++) {
        Status status = tweets.get(i);
        cursor = status.getId() - 1;

        // Ignore retweets
        if (!status.isRetweet()) {
          retval.add(status);
        }
      }
      paging.maxId(cursor);
    } while (true);
    return retval;
  }

  private synchronized void setRateLimitStatus(boolean rateLimitReached, long resetTime) {
    rateLimited = rateLimitReached;
    rateLimitResetTime = resetTime;
  }

  private synchronized boolean isRateLimited() {
    if (rateLimited && System.currentTimeMillis() > rateLimitResetTime) {
      rateLimited = false;
      rateLimitResetTime = -1;
    }
    return rateLimited;
  }

  private void checkRateLimitAndThrow() throws Exception {
    if (isRateLimited()) {
      throw new Exception("The twitter api rate limit has been hit.  " +
          "No more requests will be sent until the rate limit resets at " + LocalTime.from(Instant.ofEpochMilli(rateLimitResetTime)));
    }
  }

  @Override
  public void onRateLimitReached(RateLimitStatusEvent rlStatusEvent) {
    RateLimitStatus rls = rlStatusEvent.getRateLimitStatus();
    setRateLimitStatus(true, ((long) rls.getResetTimeInSeconds()) * 1000L);
    System.err.println("Twitter rate limit reached, stopping all requests for " + rls.getSecondsUntilReset() + " seconds");
  }

  @Override
  public void onRateLimitStatus(RateLimitStatusEvent rlStatusEvent) {
    @SuppressWarnings("unused")
    RateLimitStatus rls = rlStatusEvent.getRateLimitStatus();
  }

}
