//Jack Tinik 4/20/26
package server;

import java.net.ServerSocket;
import java.net.Socket;

// The ServerMain class is the entry point for the Tic Tac Toe server application, 
// responsible for accepting player connections and starting the game
public class ServerMain {

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(5000); // Create a server socket to listen for incoming connections on port 5000
        GameServer gameServer = new GameServer();

        // Log messages to indicate the server status and player connections
        System.out.println("Server started on port 5000");
        System.out.println("Waiting for players...");

        // Accept connections from two players and assign them symbols (X and O)
        Socket p1 = serverSocket.accept();
        System.out.println("Player 1 connected (X)");
        gameServer.addPlayer(p1, 'X');

        // Wait for the second player to connect before starting the game
        Socket p2 = serverSocket.accept();
        System.out.println("Player 2 connected (O)");
        gameServer.addPlayer(p2, 'O');

        // Start the game once both players are connected
        gameServer.startGame();

        // Log a message to indicate that the game is now running
        System.out.println("Both players connected. Game running...");
    }
}