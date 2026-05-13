package server;

import shared.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        // Edit startup arguments or default port selection here.
        int port = args.length > 0 ? Integer.parseInt(args[0]) : Constants.DEFAULT_PORT;
        runServer(port);
    }

    public static void runServer(int port) throws IOException {
        // This is where the server opens the listening socket.
        ServerSocket serverSocket = new ServerSocket(port);
        GameServer gameServer = new GameServer();

        System.out.println("Server started on port " + port);
        System.out.println("Waiting for players...");

        // Edit player-accept order here if the professor asks about connection flow.
        Socket p1 = serverSocket.accept();
        System.out.println("Player 1 connected (X)");
        gameServer.addPlayer(p1, 'X');

        Socket p2 = serverSocket.accept();
        System.out.println("Player 2 connected (O)");
        gameServer.addPlayer(p2, 'O');

        System.out.println("Both players connected. Waiting for game messages...");

        // This loop keeps the server process alive after both players join.
        while (!serverSocket.isClosed()) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static Thread startInBackground(int port) {
        // Edit this if you need different host-side background server behavior.
        Thread thread = new Thread(() -> {
            try {
                runServer(port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "tic-tac-toe-server");
        thread.setDaemon(true);
        thread.start();
        return thread;
    }
}
