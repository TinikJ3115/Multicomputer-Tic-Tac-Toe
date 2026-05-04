//Jack Tinik 4/20/26
package game;

// Represents the game board for Tic Tac Toe
//tracks the state of the board and allows players to make moves

public class Board {
    public static final int SIZE = 3;

    private final char[][] board; // 3x3 board

    public Board() {
        board = new char[SIZE][SIZE]; // Initialize the board in a 3x3 grid
        resetBoard();
    }

    // Resets the board to its initial state (empty)
    public void resetBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = ' ';
            }
        }
    }

    // Attempts to place the player's symbol on the board at the specified location
    public boolean makeMove(int row, int col, char symbol) {
        if (!isInBounds(row, col) || board[row][col] != ' ') {
            return false;
        }

        board[row][col] = symbol;
        return true;
    }

    public boolean isCellEmpty(int row, int col) {
        return isInBounds(row, col) && board[row][col] == ' ';
    }

    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    public char getCell(int row, int col) {
        if (!isInBounds(row, col)) {
            throw new IllegalArgumentException("Cell is out of bounds");
        }
        return board[row][col];
    }

    public void setCell(int row, int col, char value) {
        if (!isInBounds(row, col)) {
            throw new IllegalArgumentException("Cell is out of bounds");
        }
        board[row][col] = value;
    }

    public void loadBoard(char[][] source) {
        if (source == null || source.length != SIZE) {
            throw new IllegalArgumentException("Board snapshot must be 3x3.");
        }

        for (int row = 0; row < SIZE; row++) {
            if (source[row] == null || source[row].length != SIZE) {
                throw new IllegalArgumentException("Board snapshot must be 3x3.");
            }
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = source[row][col];
            }
        }
    }

    // Returns a defensive copy so UI/network code cannot mutate the board directly.
    public char[][] getBoard() {
        char[][] copy = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, SIZE);
        }
        return copy;
    }
}
