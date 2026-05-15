package server;

import game.GameState;
import shared.Message;
import shared.Protocol;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class GameServer {
    // This class is the server-side source of truth for the multiplayer game.
    private final GameState gameState;
    private final Set<Character> rematchVotes;
    private ClientHandler playerX;
    private ClientHandler playerO;

    public GameServer() {
        this.gameState = new GameState();
        this.rematchVotes = new HashSet<>();
    }

    public void addPlayer(Socket socket, char symbol) throws IOException {
        // Edit player assignment or connection setup here.
        ClientHandler handler = new ClientHandler(socket, symbol, this);
        Thread thread = new Thread(handler, "player-" + symbol);
        thread.start();

        if (symbol == 'X') {
            playerX = handler;
        } else {
            playerO = handler;
        }
    }

    public synchronized void registerPlayer(ClientHandler handler, String playerName) {
        // Edit what happens when a player first joins here.
        System.out.println("Player " + handler.getSymbol() + " is " + playerName);

        if (!bothPlayersConnected()) {
            handler.sendMessage(Message.of(Protocol.WAITING, "Waiting for the second player to connect."));
            return;
        }

        if (playerX.getPlayerName() == null || playerO.getPlayerName() == null) {
            broadcast(Message.of(Protocol.WAITING, "Waiting for both players to send their names."));
            return;
        }

        if (gameState.getPhase() == GameState.Phase.WAITING_FOR_PLAYERS) {
            gameState.configurePlayers(playerX.getPlayerName(), playerO.getPlayerName());
            gameState.startGame();
            rematchVotes.clear();
            broadcast(Message.of(Protocol.INFO, "Both players connected. The game is starting."));
            broadcastState();
        }
    }

    public synchronized void handleMove(ClientHandler handler, int row, int col) {
        // Edit server-side move validation and turn enforcement here.
        if (gameState.getPhase() != GameState.Phase.IN_PROGRESS) {
            handler.sendMessage(Message.of(Protocol.INVALID, "The game is not accepting moves right now."));
            return;
        }

        if (handler.getSymbol() != gameState.getCurrentPlayer()) {
            handler.sendMessage(Message.of(Protocol.INVALID, "It is not your turn."));
            return;
        }

        boolean applied = gameState.applyMove(row, col, handler.getSymbol());
        if (!applied) {
            handler.sendMessage(Message.of(Protocol.INVALID, gameState.getStatusMessage()));
            return;
        }

        broadcastState();
        if (gameState.getPhase() == GameState.Phase.FINISHED) {
            broadcast(Message.of(Protocol.INFO, gameState.getOutcomeMessage()));
        }
    }

    public synchronized void handleRematchRequest(ClientHandler handler) {
        // Edit rematch voting/start logic here.
        if (gameState.getPhase() != GameState.Phase.FINISHED) {
            handler.sendMessage(Message.of(Protocol.INVALID, "You can only request a rematch after the game ends."));
            return;
        }

        rematchVotes.add(handler.getSymbol());
        if (rematchVotes.size() < 2) {
            handler.sendMessage(Message.of(Protocol.WAITING, "Rematch requested. Waiting for the other player."));
            otherPlayer(handler).sendMessage(Message.of(Protocol.INFO, handler.getPlayerName() + " wants a rematch."));
            return;
        }

        rematchVotes.clear();
        gameState.resetForRematch();
        broadcast(Message.of(Protocol.INFO, "Rematch starting now."));
        broadcastState();
    }

    public synchronized void handleDisconnect(ClientHandler handler) {
        // Edit disconnect behavior or disconnect messages here.
        String name = handler.getPlayerName() == null ? ("Player " + handler.getSymbol()) : handler.getPlayerName();
        ClientHandler other = otherPlayer(handler);
        rematchVotes.clear();

        if (handler == playerX) {
            playerX = null;
        } else if (handler == playerO) {
            playerO = null;
        }

        if (other != null) {
            other.sendMessage(Message.of(Protocol.ERROR, name + " disconnected. The game has ended."));
        }
    }

    private void broadcastState() {
        // This is where the server pushes the latest board/game state to both clients.
        broadcast(Message.gameStateMessage(gameState));
    }

    private void broadcast(Message message) {
        // Edit this if you ever need to change who receives server messages.
        if (playerX != null) {
            playerX.sendMessage(message);
        }
        if (playerO != null) {
            playerO.sendMessage(message);
        }
    }

    private ClientHandler otherPlayer(ClientHandler handler) {
        if (handler == playerX) {
            return playerO;
        }
        if (handler == playerO) {
            return playerX;
        }
        return null;
    }

    private boolean bothPlayersConnected() {
        return playerX != null && playerO != null;
    }
}
