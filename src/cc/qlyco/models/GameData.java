package cc.qlyco.models;

import java.io.Serializable;

import cc.qlyco.models.GameState;
import cc.qlyco.models.Move;

class GameData implements Serializable {
  public int score = 0;
  public GameState state = null;
  public Move bluff = null;
  public Move move = null;

  public GameData(int score, GameState state, Move bluff, Move move) {
    this.score = score;
    this.state = state;
    this.bluff = bluff;
    this.move = move;
  }
}
