package game;

public class GameState {
    // Edit these enums if you want to change the major stages or outcomes of a match.
    public enum Phase {
        WAITING_FOR_PLAYERS,
        IN_PROGRESS,
        FINISHED
    }

    public enum Outcome {
        NONE,
        X_WINS,
        O_WINS,
        DRAW
    }

    private final Board board;
    private char currentPlayer;
    private Phase phase;
    private Outcome outcome;
    private String statusMessage;
    private String playerXName;
    private String playerOName;

    public GameState() {
        this.board = new Board();
        this.playerXName = "Player X";
        this.playerOName = "Player O";
        resetForNewSession();
    }

    public void configurePlayers(String playerXName, String playerOName) {
        // Change default/fallback player naming behavior here.
        this.playerXName = normalizeName(playerXName, "Player X");
        this.playerOName = normalizeName(playerOName, "Player O");
        statusMessage = this.playerXName + " vs " + this.playerOName;
    }

    public void startGame() {
        // Change the starting player here if the professor asks for O to go first.
        board.resetBoard();
        currentPlayer = 'X';
        phase = Phase.IN_PROGRESS;
        outcome = Outcome.NONE;
        statusMessage = currentPlayerName() + "'s turn";
    }

    public boolean canPlay(int row, int col) {
        return phase == Phase.IN_PROGRESS && board.isCellEmpty(row, col);
    }

    public boolean applyMove(int row, int col) {
        return applyMove(row, col, currentPlayer);
    }

    public boolean applyMove(int row, int col, char playerSymbol) {
        // Edit turn validation and invalid-move rules in this method.
        if (phase != Phase.IN_PROGRESS) {
            statusMessage = "Start a game before making a move.";
            return false;
        }

        if (playerSymbol != currentPlayer) {
            statusMessage = "It is not " + playerSymbol + "'s turn.";
            return false;
        }

        if (!board.makeMove(row, col, playerSymbol)) {
            statusMessage = "That square is already taken.";
            return false;
        }

        updateAfterMove();
        return true;
    }

    public void resetForRematch() {
        // Edit rematch behavior here if you want a different reset flow.
        startGame();
    }

    public void resetForNewSession() {
        // Edit the initial waiting-screen status text here.
        board.resetBoard();
        currentPlayer = 'X';
        phase = Phase.WAITING_FOR_PLAYERS;
        outcome = Outcome.NONE;
        statusMessage = "Enter player names to begin.";
    }

    private void updateAfterMove() {
        // Edit what happens after every valid move here.
        char[][] snapshot = board.getBoard();
        if (GameLogic.checkWin(snapshot, currentPlayer)) {
            phase = Phase.FINISHED;
            outcome = currentPlayer == 'X' ? Outcome.X_WINS : Outcome.O_WINS;
            statusMessage = currentPlayerName() + " wins!";
            return;
        }

        if (GameLogic.checkDraw(snapshot)) {
            phase = Phase.FINISHED;
            outcome = Outcome.DRAW;
            statusMessage = "It's a draw.";
            return;
        }

        currentPlayer = GameLogic.nextPlayer(currentPlayer);
        statusMessage = currentPlayerName() + "'s turn";
    }

    private String currentPlayerName() {
        return currentPlayer == 'X' ? playerXName : playerOName;
    }

    private String normalizeName(String name, String fallback) {
        if (name == null || name.trim().isEmpty()) {
            return fallback;
        }
        return name.trim();
    }

    public Board getBoard() {
        return board;
    }

    public char[][] getBoardSnapshot() {
        return board.getBoard();
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public Phase getPhase() {
        return phase;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getPlayerXName() {
        return playerXName;
    }

    public String getPlayerOName() {
        return playerOName;
    }

    public String getOutcomeMessage() {
        // Edit the final result text shown to players here.
        switch (outcome) {
            case X_WINS:
                return playerXName + " wins as X.";
            case O_WINS:
                return playerOName + " wins as O.";
            case DRAW:
                return "The game ended in a draw.";
            default:
                return "The game is still in progress.";
        }
    }

    public void syncState(
        char[][] boardSnapshot,
        char currentPlayer,
        Phase phase,
        Outcome outcome,
        String statusMessage,
        String playerXName,
        String playerOName
    ) {
        // This is the main place where server/network state overwrites the local copy.
        board.loadBoard(boardSnapshot);
        this.currentPlayer = currentPlayer;
        this.phase = phase;
        this.outcome = outcome;
        this.statusMessage = statusMessage;
        this.playerXName = normalizeName(playerXName, "Player X");
        this.playerOName = normalizeName(playerOName, "Player O");
    }
}
