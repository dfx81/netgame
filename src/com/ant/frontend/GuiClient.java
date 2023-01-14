package com.ant.frontend;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultCaret;

import com.ant.models.GameData;
import com.ant.models.GameState;
import com.ant.models.Move;

public class GuiClient implements Runnable {
    // GUI stuff
    private JFrame frame;
    private JPanel menu;
    private JPanel loading;
    private JPanel game;

    private JLabel loadingText;
    private JLabel waitTime;

    private JLabel scoreText;
    private JTextArea log;
    private JPanel buttonGroup;

    private JButton backButton;

    private MatchmakingTimer timer;

    // Connection stuff
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String ip;
    private int port;

    // Game stuff
    private int score1 = 0;
    private int score2 = 0;

    private GameData data;

    private final int MATCH_POINT = 3;

    public boolean running = true;

    private String gameLog = "";

    public GuiClient(String ip, int port) {
        this.ip = ip;
        this.port = port;

        constructGui();
    }

    private void constructGui() {
        // MAIN MENU
        menu = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Bluff RPS", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 64));
        menu.add(title, BorderLayout.PAGE_START);
        JLabel help = new JLabel(
                "<html>How to play:<br/>&emsp;&emsp;Declare your move before playing Rock Paper Scissors in order<br/>&emsp;&emsp;to throw off your opponent.</html>",
                SwingConstants.CENTER);
        menu.add(help, BorderLayout.CENTER);

        JButton playButton = new JButton("MATCHMAKE");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                frame.setContentPane(loading);
                frame.revalidate();
                connect();
            }
        });
        menu.add(playButton, BorderLayout.PAGE_END);

        // LOADING MENU
        loading = new JPanel(new BorderLayout());
        loadingText = new JLabel("Searching for opponent...", SwingConstants.CENTER);
        loading.add(loadingText, BorderLayout.CENTER);
        waitTime = new JLabel("Matchmaking time: 0s", SwingConstants.CENTER);
        loading.add(waitTime, BorderLayout.PAGE_END);
        timer = new MatchmakingTimer(waitTime);

        // GAME MENU
        game = new JPanel(new BorderLayout());
        scoreText = new JLabel("Your Score: 0 | Opponent's Score: 0");
        log = new JTextArea(gameLog);
        log.setEditable(false);
        ((DefaultCaret) log.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        buttonGroup = new JPanel();
        JButton rock = new JButton("ROCK");
        rock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                gameLog += "You've selected ROCK.\n";
                updateLog();
                buttonGroup.setVisible(false);

                if (data.state == GameState.BLUFF)
                    submitMove(Move.ROCK, true);
                else
                    submitMove(Move.ROCK, false);
            }
        });
        JButton paper = new JButton("PAPER");
        paper.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                gameLog += "You've selected PAPER.\n";
                updateLog();
                buttonGroup.setVisible(false);

                if (data.state == GameState.BLUFF)
                    submitMove(Move.PAPER, true);
                else
                    submitMove(Move.PAPER, false);
            }
        });
        JButton scissor = new JButton("SCISSORS");
        scissor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                gameLog += "You've selected SCISSORS.\n";
                updateLog();
                buttonGroup.setVisible(false);

                if (data.state == GameState.BLUFF)
                    submitMove(Move.SCISSOR, true);
                else
                    submitMove(Move.SCISSOR, false);
            }
        });
        backButton = new JButton("Return to main menu");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                frame.setContentPane(menu);
                frame.revalidate();
                game.add(buttonGroup, BorderLayout.PAGE_END);
            }
        });
        buttonGroup.add(rock);
        buttonGroup.add(paper);
        buttonGroup.add(scissor);
        game.add(scoreText, BorderLayout.PAGE_START);
        game.add(new JScrollPane(log), BorderLayout.CENTER);
        game.add(buttonGroup, BorderLayout.PAGE_END);

        // MAIN WINDOW
        frame = new JFrame();
        frame.setResizable(false);
        frame.setSize(640, 480);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowActivated(WindowEvent evt) {
                return;
            }

            @Override
            public void windowClosed(WindowEvent evt) {
                return;
            }

            @Override
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }

            @Override
            public void windowDeactivated(WindowEvent evt) {
                return;
            }

            @Override
            public void windowDeiconified(WindowEvent evt) {
                return;
            }

            @Override
            public void windowIconified(WindowEvent evt) {
                return;
            }

            @Override
            public void windowOpened(WindowEvent evt) {
                return;
            }

        });
        frame.setContentPane(menu);
        frame.revalidate();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void connect() {
        new Thread(this).start();
    }

    private void matchmake() {
        running = true;

        try {
            socket = new Socket(ip, port);
        } catch (Exception err) {
            showError("Cannot connect to the server.");
            frame.setContentPane(menu);
            frame.revalidate();
            return;
        }

        new Thread(timer).start();

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            runGame();
        } catch (Exception err) {
            showError("Communication with the other player is lost.");
            closeConnection();
            frame.setContentPane(menu);
            frame.revalidate();
            return;
        }
    }

    private void runGame() {
        timer.matchmaking = false;
        frame.setContentPane(game);
        frame.revalidate();
        gameLog = "Opponent has been found!\n\n";
        updateLog();

        running = true;
    }

    private void closeConnection() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            showError("Game session ended early because connection with the other player is lost.");
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
        gameLog += "BLUFF PHASE: Declare your move\n";
        updateLog();
    }

    private void play() {
        gameLog += "PLAY PHASE: The opponent intented to use " + data.bluff + ".\n";
        gameLog += "What is your move?\n";
        updateLog();
    }

    private void submitMove(Move curMove, boolean isBluff) {
        data = new GameData(0, null, (isBluff) ? curMove : null, (!isBluff) ? curMove : null);
        try {
            out.writeObject(data);
        } catch (Exception e) {
            showError("Cannot communicate with game session.\nEither any of the players went offline or the server is offline.");
            closeConnection();
            frame.setContentPane(menu);
            frame.revalidate();
            running = false;
        }
    }

    private void receiveGameData() {
        try {
            data = (GameData) in.readObject();
        } catch (Exception e) {
            showError("Cannot communicate with game session.\nEither any of the players went offline or the server is offline.");
            closeConnection();
            frame.setContentPane(menu);
            frame.revalidate();
            running = false;
        }
    }

    private void check() {
        switch (data.state) {
            case WIN:
                gameLog += "You won the round!\n\n";
                score1 = data.score;
                break;
            case LOSE:
                gameLog += "You lost the round...\n\n";
                score1 = data.score;
                score2++;
                break;
            case DRAW:
                gameLog += "The round ends in a draw.\n\n";
                break;
            case OVER:
                if (score1 >= MATCH_POINT)
                    gameLog += "\nYou've won the game!";
                else if (score2 >= MATCH_POINT)
                    gameLog += "\nYour opponent have won the game!";

                
                game.add(backButton, BorderLayout.PAGE_END);
                running = false;
                closeConnection();
                break;
            default:
                break;
        }

        updateLog();
    }

    private void updateLog() {
        log.setText(gameLog);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame,
                message,
                "ERROR",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void run() {
        matchmake();

        while (running) {
            receiveGameData();

            if (data.state == GameState.BLUFF || data.state == GameState.OVER) {
                scoreText.setText("Your Score: " + score1 + " | Opponent's Score: " + score2);
            }

            if (data.state == GameState.BLUFF || data.state == GameState.PLAY)
                buttonGroup.setVisible(true);

            handleTurn();
        }
    }
}
