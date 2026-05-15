package server;

import shared.Message;
import shared.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    // One ClientHandler is created per connected player socket.
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final char symbol;
    private final GameServer server;
    private String playerName;
    private boolean disconnectHandled;

    public ClientHandler(Socket socket, char symbol, GameServer server) throws IOException {
        // Edit low-level socket stream setup here.
        this.socket = socket;
        this.symbol = symbol;
        this.server = server;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessage(String msg) {
        // This is the raw string send path from server to one client.
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
        // Edit per-client startup messages here.
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
            notifyDisconnectOnce();
        }
    }

    private void handleMessage(Message message) {
        // Edit how server-side incoming client messages are routed here.
        switch (message.getType()) {
            case Protocol.HELLO:
                // Edit player-name registration flow here.
                playerName = message.getPartCount() > 0 ? message.getPart(0) : ("Player " + symbol);
                server.registerPlayer(this, playerName);
                break;
            case Protocol.MOVE:
                // Edit client move parsing here.
                if (message.getPartCount() < 2) {
                    sendMessage(Message.of(Protocol.ERROR, "Move message was missing coordinates."));
                    return;
                }
                int row = Integer.parseInt(message.getPart(0));
                int col = Integer.parseInt(message.getPart(1));
                server.handleMove(this, row, col);
                break;
            case Protocol.REMATCH:
                // Edit client rematch request routing here.
                server.handleRematchRequest(this);
                break;
            case Protocol.DISCONNECT:
                // Edit explicit client disconnect behavior here.
                close();
                notifyDisconnectOnce();
                break;
            default:
                sendMessage(Message.of(Protocol.ERROR, "Unknown message type: " + message.getType()));
                break;
        }
    }

    public synchronized void close() {
        // Edit socket cleanup behavior here.
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    private synchronized void notifyDisconnectOnce() {
        if (disconnectHandled) {
            return;
        }
        disconnectHandled = true;
        server.handleDisconnect(this);
    }
}
