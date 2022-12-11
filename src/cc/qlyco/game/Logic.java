package cc.qlyco.game;

import java.util.Scanner;

import cc.qlyco.models.GameState;
import cc.qlyco.models.Move;

class Logic implements Runnable {
  private GameState gameState = null;
  private Scanner input = new Scanner(System.in);

  public Logic() {
    new Thread(this).start();

    runGame();
  }

  @Override
  public void run() {
    int timeThen = System.nanoTime() / Math.pow(10, 9);

    while (true) {
      int timeNow = System.nanoTime() / Math.pow(10, 9);

      if (timeNow - timeThen > 1 / 60.0) {
        receiveGameData();
        timeThen = timeNow;
      } else {
        wait(100);
      }
    }
  }

  private void runGame() {
    while (true) {
      if (gameState == GameState.WAIT) {
        wait(1000);
        continue;
      }

      handleTurn();
    }
  }

  private void handleTurn() {
    switch (gameState) {
      case GameState.BLUFF:
        bluff();
        break();
      case GameState.PLAY:
        play();
      case GameState.CHECK:
        check();
        break;
      default:
        gameOver();
    }
  }

  private void bluff() {
    System.out.print("Declare your move (R = Rock, P = Paper, S = Scissor): ");

    char move = input.readLine().toUpperCase().charAt(0);

    switch (move) {
      case 'R':
        submitMove(Move.ROCK);
        break;
      case 'P':
        submitMove(Move.PAPER);
        break;
      case 'S':
        submitMove(Move.SCISSOR);
        break;
    }
  }

  private void play() {
    System.out.println("The opponent intented to use " + oppIntent + ".");
    System.out.println("What is your move (R = Rock, P = Paper, S = Scissor)?");

    char move = input.readLine().toUpperCase().charAt(0);

    switch (move) {
      case 'R':
        submitMove(Move.ROCK);
        break;
      case 'P':
        submitMove(Move.PAPER);
        break;
      case 'S':
        submitMove(Move.SCISSOR);
        break;
    }
  }

  private void check() {
    // pass
  }

  private void submitMove(Move move) {

  }

  private void receiveGameData() {

  }

  private void wait(int mils) {
    try {
      Thread.sleep(mils);
    } catch (Exception err) {
      err.printStackTrace();
    }
  }

  private void gameOver() {
    switch (gameState) {
      case GameState.WIN:
        message = "You win!";
        break;
      case GameState.LOSE:
        message = "You lose...";
        break;
    }

    System.out.println(message);
  }
}
