package client;

import game.GameState;
import shared.Message;
import shared.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {
    // This class manages the client-side socket connection to the host/server.
    public interface Listener {
        void onAssignedSymbol(char symbol);
        void onStateUpdated(GameState state);
        void onInfo(String message);
        void onWaiting(String message);
        void onInvalidMove(String message);
        void onDisconnected(String message);
        void onError(String message);
    }

    private final String host;
    private final int port;
    private final String playerName;
    private final Listener listener;
    private final GameState state;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private char symbol;

    public GameClient(String host, int port, String playerName, Listener listener) {
        this(host, port, playerName, listener, new GameState());
    }

    public GameClient(String host, int port, String playerName, Listener listener, GameState state) {
        this.host = host;
        this.port = port;
        this.playerName = playerName;
        this.listener = listener;
        this.state = state;
    }

    public void connect() throws IOException {
        // Edit how the client opens the socket connection here.
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        send(Message.of(Protocol.HELLO, playerName));

        Thread thread = new Thread(this::listenForMessages, "game-client-listener");
        thread.setDaemon(true);
        thread.start();
    }

    public void sendMove(int row, int col) {
        // Edit the outgoing move message format here if protocol requirements change.
        send(Message.of(Protocol.MOVE, String.valueOf(row), String.valueOf(col)));
    }

    public void requestRematch() {
        // Edit outgoing rematch requests here.
        send(Message.of(Protocol.REMATCH));
    }

    public void disconnect() {
        // Edit disconnect behavior here.
        send(Message.of(Protocol.DISCONNECT));
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }

    public GameState getState() {
        return state;
    }

    public char getSymbol() {
        return symbol;
    }

    private void send(Message message) {
        // This is the single path for sending messages from the client to the server.
        if (out != null) {
            out.println(message.serialize());
        }
    }

    private void listenForMessages() {
        // This loop waits for server updates and feeds them back into the UI/game state.
        try {
            String line;
            while ((line = in.readLine()) != null) {
                handleMessage(Message.parse(line));
            }
        } catch (IOException e) {
            listener.onDisconnected("The connection to the server was lost.");
        }
    }

    private void handleMessage(Message message) {
        // Edit how incoming server messages are interpreted here.
        switch (message.getType()) {
            case Protocol.WELCOME:
                symbol = message.getPart(0).charAt(0);
                listener.onAssignedSymbol(symbol);
                break;
            case Protocol.WAITING:
                listener.onWaiting(message.getPart(0));
                break;
            case Protocol.INFO:
                listener.onInfo(message.getPart(0));
                break;
            case Protocol.INVALID:
                listener.onInvalidMove(message.getPart(0));
                break;
            case Protocol.STATE:
                state.syncState(
                    Message.stringToBoard(message.getPart(5)),
                    message.getPart(2).charAt(0),
                    GameState.Phase.valueOf(message.getPart(0)),
                    GameState.Outcome.valueOf(message.getPart(1)),
                    message.getPart(6),
                    message.getPart(3),
                    message.getPart(4)
                );
                listener.onStateUpdated(state);
                break;
            case Protocol.ERROR:
                listener.onError(message.getPart(0));
                break;
            default:
                listener.onError("Unhandled server message: " + message.getType());
                break;
        }
    }
}
