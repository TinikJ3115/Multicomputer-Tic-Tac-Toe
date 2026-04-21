//Jack Tinik 4/20/26
package game;

//simple class to check for wins and draws in the game of tic tac toe

public class GameLogic {

    public static boolean checkWin(char[][] b, char s) {

        // Check rows and columns for a win
        for (int i = 0; i < Board.SIZE; i++) {
            if (b[i][0] == s && b[i][1] == s && b[i][2] == s) return true;
            if (b[0][i] == s && b[1][i] == s && b[2][i] == s) return true;
        }

        if (b[0][0] == s && b[1][1] == s && b[2][2] == s) return true;
        if (b[0][2] == s && b[1][1] == s && b[2][0] == s) return true;

        return false;
    }
// Checks if the board is full without any winner, indicating a draw
    public static boolean checkDraw(char[][] b) {
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                if (b[i][j] == ' ') return false;
            }
        }
        return true;
    }

    public static char nextPlayer(char currentPlayer) {
        return currentPlayer == 'X' ? 'O' : 'X';
    }
}
