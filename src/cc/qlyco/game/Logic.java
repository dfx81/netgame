package cc.qlyco.game;

import java.util.Scanner;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cc.qlyco.models.GameData;
import cc.qlyco.models.GameState;
import cc.qlyco.models.Move;

class Logic {
  private GameState gameState = null;
  private Scanner input = new Scanner(System.in);
  private Socket socket;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private GameData data;

  private int score1 = 0;
  private int score2 = 0;

  private boolean running;

  public Logic(String ip, int port) {
    try {
      socket = new Socket(ip, port);
      out = new ObjectOutputStream(socket.getOutputStream());
      in = new ObjectInputStream(socket.getInputStream());
    } catch (Exception err) {
      err.printStackTrace();
    }

    runGame();
  }

  private void runGame() {
    running = true;

    while (running) {
      System.out.println("YOUR SCORE: " + score1 + " | OPPONENT's SCORE: " + score2);
      receiveGameData();
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
        break;
      default:
        check();
    }
  }

  private void bluff() {
    System.out.print("Declare your move (R = Rock, P = Paper, S = Scissor): ");

    char move = input.nextLine().toUpperCase().charAt(0);

    switch (move) {
      case 'R':
        submitMove(Move.ROCK, true);
        break;
      case 'P':
        submitMove(Move.PAPER, true);
        break;
      case 'S':
        submitMove(Move.SCISSOR, true);
        break;
    }
  }

  private void play() {
    System.out.println("The opponent intented to use " + data.bluff + ".");
    System.out.println("What is your move (R = Rock, P = Paper, S = Scissor)?");

    char move = input.nextLine().toUpperCase().charAt(0);

    switch (move) {
      case 'R':
        submitMove(Move.ROCK, false);
        break;
      case 'P':
        submitMove(Move.PAPER, false);
        break;
      case 'S':
        submitMove(Move.SCISSOR, false);
        break;
    }
  }

  private void submitMove(Move move, boolean isBluff) {
    data = new GameData(score, null, (!isBluff) ? move : null, (isBluff) ? move : null);
    out.writeObject(data);
  }

  private void receiveGameData() {
    data = (GameData) in.readObject();
  }

  private void wait(int mils) {
    try {
      Thread.sleep(mils);
    } catch (Exception err) {
      err.printStackTrace();
    }
  }

  private void check() {
    switch (gameState) {
      case GameState.WIN:
        message = "You win the round!";
        score1 = data.score;
        break;
      case GameState.LOSE:
        message = "You lost the round...";
        score1 = data.score;
        score2++;
        break;
      case GameState.DRAW:
        message = "The round ends in a draw.";
        break;
    }

    System.out.println(message);
  }
}
