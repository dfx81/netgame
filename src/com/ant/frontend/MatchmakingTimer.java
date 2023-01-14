package com.ant.frontend;

import javax.swing.JLabel;

class MatchmakingTimer implements Runnable {
    public boolean matchmaking = false;
    public double elapsed = 0;

    private JLabel label;

    public MatchmakingTimer(JLabel label) {
        this.label = label;
    }

    @Override
    public void run() {
        matchmaking = true;
        elapsed = 0;

        double timeThen = System.nanoTime() / Math.pow(10, 9);

        while (matchmaking) {
            double timeNow = System.nanoTime() / Math.pow(10, 9);
            elapsed = timeNow - timeThen;

            label.setText(String.format("Matchmaking time: %.0fs", elapsed));
        }
    }
        
}
