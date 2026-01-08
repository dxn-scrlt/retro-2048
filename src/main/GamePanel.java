package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.InputStream;
import javax.swing.JPanel;

import game.Grid;
import game.Outcome;

public class GamePanel extends JPanel {
    private final int tileSize = 100; // 100 x 100
    
    // set screen
    private final int screenWidth = 600, screenHeight = 450; // 4 : 3 aspect ratio
    private final int borderWidth = tileSize / 4; // inner border width
    
    private Font gameFont; // pixelated font
    private KeyHandler keyHandler = new KeyHandler(this); // key input handler
    
    // set grid
    private Grid grid;
    private final int tileMargin = tileSize / 20; // spacing between tiles
    private final int gridDimension = tileSize * Grid.BOARD_SIZE + tileMargin * (Grid.BOARD_SIZE + 1);
    private final int gridX = borderWidth, gridY = (screenHeight - gridDimension) / 2 + 1;

    // set side panel
    private final int sidePanelBoxWidth = tileSize, sidePanelBoxHeight = tileSize * (Grid.BOARD_SIZE - 2);
    private final int scoreBoxX = gridDimension + borderWidth * 2, scoreBoxY = gridY;
    private final int statusBoxX = scoreBoxX, statusBoxY = scoreBoxY + sidePanelBoxHeight + borderWidth;

    public GamePanel() {
        grid = new Grid(); // start new game

        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);

