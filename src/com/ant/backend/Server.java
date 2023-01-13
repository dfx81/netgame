package com.ant.backend;

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

      System.out.println("[INFO] Server is now running.");
      workers = new ArrayList<>();
      players = new ArrayList<>();
      
      System.out.println("[INFO] Connected clients: " + players.size());
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
        players.add(server.accept());
        System.out.println("[INFO] Connected clients: " + players.size());
        matchmake();
      } catch (Exception err) {
        err.printStackTrace();
      }
    }
  }

  private void wait(int mils) {
    try {
      Thread.sleep(mils);
    } catch (Exception err) {
      err.printStackTrace();
    }
  }

  private void monitor() {
    while (running) {
      wait(10000);

      boolean removed = false;

      for (int i = workers.size() - 1; i >= 0; i--) {
        if (workers.get(i).running == false) {
          removed = true;
          workers.remove(i);
        }
      }

      if (removed) {
        System.out.println("[INFO] Removed finished sessions.");
        System.out.println("[INFO] Available sessions: " + workers.size());
      }
    }
  }

  private void matchmake() {
    if (players.size() >= 2) {
      workers.add(new Worker(players.remove(0), players.remove(0)));
      System.out.println("[INFO] Available sessions: " + workers.size());
    }
  }
}