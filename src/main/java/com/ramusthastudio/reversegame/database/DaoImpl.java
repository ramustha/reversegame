package com.ramusthastudio.reversegame.database;

import com.linecorp.bot.model.profile.UserProfileResponse;
import com.ramusthastudio.reversegame.model.GameLeaderboard;
import com.ramusthastudio.reversegame.model.GameStatus;
import com.ramusthastudio.reversegame.model.GameWord;
import com.ramusthastudio.reversegame.model.UserChat;
import com.ramusthastudio.reversegame.model.UserLine;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

public class DaoImpl implements Dao {
  private static final Logger LOG = LoggerFactory.getLogger(DaoImpl.class);

  private final static String SQL_SELECT_ALL_USER_LINE = "SELECT * FROM user_line";
  private final static String SQL_USER_LINE_GET_BY_ID = SQL_SELECT_ALL_USER_LINE + " WHERE LOWER(id) LIKE LOWER(?) ;";
  private final static String SQL_INSERT_USER_LINE = "INSERT INTO user_line (id, display_name, picture_url, status_message) VALUES (?, ?, ?, ?);";

  private final static String SQL_SELECT_ALL_USER_CHAT = "SELECT * FROM user_chat";
  private final static String SQL_USER_CHAT_GET_BY_ID = SQL_SELECT_ALL_USER_CHAT + " WHERE LOWER(id) LIKE LOWER(?) ;";
  private final static String SQL_INSERT_USER_CHAT = "INSERT INTO user_chat (id, last_chat, last_time, false_count) VALUES (?, ?, ?, ?);";
  private final static String SQL_UPDATE_USER_CHAT = "UPDATE user_chat SET last_chat = ?, last_time = ?, false_count = ? WHERE LOWER(id) LIKE LOWER(?);";

  private final static String SQL_SELECT_ALL_GAME_STATUS = "SELECT * FROM game_status";
  private final static String SQL_GAME_STATUS_GET_BY_ID = SQL_SELECT_ALL_GAME_STATUS + " WHERE LOWER(id) LIKE LOWER(?) ;";
  private final static String SQL_INSERT_GAME_STATUS = "INSERT INTO game_status (id, status, word_true, word_false, last_time) VALUES (?, ?, ?, ?, ?);";
  private final static String SQL_UPDATE_GAME_STATUS = "UPDATE game_status SET status=?, word_true=?, word_false=?, last_time=? WHERE LOWER(id) LIKE LOWER(?);";

  private final static String SQL_SELECT_ALL_GAME_WORD = "SELECT * FROM game_word";
  private final static String SQL_GAME_WORD_GET_BY_ID = SQL_SELECT_ALL_GAME_WORD + " WHERE LOWER(id) LIKE LOWER(?) ;";
  private final static String SQL_INSERT_GAME_WORD = "INSERT INTO game_word (id, word_quest, word_answer, word_count, game_level, start_quest, start_answer) VALUES (?, ?, ?, ?, ?, ?, ?);";
  private final static String SQL_UPDATE_GAME_WORD = "UPDATE game_word SET word_quest=?, word_answer=?, word_count=?, game_level=?, start_quest=?, start_answer=? WHERE LOWER(id) LIKE LOWER(?);";
  private final static String SQL_DELETE_GAME_WORD = "DELETE FROM game_word WHERE LOWER(id) LIKE LOWER(?) ;";

  private final static String SQL_SELECT_ALL_GAME_LEADERBOARD = "SELECT * FROM game_leaderboard";
  private final static String SQL_GAME_LEADERBOARD_GET_BY_ID = SQL_SELECT_ALL_GAME_LEADERBOARD + " WHERE LOWER(id) LIKE LOWER(?) ;";
  private final static String SQL_INSERT_GAME_LEADERBOARD = "INSERT INTO game_leaderboard (id, username, best_score, best_answer_time, average_answer_time) VALUES (?, ?, ?, ?, ?);";
  private final static String SQL_UPDATE_GAME_LEADERBOARD = "UPDATE game_leaderboard SET username=?, best_score=?, best_answer_time=?, average_answer_time=? WHERE LOWER(id) LIKE LOWER(?);";

  private final JdbcTemplate mJdbc;

  public DaoImpl(DataSource aDataSource) {
    mJdbc = new JdbcTemplate(aDataSource);
  }

  private final static RowMapper<UserLine> SINGLE_USER_LINE = (aRs, rowNum) ->
      new UserLine(
          aRs.getString("id"),
          aRs.getString("display_name"),
          aRs.getString("picture_url"),
          aRs.getString("status_message"));

  private final static RowMapper<UserChat> SINGLE_USER_CHAT = (aRs, rowNum) ->
      new UserChat(
          aRs.getString("id"),
          aRs.getString("last_chat"),
          aRs.getTimestamp("last_time").getTime(),
          aRs.getInt("false_count")
      );

