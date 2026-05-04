package server;

import shared.Message;
import shared.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final char symbol;
    private final GameServer server;
    private String playerName;

    public ClientHandler(Socket socket, char symbol, GameServer server) throws IOException {
        this.socket = socket;
        this.symbol = symbol;
        this.server = server;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public void sendMessage(Message message) {
        sendMessage(message.serialize());
    }

    public char getSymbol() {
        return symbol;
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void run() {
        sendMessage(Message.of(Protocol.WELCOME, String.valueOf(symbol)));
        sendMessage(Message.of(Protocol.INFO, "Connected to the Tic Tac Toe server."));

        try {
            String input;
            while ((input = in.readLine()) != null) {
                handleMessage(Message.parse(input));
            }
        } catch (IOException e) {
            System.out.println("Player " + symbol + " disconnected.");
        } finally {
            close();
            server.handleDisconnect(this);
        }
    }

    private void handleMessage(Message message) {
        switch (message.getType()) {
            case Protocol.HELLO:
                playerName = message.getPartCount() > 0 ? message.getPart(0) : ("Player " + symbol);
                server.registerPlayer(this, playerName);
                break;
            case Protocol.MOVE:
                if (message.getPartCount() < 2) {
                    sendMessage(Message.of(Protocol.ERROR, "Move message was missing coordinates."));
                    return;
                }
                int row = Integer.parseInt(message.getPart(0));
                int col = Integer.parseInt(message.getPart(1));
                server.handleMove(this, row, col);
                break;
            case Protocol.REMATCH:
                server.handleRematchRequest(this);
                break;
            case Protocol.DISCONNECT:
                close();
                server.handleDisconnect(this);
                break;
            default:
                sendMessage(Message.of(Protocol.ERROR, "Unknown message type: " + message.getType()));
                break;
        }
    }

    public synchronized void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
