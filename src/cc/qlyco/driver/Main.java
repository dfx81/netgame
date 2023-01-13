package cc.qlyco.driver;

import cc.qlyco.backend.Server;
import cc.qlyco.game.Logic;

import java.util.Scanner;
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

            Logic gameClient = new Logic(ip, port);

            while (gameClient.running) {
                continue;
            }
        }

        System.out.println("Closing program.");
    }
}
