package cc.qlyco.backend;

import java.net.Socket;
import java.net.ServerSocket;
import java.util.Scanner;

class Server implements Runnable {
  private boolean running = false;
  private ServerSocket server;
  private ArrayList<Worker> workers;
  private ArrayList<Socket> players;

  public Server(int port) {
    try {
      server = new ServerSocket(port);
      running = true;
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
          players.add(server.accept());
        } catch (Exception err) {
          err.printStackTrace();
        }
      }
    }
  }

  private void monitor() {
    while (running) {
      matchmake();
    }
  }

  private void matchmake() {
    if (players.size() >= 2) {
      workers.add(new Worker(players.remove(0), players.remove(0)));
    }
  }
}
