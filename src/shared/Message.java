package shared;

import game.Board;
import game.GameState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Message {
    // This class defines how socket messages are packed into strings and unpacked again.
    private static final String DELIMITER = "|";
    private static final String DELIMITER_REGEX = "\\|";

    private final String type;
    private final List<String> parts;

    public Message(String type, List<String> parts) {
        this.type = type;
        this.parts = parts;
    }

    public static Message of(String type, String... parts) {
        return new Message(type, Arrays.asList(parts));
    }

    public static Message parse(String raw) {
        // Edit parsing rules here if you change the wire/message format.
        String[] tokens = raw.split(DELIMITER_REGEX, -1);
        String type = tokens[0];
        List<String> parts = new ArrayList<>();
        for (int i = 1; i < tokens.length; i++) {
            parts.add(tokens[i].replace("\\p", DELIMITER));
        }
        return new Message(type, parts);
    }

    public String serialize() {
        // Edit serialization rules here if you change the wire/message format.
        StringBuilder builder = new StringBuilder(type);
        for (String part : parts) {
            builder.append(DELIMITER).append(escape(part));
        }
        return builder.toString();
    }

    public String getType() {
        return type;
    }

    public String getPart(int index) {
        return parts.get(index);
    }

    public int getPartCount() {
        return parts.size();
    }

    public static String boardToString(char[][] board) {
        // Edit this if you want a different way to encode the 3x3 board for sockets.
        StringBuilder builder = new StringBuilder(Board.SIZE * Board.SIZE);
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                char value = board[row][col];
                builder.append(value == ' ' ? Constants.EMPTY_CELL_TOKEN : value);
            }
        }
        return builder.toString();
    }

    public static char[][] stringToBoard(String encodedBoard) {
        // Edit this if you change how boards are decoded from socket messages.
        char[][] board = new char[Board.SIZE][Board.SIZE];
        for (int i = 0; i < encodedBoard.length() && i < Board.SIZE * Board.SIZE; i++) {
            char value = encodedBoard.charAt(i);
            board[i / Board.SIZE][i % Board.SIZE] = value == Constants.EMPTY_CELL_TOKEN.charAt(0) ? ' ' : value;
        }
        return board;
    }

    public static Message gameStateMessage(GameState gameState) {
        // This defines the full STATE message sent from server to both clients.
        return Message.of(
            Protocol.STATE,
            gameState.getPhase().name(),
            gameState.getOutcome().name(),
            String.valueOf(gameState.getCurrentPlayer()),
            gameState.getPlayerXName(),
            gameState.getPlayerOName(),
            boardToString(gameState.getBoardSnapshot()),
            gameState.getStatusMessage()
        );
    }

    private static String escape(String value) {
        // Edit escaping here if special characters ever break the protocol format.
        return value.replace(DELIMITER, "\\p");
    }
}
