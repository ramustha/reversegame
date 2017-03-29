package com.ramusthastudio.reversegame.database;

import com.linecorp.bot.model.profile.UserProfileResponse;
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

  @Override public void updateUserChat(UserChat aUser) {
    mJdbc.update(SQL_UPDATE_USER_CHAT,
        aUser.getLastChat(),
        new Timestamp(aUser.getLastTime()),
        aUser.getFalseCount(),
        aUser.getUserId()
    );
  }

  @Override public List<UserLine> getAllUserLine() {
    return mJdbc.query(SQL_SELECT_ALL_USER_LINE, MULTIPLE_USER_LINE);
  }

  @Override public List<UserChat> getAllUserChat() {
    return mJdbc.query(SQL_SELECT_ALL_USER_CHAT, MULTIPLE_USER_CHAT);
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
}
