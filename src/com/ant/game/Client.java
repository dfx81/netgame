package com.ant.game;

import java.util.Scanner;

import com.ant.models.GameData;
import com.ant.models.GameState;
import com.ant.models.Move;

import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Client {
  private Scanner input = new Scanner(System.in);
  private Socket socket;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private GameData data;

  private int score1 = 0;
  private int score2 = 0;

  private final int MATCH_POINT = 3;

  public boolean running = true;

  public Client(String ip, int port) {
    try {
      socket = new Socket(ip, port);
    } catch (Exception err) {
      System.out.println("\rERROR: Cannot establish connection to server.");
      running = false;
    }

    System.out.print("Searching opponent.");
    
    try {
      out = new ObjectOutputStream(socket.getOutputStream());
      out.flush();
      in = new ObjectInputStream(socket.getInputStream());

      runGame();
    } catch (Exception err) {
      System.out.println("\rERROR: Communication with the other player is lost.");
    }
  }

  private void runGame() {
    System.out.println("\rOpponent has been found!");

    while (running) {
      receiveGameData();

      if (data.state == GameState.BLUFF || data.state == GameState.OVER)
        System.out.println("\nYOUR SCORE: " + score1 + " | OPPONENT's SCORE: " + score2);

      handleTurn();
    }

    closeConnection();
  }

  private void closeConnection() {
    try {
      out.close();
      in.close();
      socket.close();
    } catch (Exception e) {
      System.out.println("ERROR: Game session ended early because connection with the other player is lost.");
    }
  }

  private void handleTurn() {
    switch (data.state) {
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
    char move = '-';

    while (!(move == 'R' || move == 'P' || move == 'S')) { 
      System.out.print("Declare your move (R = Rock, P = Paper, S = Scissor): ");
      move = input.nextLine().toUpperCase().charAt(0);
    }

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
    char move = '-';

    System.out.println("The opponent intented to use " + data.bluff + ".");

    while (!(move == 'R' || move == 'P' || move == 'S')) {
      System.out.print("What is your move (R = Rock, P = Paper, S = Scissor): ");
      move = input.nextLine().toUpperCase().charAt(0);
    }

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
    data = new GameData(0, null, (isBluff) ? move : null, (!isBluff) ? move : null);
    try {
      out.writeObject(data);
    } catch (Exception e) {
      System.out.println("\nERROR: Cannot communicate with game session.");
      running = false;
    }
  }

  private void receiveGameData() {
    try {
      data = (GameData) in.readObject();
    } catch (Exception e) {
      System.out.println("\nERROR: Cannot communicate with game session.");
      running = false;
    }
  }

  private void check() {
    String message = "";
    
    switch (data.state) {
      case WIN:
        message = "You won the round!";
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
        if (score1 >= MATCH_POINT)
          message = "You've won the game!";
        else if (score2 >= MATCH_POINT)
          message = "Your opponent have won the game!";
        
        running = false;
        break;
      default:
        break;
    }

    System.out.println(message);
  }
}
