package com.ramusthastudio.reversegame.database;

import com.linecorp.bot.model.profile.UserProfileResponse;
import com.ramusthastudio.reversegame.model.GameLeaderboard;
import com.ramusthastudio.reversegame.model.GameStatus;
import com.ramusthastudio.reversegame.model.GameWord;
import com.ramusthastudio.reversegame.model.UserChat;
import com.ramusthastudio.reversegame.model.UserLine;
import java.util.List;

public interface Dao {
  List<UserLine> getAllUserLine();
  void setUserLine(UserProfileResponse aUser);
  UserLine getUserLineById(String aUserId);

  List<UserChat> getAllUserChat();
  void setUserChat(UserChat aUser);
  void updateUserChat(UserChat aUser);
  UserChat getUserChatById(String aUserId);

  List<GameStatus> getAllGameStatus();
  void setGameStatus(GameStatus aGameStatus);
  void updateGameStatus(GameStatus aGameStatus);
  GameStatus getGameStatusById(String aUserId);

  List<GameWord> getAllGameWord();
  void setGameWord(GameWord aGameWord);
  void updateGameWord(GameWord aGameWord);
  void deleteGameWord(String aUserId);
  GameWord getGameWordById(String aUserId);

  List<GameLeaderboard> getAllGameLeaderboard();
  void setGameLeaderboard(GameLeaderboard aGameLeaderboard);
  void updateGameLeaderboard(GameLeaderboard aGameLeaderboard);
  GameLeaderboard getGameLeaderboardById(String aUserId);
}
