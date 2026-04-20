package game;

public class TestGame {
        public static void main(String[] args) {
        Board b = new Board();

        b.makeMove(0, 0, 'X');
        b.makeMove(0, 1, 'X');
        b.makeMove(0, 2, 'X');

        System.out.println(GameLogic.checkWin(b.getBoard(), 'X')); // should print true
    }
}
