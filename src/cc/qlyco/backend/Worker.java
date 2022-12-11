package cc.qlyco.backend;

import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

class Worker {
  private Socket connection;

  public Worker(Socket socket) {
    connection = socket;
  }
}