  private final static RowMapper<GameStatus> SINGLE_GAME_STATUS = (aRs, rowNum) ->
      new GameStatus(
          aRs.getString("id"),
          aRs.getString("status"),
          aRs.getInt("word_true"),
          aRs.getInt("word_false"),
          aRs.getTimestamp("last_time").getTime()
      );

  private final static RowMapper<GameWord> SINGLE_GAME_WORD = (aRs, rowNum) ->
      new GameWord(
          aRs.getString("id"),
          aRs.getString("word_quest"),
          aRs.getString("word_answer"),
          aRs.getInt("word_count"),
          aRs.getInt("game_level"),
          aRs.getTimestamp("start_quest").getTime(),
          aRs.getTimestamp("start_answer").getTime()
      );

  private final static RowMapper<GameLeaderboard> SINGLE_GAME_LEADERBOARD = (aRs, rowNum) ->
      new GameLeaderboard(
          aRs.getString("id"),
          aRs.getString("username"),
          aRs.getInt("best_score"),
          aRs.getInt("best_answer_time"),
          aRs.getInt("average_answer_time")
      );

  private final static ResultSetExtractor<List<UserLine>> MULTIPLE_USER_LINE = aRs -> {
    List<UserLine> list = new ArrayList<>();
    while (aRs.next()) {
      list.add(new UserLine(
          aRs.getString("id"),
          aRs.getString("display_name"),
          aRs.getString("picture_url"),
          aRs.getString("status_message")
      ));
    }
    return list;
  };

  private final static ResultSetExtractor<List<UserChat>> MULTIPLE_USER_CHAT = aRs -> {
    List<UserChat> list = new ArrayList<>();
    while (aRs.next()) {
      list.add(new UserChat(
          aRs.getString("id"),
          aRs.getString("last_chat"),
          aRs.getTimestamp("last_time").getTime(),
          aRs.getInt("false_count")
      ));
    }
    return list;
  };

  private final static ResultSetExtractor<List<GameStatus>> MULTIPLE_GAME_STATUS = aRs -> {
    List<GameStatus> list = new ArrayList<>();
    while (aRs.next()) {
      list.add(new GameStatus(
          aRs.getString("id"),
          aRs.getString("status"),
          aRs.getInt("word_true"),
          aRs.getInt("word_false"),
          aRs.getTimestamp("last_time").getTime()

      ));
    }
    return list;
  };

  private final static ResultSetExtractor<List<GameWord>> MULTIPLE_GAME_WORD = aRs -> {
    List<GameWord> list = new ArrayList<>();
    while (aRs.next()) {
      list.add(new GameWord(
          aRs.getString("id"),
          aRs.getString("word_quest"),
          aRs.getString("word_answer"),
          aRs.getInt("word_count"),
          aRs.getInt("game_level"),
          aRs.getTimestamp("start_quest").getTime(),
          aRs.getTimestamp("start_answer").getTime()

      ));
    }
    return list;
  };

  private final static ResultSetExtractor<List<GameLeaderboard>> MULTIPLE_GAME_LEADERBOARD = aRs -> {
    List<GameLeaderboard> list = new ArrayList<>();
    while (aRs.next()) {
      list.add(new GameLeaderboard(
          aRs.getString("id"),
          aRs.getString("username"),
          aRs.getInt("best_score"),
          aRs.getInt("best_answer_time"),
          aRs.getInt("average_answer_time")

      ));
    }
    return list;
  };

  @Override public void setUserLine(UserProfileResponse aUser) {
    mJdbc.update(SQL_INSERT_USER_LINE,
        aUser.getUserId(),
        aUser.getDisplayName(),
        aUser.getPictureUrl(),
        aUser.getStatusMessage());
  }

  @Override public void setUserChat(UserChat aUser) {
    mJdbc.update(SQL_INSERT_USER_CHAT,
        aUser.getUserId(),
        aUser.getLastChat(),
        new Timestamp(aUser.getLastTime()),
        aUser.getFalseCount()
    );
  }

  @Override public void setGameStatus(GameStatus aGameStatus) {
    mJdbc.update(SQL_INSERT_GAME_STATUS,
        aGameStatus.getId(),
        aGameStatus.getStatus(),
        aGameStatus.getWordTrue(),
        aGameStatus.getWordFalse(),
        new Timestamp(aGameStatus.getLastTime())
    );
  }
  @Override public void setGameWord(GameWord aGameWord) {
    mJdbc.update(SQL_INSERT_GAME_WORD,
        aGameWord.getId(),
        aGameWord.getWordQuest(),
        aGameWord.getWordAnswer(),
        aGameWord.getWordCount(),
        aGameWord.getGameLevel(),
        new Timestamp(aGameWord.getStartQuest()),
        new Timestamp(aGameWord.getStartAnswer())
    );
  }
  @Override public void setGameLeaderboard(GameLeaderboard aGameLeaderboard) {
    mJdbc.update(SQL_INSERT_GAME_LEADERBOARD,
        aGameLeaderboard.getId(),
        aGameLeaderboard.getUsername(),
        aGameLeaderboard.getBestScore(),
        aGameLeaderboard.getBestAnswerTime(),
        aGameLeaderboard.getAverageAnswerTime()
    );
  }
  @Override public void updateUserChat(UserChat aUser) {
    mJdbc.update(SQL_UPDATE_USER_CHAT,
        aUser.getLastChat(),
        new Timestamp(aUser.getLastTime()),
        aUser.getFalseCount(),
        aUser.getUserId()
    );
  }

