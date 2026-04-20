//Jack Tinik 4/20/26
package game;

// Represents a player in the Tic Tac Toe game, holding their name and symbol (X or O)
public class Player {
    private String name;
    private char symbol;


// Constructor to initialize the player's name and symbol
    public Player(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public char getSymbol() {
        return symbol;
    }
}