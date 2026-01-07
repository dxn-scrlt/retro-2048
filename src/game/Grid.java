package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Grid {
    private int[][] board;
    public static final int BOARD_SIZE = 4;

    // game state trackers
    private static int bestScore;
    private int score;
    private Outcome outcome;

    private Random random = new Random(); // RNG

    public Grid() {
        board = new int[BOARD_SIZE][BOARD_SIZE]; // 4 x 4
        score = 0;
        outcome = Outcome.NONE;

        // initialize with 2 tiles
        spawnRandomTile();
        spawnRandomTile();
    }

    public int getTile(int row, int column) { return board[row][column]; }
    public int getScore() { return score; }
    public static int getBestScore() { return bestScore; }
    public Outcome getOutcome() { return outcome; }

    public void move(Direction direction) {
        if (outcome != Outcome.NONE) return; // game finished

        int[][] previousBoard = copyBoard(); // unchanged board copy for comparing

        for (int index = 0; index < BOARD_SIZE; index++) { // merge per line
            int[] extractedLine = extractLine(index, direction);
            int[] mergedLine = mergeLine(extractedLine);
            writeLine(index, direction, mergedLine);
        }
        
        if (!isEqual(previousBoard, board)) { // moved
            spawnRandomTile();
            gameStatus();
        }
    }

    private void setScore(int result) { score += result; }
    private void setBestScore() { bestScore = Math.max(bestScore, score); }

    private void gameStatus() {
        if (isGameWin()) outcome = Outcome.WIN; // 2048 reached
        else if (isGameOver()) outcome = Outcome.LOSE; // full and unmergeable
        else outcome = Outcome.NONE; // continue
    }

    private boolean isGameWin() {
        for (int[] row : board) for (int tile : row) if (tile == 2048) return true; // winning tile value
        return false;
    }
    private boolean isGameOver() { return isFull() && !canMerge(); }

    private boolean isFull() {
        for (int[] row : board) for (int cell : row) if (cell == 0) return false; // empty cell
        return true;
    }

    private boolean canMerge() { return canHorizontalMerge() || canVerticalMerge(); }
    private boolean canHorizontalMerge() {
        for (int row = 0; row < BOARD_SIZE; row++) for (int column = 0; column < BOARD_SIZE - 1; column++) {
            int current = board[row][column], next = board[row][column + 1];
            if (current != 0 && current == next) return true;
        }

        return false;
    }
    private boolean canVerticalMerge() {
        for (int column = 0; column < BOARD_SIZE; column++) for (int row = 0; row < BOARD_SIZE - 1; row++) {
            int current = board[row][column], next = board[row + 1][column];
            if (current != 0 && current == next) return true;
        }

        return false;
    }

    private void spawnRandomTile() {
        int[] cell = getRandomEmptyCell();
        if (cell == null) return; // no empty cells for spawning

        int row = cell[0], column = cell[1]; // {row, column}
        board[row][column] = getRandomValue();
    }

    private List<int[]> getEmptyCells() {
        if (isFull()) return Collections.emptyList();

        List<int[]> emptyCells = new ArrayList<int[]>();
        for (int row = 0; row < BOARD_SIZE; row++) for (int column = 0; column < BOARD_SIZE; column++) if (board[row][column] == 0) emptyCells.add(new int[]{row, column});

        return emptyCells;
    }

    private int[] getRandomEmptyCell() {
        List<int[]> emptyCells = getEmptyCells();
        if (emptyCells.isEmpty()) return null;

        return emptyCells.get(random.nextInt(emptyCells.size()));
    }

    private int getRandomValue() { return random.nextDouble() < 0.9 ? 2 : 4; } // 90% 2, 10% 4

    private int[] extractLine(int index, Direction direction) { // extract line to merge tiles
        int[] line = new int[BOARD_SIZE];
        switch (direction) {
            case UP:
                for (int i = 0; i < BOARD_SIZE; i++) line[i] = board[i][index]; break;
            case DOWN:
                for (int i = 0; i < BOARD_SIZE; i++) line[i] = board[BOARD_SIZE - 1 - i][index]; break;
            case LEFT:
                for (int i = 0; i < BOARD_SIZE; i++) line[i] = board[index][i]; break;
            case RIGHT:
                for (int i = 0; i < BOARD_SIZE; i++) line[i] = board[index][BOARD_SIZE - 1 - i]; break;
        }

        return line;
    }

    private int[] mergeLine(int[] line) {
        List<Integer> tiles = new ArrayList<Integer>();
        for (int tile : line) if (tile != 0) tiles.add(tile);

        List<Integer> merged = new ArrayList<Integer>();
        for (int i = 0; i < tiles.size(); i++) {
            int current = tiles.get(i);

            if (i + 1 < tiles.size()) { // next tile exists
                int next = tiles.get(i + 1);

                if (current == next) { // merge if similar
                    int mergedValue = current + next;
                    merged.add(mergedValue);
                    setScore(mergedValue); // add merged value to score
                    setBestScore();
                    i++; // skip next
                    continue;
                }
            }

            merged.add(current);
        }

        int[] mergedLine = new int[BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) mergedLine[i] = i < merged.size() ? merged.get(i) : 0;

        return mergedLine;
    }

    private void writeLine(int index, Direction direction, int[] line) { // overwrite line after merge
        switch (direction) {
            case UP:
                for (int i = 0; i < BOARD_SIZE; i++) board[i][index] = line[i]; break;
            case DOWN:
                for (int i = 0; i < BOARD_SIZE; i++) board[BOARD_SIZE - 1 - i][index] = line[i]; break;
            case LEFT:
                for (int i = 0; i < BOARD_SIZE; i++) board[index][i] = line[i]; break;
            case RIGHT:
                for (int i = 0; i < BOARD_SIZE; i++) board[index][BOARD_SIZE - 1 - i] = line[i]; break;
        }
    }

    private boolean isEqual(int[][] board1, int[][] board2) {
        for (int row = 0; row < BOARD_SIZE; row++) for (int column = 0; column < BOARD_SIZE; column++) if (board1[row][column] != board2[row][column]) return false; // different tiles
        return true;
    }
    
    private int[][] copyBoard() {
        int[][] copy = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) System.arraycopy(board[i], 0, copy[i], 0, BOARD_SIZE); // copy row by row

        return copy;
    }
}
