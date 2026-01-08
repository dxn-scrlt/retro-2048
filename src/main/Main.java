package main;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame gameWindow = new JFrame("Retro 2048"); // game window

        // set behavior
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWindow.setResizable(false); // lock size

        // add GamePanel object
        GamePanel gamePanel = new GamePanel();
        gameWindow.add(gamePanel);

        // set size and position
        gameWindow.pack(); // autofit GamePanel object
        gameWindow.setLocationRelativeTo(null); // center screen

        // start game
        gameWindow.setVisible(true); // display JFrame object
    }
}
