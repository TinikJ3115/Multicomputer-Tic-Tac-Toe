//Jack Tinik 4/20/26
package game;

public class Board {
    private char[][] board; // 3x3 board

    public Board() {
        board = new char[3][3]; // Initialize the board in a 3x3 grid 
        resetBoard();
    }

    // Resets the board to its initial state (empty)
    public void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }
// Attempts to place the player's symbol on the board at the specified location
    public boolean makeMove(int row, int col, char symbol) {
        if (board[row][col] == ' ') {
            board[row][col] = symbol;
            return true;
        }
        return false;
    }

// Returns the current state of the board
    public char[][] getBoard() {
        return board;
    }
}