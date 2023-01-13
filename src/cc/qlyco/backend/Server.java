package cc.qlyco.backend;

import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server implements Runnable {
  private boolean running = false;
  private ServerSocket server;
  private ArrayList<Worker> workers;
  private ArrayList<Socket> players;

  public Server(int port) {
    try {
      server = new ServerSocket(port);
      running = true;

      System.out.println("Server is now running.\n");
      workers = new ArrayList<>();
      players = new ArrayList<>();
    } catch (Exception err) {
      err.printStackTrace();
    }

    new Thread(this).start();
    monitor();
  }

  @Override
  public void run() {
    while (running) {
      try {
        System.out.println("\nConnected clients: " + players.size());
        players.add(server.accept());
        matchmake();
      } catch (Exception err) {
        err.printStackTrace();
      }
    }
  }

  private void monitor() {
    while (running) {
      for (int i = workers.size() - 1; i >= 0; i--) {
        if (workers.get(i).running == false) {
          System.out.println("\nRemoving ended session.\n");
          workers.remove(i);
        }
      }
    }
  }

  private void matchmake() {
    if (players.size() >= 2) {
      workers.add(new Worker(players.remove(0), players.remove(0)));
    }
  }
}