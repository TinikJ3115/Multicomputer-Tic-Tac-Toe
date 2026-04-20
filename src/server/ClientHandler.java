//Jack Tinik 4/20/26
// Handles communication with a single client in the Tic Tac Toe server application
package server;

import java.io.*;
import java.net.Socket;

// Each ClientHandler runs in its own thread to manage communication with a connected client
public class ClientHandler implements Runnable {

    private Socket socket; // Socket for communicating with the client
    private BufferedReader in; // Reader for receiving messages from the client
    private PrintWriter out; // Writer for sending messages to the client
    private char symbol; // The symbol (X or O) assigned to this client

    //Initializes the ClientHandler with the client's socket and assigned symbol
    public ClientHandler(Socket socket, char symbol) {
        this.socket = socket;
        this.symbol = symbol;

        // Set up input and output streams for communication with the client
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sends a message to the client
    public void sendMessage(String msg) {
        out.println(msg);
    }

    // The main loop for handling client communication, runs in a separate thread
    @Override
    public void run() {
        sendMessage("WELCOME " + symbol);
        System.out.println("Player " + symbol + " handler started"); // Log when a new client handler is started

        try {
            String input; // Read messages from the client until the connection is closed
            while ((input = in.readLine()) != null) {
                System.out.println("Received from " + symbol + ": " + input);// Log received messages for debugging purposes
            }
        } catch (IOException e) {
            System.out.println("Player " + symbol + " disconnected.");// Log when a client disconnects
        }
    }
}