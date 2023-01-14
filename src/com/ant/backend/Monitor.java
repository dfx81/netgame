package com.ant.backend;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

class Monitor implements Runnable {
    private ServerSocket monitorService;
    public boolean running = false;

    private int port = 80;

    public Monitor(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        running = true;

        try {
            monitorService = new ServerSocket(port);
        } catch (Exception err) {
            System.out.println("[ERROR] Cannot open port for monitoring.");
            System.out.println("[ERROR] Monitoring service won't be available.");
            running = false;
        }

        while (running) {
            try {
                Socket client = monitorService.accept();
                Scanner in = new Scanner(client.getInputStream());
                PrintWriter out = new PrintWriter(client.getOutputStream());

                boolean done = false;

                while (!done) {
                    String req = in.nextLine();
                    if (req.equals("")) {
                        done = true;
                        System.out.println("[MONITOR] HTTP request received.");
                        break;
                    }
                }

                System.out.println("[MONITOR] Sending response.");

                out.write("HTTP/1.0 200 OK\r\n");
                out.write("Content-Type: text/html\r\n");
                out.write("\r\n");
                out.write("<title>Netgame</title>");
                out.write("<p>Server is running.</p>");

                out.flush();
                out.close();
                in.close();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

}
