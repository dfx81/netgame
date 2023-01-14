package com.ant.driver;

import java.util.Scanner;

import com.ant.backend.Server;
import com.ant.frontend.Client;
import com.ant.frontend.GuiClient;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        if (args.length == 2) {
            new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        } else {
            String ip = "127.0.0.1";
            int port = 8080;
            boolean gui = false;

            File config = new File("./.config");

            if (config.exists()) {
                try {
                    Scanner reader = new Scanner(config);

                    ip = reader.nextLine();
                    port = reader.nextInt();
                    gui = reader.nextBoolean();

                    reader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            if (gui)
                new GuiClient(ip, port);
            else
                new Client(ip, port);
        }

        System.out.println("\nClosing program.");
    }
}
