package com.ant.backend;

import java.net.Socket;

import com.ant.models.GameData;
import com.ant.models.GameState;
import com.ant.models.Move;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

class Worker implements Runnable {
  private ObjectInputStream[] inputs;
  private ObjectOutputStream[] outputs;
  private GameState state = GameState.START;

  private int score1 = 0;
  private int score2 = 0;
  private GameData data1;
  private GameData data2;

  private Socket p1;
  private Socket p2;

  public boolean running = true;

  private int errors = 0;

  private final int MATCH_POINT = 3;

  public Worker(Socket p1, Socket p2) {
    this.p1 = p1;
    this.p2 = p2;

    try {
      outputs = new ObjectOutputStream[]{
        new ObjectOutputStream(p1.getOutputStream()),
        new ObjectOutputStream(p2.getOutputStream())
      };

      outputs[0].flush();
      outputs[1].flush();
    } catch (Exception e) {
      System.out.println("[ERROR] Cannot establish connection with players.");
      errors++;
      running = false;
    }

    try {
      inputs = new ObjectInputStream[]{
        new ObjectInputStream(p1.getInputStream()),
        new ObjectInputStream(p2.getInputStream())
      };
    } catch (Exception e) {
      System.out.println("[ERROR] Cannot establish connection with players.");
      errors++;
      running = false;
    }

    new Thread(this).start();
  }

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
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

      data1 = new GameData(score1, state, bluff2, null);
      data2 = new GameData(score2, state, bluff1, null);

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

      if (score1 >= MATCH_POINT || score2 >= MATCH_POINT) {
        state = GameState.OVER;
        running = false;
      }
    }

    data1 = new GameData(score1, state, null, null);
    data2 = new GameData(score2, state, null, null);

    writeObjects();

    closeConnection();
  }

  private void closeConnection() {
    try {
      outputs[0].close();
      inputs[0].close();
      p1.close();
    } catch (Exception e) {
      System.out.println("[ERROR] Encountered problem when closing connection.");
    }

    try {
      outputs[1].close();
      inputs[1].close();
      p2.close();
    } catch (Exception e) {
      System.out.println("[ERROR] Encountered problem when closing connection.");
    }
  }

  private void writeObjects() {
    if (errors > 0)
      return;

    try {
      outputs[0].writeObject(data1);
      outputs[0].flush();
      outputs[1].writeObject(data2);
      outputs[1].flush();
    } catch (Exception e) {
      System.out.println("[ERROR] Cannot communicate with players.");
      errors++;
      running = false;
    }
  }

  private void readObjects() {
    if (errors > 0)
      return;
    
    try {
      data1 = (GameData) inputs[0].readObject();
      data2 = (GameData) inputs[1].readObject();
    } catch (Exception e) {
      System.out.println("[ERROR] Cannot communicate with players.");
      errors++;
      running = false;
    }
  }

  private int check(Move m1, Move m2) {
    if (m1 == m2) {
      return 0;
    } else if ((m1 == Move.ROCK && m2 == Move.SCISSOR) || (m1 == Move.SCISSOR && m2 == Move.PAPER) || (m1 == Move.PAPER && m2 == Move.ROCK)) {
      return 1;
    } else {
      return 2;
    }
  }
}
