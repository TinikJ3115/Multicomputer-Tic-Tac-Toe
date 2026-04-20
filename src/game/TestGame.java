package game;

// Jack Tinik 4/20/26
// A simple test class to verify the functionality of the Board and GameLogic classes 
// KAMRAN this should return "true" when you run it.

public class TestGame {
        public static void main(String[] args) {
        Board b = new Board();

        b.makeMove(0, 0, 'X');
        b.makeMove(0, 1, 'X');
        b.makeMove(0, 2, 'X');

        System.out.println(GameLogic.checkWin(b.getBoard(), 'X')); // should print true
    }
}
