//Jack Tinik 4/20/26
package server;


import java.net.Socket;
// The GameServer class manages the overall game state and player connections for the Tic Tac Toe server application
public class GameServer {

    private ClientHandler playerX; // Handler for player X
    private ClientHandler playerO;// Handler for player O

    // Adds a new player to the game and starts a new thread to handle communication with that player
    public void addPlayer(Socket socket, char symbol) {
        ClientHandler handler = new ClientHandler(socket, symbol); // Create a new ClientHandler for the connected player
        Thread t = new Thread(handler);// Start a new thread to handle communication with the player
        t.start(); // Start the client handler thread

        if (symbol == 'X') { // Assign the handler to playerX if the symbol is 'X'
            playerX = handler;
        } else {
            playerO = handler;
        }
    }

    // Starts the game by sending a "GAME_START" message to both players
    public void startGame() {
        System.out.println("Game ready to start!");
        playerX.sendMessage("GAME_START");
        playerO.sendMessage("GAME_START");
    }
}