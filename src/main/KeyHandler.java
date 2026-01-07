package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import game.Direction;
import game.Grid;
import game.Outcome;

public class KeyHandler implements KeyListener {
    private GamePanel gamePanel;

    public KeyHandler(GamePanel gamePanel) { this.gamePanel = gamePanel; }

    @Override
    public void keyPressed(KeyEvent e) {
        Grid grid = gamePanel.getGrid();

        if (grid.getOutcome() != Outcome.NONE) { // game over
            gamePanel.restart(); // press any key
            return;
        }

        switch(e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                grid.move(Direction.UP); break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                grid.move(Direction.DOWN); break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                grid.move(Direction.LEFT); break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                grid.move(Direction.RIGHT); break;
            }

        gamePanel.repaint(); // repaint after move
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}
