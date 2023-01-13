package cc.qlyco.game;

import java.util.Scanner;
import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cc.qlyco.models.GameData;
import cc.qlyco.models.GameState;
import cc.qlyco.models.Move;

public class Logic {
  private GameState gameState = null;
  private Scanner input = new Scanner(System.in);
  private Socket socket;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private GameData data;

  private int score1 = 0;
  private int score2 = 0;

  public boolean running;

  public Logic(String ip, int port) {
    try {
      socket = new Socket(ip, port);
      
      System.out.print("Connected to server. Waiting for opponent...");
      
      out = new ObjectOutputStream(socket.getOutputStream());
      out.flush();
      in = new ObjectInputStream(socket.getInputStream());

      runGame();
    } catch (Exception err) {
      err.printStackTrace();
    }
  }

  private void runGame() {
    /*receiveGameData();

    if (data.state == GameState.PLAY) {
      System.out.println("\rOpponent found!");
      running = true;
    }*/

    running = true;

    while (running) {
      System.out.println("\rOpponent found!\n\n");
      System.out.println("YOUR SCORE: " + score1 + " | OPPONENT's SCORE: " + score2);
      receiveGameData();
      handleTurn();
    }
  }

  private void handleTurn() {
    switch (gameState) {
      case BLUFF:
        bluff();
        break;
      case PLAY:
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
    data = new GameData(0, null, (!isBluff) ? move : null, (isBluff) ? move : null);
    try {
      out.writeObject(data);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void receiveGameData() {
    try {
      data = (GameData) in.readObject();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /*private void wait(int mils) {
    try {
      Thread.sleep(mils);
    } catch (Exception err) {
      err.printStackTrace();
    }
  }*/

  private void check() {
    String message = "";
    
    switch (gameState) {
      case WIN:
        message = "You win the round!";
        score1 = data.score;
        break;
      case LOSE:
        message = "You lost the round...";
        score1 = data.score;
        score2++;
        break;
      case DRAW:
        message = "The round ends in a draw.";
        break;
      case OVER:
        if (score1 >= 3)
          message = "You've won the game!";
        else if (score2 >= 3)
          message = "Your opponent have won the game!";
        
        break;
      default:
        break;
    }

    System.out.println(message);
  }
}
