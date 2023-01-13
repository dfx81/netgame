package cc.qlyco.backend;

import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cc.qlyco.models.GameData;
import cc.qlyco.models.GameState;
import cc.qlyco.models.Move;

class Worker implements Runnable {
  private Socket p1;
  private Socket p2;
  private ObjectInputStream[] inputs;
  private ObjectOutputStream[] outputs;
  private GameState state = GameState.WAIT;

  private int score1 = 0;
  private int score2 = 0;
  private GameData data1;
  private GameData data2;

  public boolean running;

  public Worker(Socket p1, Socket p2) {
    this.p1 = p1;
    this.p2 = p2;

    inputs = new ObjectInputStream[]{
      new ObjectInputStream(p1.getInputStream()),
      new ObjectInputStream(p2.getInputStream())
    };
    outputs = new ObjectOutputStream[]{
      new ObjectOutputStream(p1.getOutputStream()),
      new ObjectOutputStream(p2.getOutputStream())
    };

    running = true;

    new Thread(this).start();
  }

  @Override
  public void run() {
    while (running) {
      state = GameState.BLUFF;

      data1 = new GameData(score1, state, null, null);
      data2 = new GameData(score2, state, null, null);

      writeObjects();
      readObjects();

      state = GameState.PLAY;

      Move bluff1 = data1.bluff;
      Move bluff2 = data2.bluff;

      writeObjects();
      readObjects();

      int winner = check(data1.move, data2.move);

      GameState state1;
      GameState state2;

      switch (winner) {
        case 1:
          score1++;
          state1 = GameState.WIN;
          state2 = GameState.LOSE;
          break;
        case 2:
          score2++;
          state2 = GameState.WIN;
          state1 = GameState.LOSE;
          break;
        default:
          state1 = state2 = GameState.DRAW;
      }

      data1 = new GameData(score1, state1, null, null);
      data2 = new GameData(score2, state2, null, null);

      writeObjects();

      if (score1 >= 3 || score2 >= 3) {
        running = false;
      }
    }

    data1.state = data2.state = GameState.OVER;
    writeObjects();

    inputs[0].close();
    inputs[1].close();
    outputs[0].close();
    outputs[1].close();
  }

  private void writeObjects() {
    outputs[0].writeObject(data1);
    outputs[1].writeObject(data2);

    outputs[0].flush();
    outputs[1].flush();
  }

  private void readObjects() {
    data1 = (GameData) inputs[0].readObject();
    data2 = (GameData) inputs[1].readObject();
  }

  private void check(Move m1, Move m2) {
    if (m1 == m2) {
      return 0;
    } else if ((m1 == Move.ROCK && m2 == Move.SCISSOR) || (m1 == Move.SCISSOR && m2 == Move.PAPER) || (m1 == Move.PAPER && m2 == Move.ROCK)) {
      return 1;
    } else {
      return 2;
    }
  }
}