  @Override public void updateGameStatus(GameStatus aGameStatus) {
    mJdbc.update(SQL_UPDATE_GAME_STATUS,
        aGameStatus.getStatus(),
        aGameStatus.getWordTrue(),
        aGameStatus.getWordFalse(),
        new Timestamp(aGameStatus.getLastTime()),
        aGameStatus.getId()
    );
  }

  @Override public void updateGameWord(GameWord aGameWord) {
    mJdbc.update(SQL_UPDATE_GAME_WORD,
        aGameWord.getWordQuest(),
        aGameWord.getWordAnswer(),
        aGameWord.getWordCount(),
        aGameWord.getGameLevel(),
        new Timestamp(aGameWord.getStartQuest()),
        new Timestamp(aGameWord.getStartAnswer()),
        aGameWord.getId()
    );
  }
  @Override public void updateGameLeaderboard(GameLeaderboard aGameLeaderboard) {
    mJdbc.update(SQL_UPDATE_GAME_LEADERBOARD,
        aGameLeaderboard.getUsername(),
        aGameLeaderboard.getBestScore(),
        aGameLeaderboard.getBestAnswerTime(),
        aGameLeaderboard.getAverageAnswerTime(),
        aGameLeaderboard.getId()
    );
  }

  @Override public void deleteGameWord(String aUserId) {
    mJdbc.query(SQL_DELETE_GAME_WORD, new Object[] {"%" + aUserId + "%"}, SINGLE_GAME_WORD);
  }

  @Override public List<UserLine> getAllUserLine() {
    return mJdbc.query(SQL_SELECT_ALL_USER_LINE, MULTIPLE_USER_LINE);
  }

  @Override public List<UserChat> getAllUserChat() {
    return mJdbc.query(SQL_SELECT_ALL_USER_CHAT, MULTIPLE_USER_CHAT);
  }

  @Override public List<GameStatus> getAllGameStatus() {
    return mJdbc.query(SQL_SELECT_ALL_GAME_STATUS, MULTIPLE_GAME_STATUS);
  }
  @Override public List<GameWord> getAllGameWord() {
    return mJdbc.query(SQL_SELECT_ALL_GAME_WORD, MULTIPLE_GAME_WORD);
  }
  @Override public List<GameLeaderboard> getAllGameLeaderboard() {
    return mJdbc.query(SQL_SELECT_ALL_GAME_LEADERBOARD, MULTIPLE_GAME_LEADERBOARD);
  }

  @Override public UserLine getUserLineById(String aUserId) {
    try {
      return mJdbc.queryForObject(SQL_USER_LINE_GET_BY_ID, new Object[] {"%" + aUserId + "%"}, SINGLE_USER_LINE);
    } catch (Exception e) {
      LOG.error("Error when trying get UserLine cause : " + e.getMessage());
      return null;
    }
  }

  @Override public UserChat getUserChatById(String aUserId) {
    try {
      return mJdbc.queryForObject(SQL_USER_CHAT_GET_BY_ID, new Object[] {"%" + aUserId + "%"}, SINGLE_USER_CHAT);
    } catch (Exception e) {
      LOG.error("Error when trying get UserChat cause : " + e.getMessage());
      return null;
    }
  }
  @Override public GameStatus getGameStatusById(String aUserId) {
    try {
      return mJdbc.queryForObject(SQL_GAME_STATUS_GET_BY_ID, new Object[] {"%" + aUserId + "%"}, SINGLE_GAME_STATUS);
    } catch (Exception e) {
      LOG.error("Error when trying get GameStatus cause : " + e.getMessage());
      return null;
    }
  }
  @Override public GameWord getGameWordById(String aUserId) {
    try {
      return mJdbc.queryForObject(SQL_GAME_WORD_GET_BY_ID, new Object[] {"%" + aUserId + "%"}, SINGLE_GAME_WORD);
    } catch (Exception e) {
      LOG.error("Error when trying get GameWord cause : " + e.getMessage());
      return null;
    }
  }
  @Override public GameLeaderboard getGameLeaderboardById(String aUserId) {
    try {
      return mJdbc.queryForObject(SQL_GAME_LEADERBOARD_GET_BY_ID, new Object[] {"%" + aUserId + "%"}, SINGLE_GAME_LEADERBOARD);
    } catch (Exception e) {
      LOG.error("Error when trying get GameLeaderboard cause : " + e.getMessage());
      return null;
    }
  }
}