        loadGameFont();
        addKeyListener(keyHandler);
    }

    public Grid getGrid() { return grid; }

    public void restart() {
        grid = new Grid();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g; // cast into Graphics2D object for drawing
        drawBorder(g2);
        drawTiles(g2);
        drawScoreBox(g2);
        drawStatusBox(g2);
    }

    private void drawBorder(Graphics2D g2) {
        g2.setColor(new Color(168, 168, 168)); // metallic border

        // outer border
        int outerBorderTopWidth = (screenHeight - gridDimension) / 2 + 1;
        g2.fillRect(0, 0, screenWidth, outerBorderTopWidth);

        int outerBorderBottomWidth = screenHeight - gridDimension - outerBorderTopWidth;
        g2.fillRect(0, screenHeight - outerBorderBottomWidth, screenWidth, outerBorderBottomWidth);

        int outerBorderLeftWidth = borderWidth;
        g2.fillRect(0, 0, outerBorderLeftWidth, screenHeight);

        int outerBorderRightWidth = borderWidth;
        g2.fillRect(screenWidth - outerBorderRightWidth, 0, outerBorderRightWidth, screenHeight);

        // inner borders
        g2.fillRect(outerBorderLeftWidth + gridDimension, outerBorderTopWidth, borderWidth, gridDimension); // grid and side panel border
        g2.fillRect(gridDimension + outerBorderLeftWidth * 2, outerBorderTopWidth + sidePanelBoxHeight, sidePanelBoxWidth, borderWidth); // side panel boxes border
    }
    
    private void drawTiles(Graphics2D g2) {
        int startX = gridX + tileMargin, startY = gridY + tileMargin;

        for (int row = 0; row < Grid.BOARD_SIZE; row++) for (int column = 0; column < Grid.BOARD_SIZE; column++) {
            int tile = grid.getTile(row, column);
            int tileX = startX + column * (tileSize + tileMargin), tileY = startY + row * (tileSize + tileMargin);
            g2.setColor(TileColor.forValue(tile)); // color based on tile value

            g2.fillRect(tileX, tileY, tileSize, tileSize);

            if (tile != 0) { // draw nonzero numbers
                g2.setColor(Color.BLACK); // font color
                int fontSize = tile < 1000 ? tileSize / 4 : tileSize / 5; // reduce font size after 3 digits
                g2.setFont(gameFont.deriveFont((float) fontSize));

                String number = String.valueOf(tile);
                int numberWidth = g2.getFontMetrics().stringWidth(number), numberHeight = g2.getFontMetrics().getAscent();
                int numberX = tileX + (tileSize - numberWidth) / 2 + 1, numberY = tileY + (tileSize + numberHeight) / 2;

                g2.drawString(number, numberX, numberY);
            }
        }
    }

    private void drawScoreBox(Graphics2D g2) {
        // draw border
        g2.setColor(Color.CYAN); // border outline color
        g2.setStroke(new BasicStroke(4));
        g2.drawRect(scoreBoxX, scoreBoxY, sidePanelBoxWidth, sidePanelBoxHeight);

        // draw text
        g2.setColor(Color.WHITE); // font color
        int fontSize = tileSize / 6;
        g2.setFont(gameFont.deriveFont((float) fontSize));
        int scoreBoxPadding = tileMargin * 2;

        // current score
        String currentScoreText = "SCORE";
        String currentScoreValue = String.valueOf(grid.getScore());
        int currentScoreTextX = scoreBoxX + scoreBoxPadding, currentScoreTextY = scoreBoxY + g2.getFontMetrics().getAscent() + scoreBoxPadding;
        int currentScoreValueX = currentScoreTextX, currentScoreValueY = currentScoreTextY + g2.getFontMetrics().getHeight() + scoreBoxPadding;

        g2.drawString(currentScoreText, currentScoreTextX, currentScoreTextY);
        g2.drawString(currentScoreValue, currentScoreValueX, currentScoreValueY);

        // best score
        String bestScoreText = "BEST";
        String bestScoreValue = String.valueOf(Grid.getBestScore());
        int bestScoreTextX = scoreBoxX + scoreBoxPadding, bestScoreTextY = scoreBoxY + sidePanelBoxHeight / 2 + g2.getFontMetrics().getAscent() + scoreBoxPadding;
        int bestScoreValueX = bestScoreTextX, bestScoreValueY = bestScoreTextY + g2.getFontMetrics().getHeight() + scoreBoxPadding;

        g2.drawString(bestScoreText, bestScoreTextX, bestScoreTextY);
        g2.drawString(bestScoreValue, bestScoreValueX, bestScoreValueY);
    }

    private void drawStatusBox(Graphics2D g2) {
        // draw border
        g2.setColor(Color.CYAN); // border outline color
        g2.setStroke(new BasicStroke(4));
        g2.drawRect(statusBoxX, statusBoxY, sidePanelBoxWidth, sidePanelBoxHeight);

        // draw text
        g2.setColor(Color.WHITE); // font color
        int fontSize = tileSize / 6;
        g2.setFont(gameFont.deriveFont((float) fontSize));
        int statusBoxPadding = tileMargin * 2;

        String statusMessage1, statusMessage2;

        switch (grid.getOutcome()) {
            case Outcome.WIN:
                statusMessage1 = "YOU"; statusMessage2 = "WIN"; break;
            case Outcome.LOSE:
                statusMessage1 = "GAME"; statusMessage2 = "OVER"; break;
            default:
                statusMessage1 = "RETRO"; statusMessage2 = "2048"; break;
        }

        int statusMessage1X = statusBoxX + statusBoxPadding, statusMessage1Y = statusBoxY + g2.getFontMetrics().getAscent() + statusBoxPadding;
        int statusMessage2X = statusMessage1X, statusMessage2Y = statusMessage1Y + g2.getFontMetrics().getHeight() + statusBoxPadding;

        g2.drawString(statusMessage1, statusMessage1X, statusMessage1Y);
        g2.drawString(statusMessage2, statusMessage2X, statusMessage2Y);

        if (grid.getOutcome() != Outcome.NONE) { // restart game prompt
            String[] promptMessages = {"PRESS", "ANY", "KEY", "TO", "PLAY", "AGAIN"};
            int promptMessageX = statusMessage2X, promptMessageY = statusMessage2Y + g2.getFontMetrics().getHeight() + statusBoxPadding;

            for (String promptMessage : promptMessages) {
                g2.drawString(promptMessage, promptMessageX, promptMessageY);
                promptMessageY += g2.getFontMetrics().getHeight();
            }
        }
    }

    private void loadGameFont() { try (InputStream is = getClass().getResourceAsStream("/fonts/PressStart2P.ttf")) { gameFont = Font.createFont(Font.TRUETYPE_FONT, is); } catch (Exception e) { e.printStackTrace(); } }
}
