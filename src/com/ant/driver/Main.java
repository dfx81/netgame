package com.ant.driver;

import java.util.Scanner;

import com.ant.backend.Server;
import com.ant.game.Client;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        if (args.length == 1) {
            new Server(Integer.parseInt(args[0]));
        } else {
            String ip = "127.0.0.1";
            int port = 8080;
            File config = new File("./.config");

            if (config.exists()) {
                try {
                    Scanner reader = new Scanner(config);

                    ip = reader.nextLine();
                    port = reader.nextInt();

                    reader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            new Client(ip, port);
        }

        System.out.println("\nClosing program.");
    }
}
