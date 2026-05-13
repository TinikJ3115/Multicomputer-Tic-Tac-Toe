package ui;

import game.GameState;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class GameWindow extends JFrame {
    // This is the main gameplay window shown after the menu.
    public interface MoveHandler {
        boolean onMoveRequested(int row, int col);
    }

    public interface FlowHandler {
        void onRestartRequested();
        void onBackToMenuRequested();
    }

    private final GameState gameState;
    private final BoardPanel boardPanel;
    private final JLabel statusLabel;
    private final JButton restartButton;
    private final JButton menuButton;
    private MoveHandler moveHandler;
    private FlowHandler flowHandler;

    public GameWindow(GameState gameState) {
        super("Multicomputer Tic Tac Toe");
        this.gameState = gameState;
        this.boardPanel = new BoardPanel();
        this.statusLabel = new JLabel("", SwingConstants.CENTER);
        this.restartButton = new JButton("Restart");
        this.menuButton = new JButton("Main Menu");

        // Edit gameplay window sizing/layout here.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        setMinimumSize(new Dimension(420, 500));
        setLocationByPlatform(true);

        statusLabel.setPreferredSize(new Dimension(0, 48));
        add(statusLabel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionsPanel.add(restartButton);
        actionsPanel.add(menuButton);
        add(actionsPanel, BorderLayout.SOUTH);

        // Edit what happens when the player clicks a board square here.
        boardPanel.setMoveListener((row, col) -> {
            if (moveHandler == null) {
                return;
            }

            boolean accepted = moveHandler.onMoveRequested(row, col);
            if (!accepted && gameState.getPhase() == GameState.Phase.IN_PROGRESS) {
                ResultPopup.showError(this, gameState.getStatusMessage());
            }
            refresh();
        });

        restartButton.addActionListener(e -> {
            // Edit restart-button behavior here.
            if (flowHandler != null) {
                flowHandler.onRestartRequested();
            }
            refresh();
        });

        menuButton.addActionListener(e -> {
            // Edit main-menu button behavior here.
            if (flowHandler != null) {
                flowHandler.onBackToMenuRequested();
            }
        });

        refresh();
    }

    public void setMoveHandler(MoveHandler moveHandler) {
        this.moveHandler = moveHandler;
    }

    public void setFlowHandler(FlowHandler flowHandler) {
        this.flowHandler = flowHandler;
    }

    public void refresh() {
        // This is the main place where the screen redraws from the latest GameState.
        statusLabel.setText(gameState.getStatusMessage());
        boardPanel.renderBoard(gameState.getBoardSnapshot());
        boardPanel.setBoardEnabled(gameState.getPhase() == GameState.Phase.IN_PROGRESS);
        restartButton.setEnabled(gameState.getPhase() != GameState.Phase.WAITING_FOR_PLAYERS);
    }

    public void showWindow() {
        SwingUtilities.invokeLater(() -> {
            pack();
            setVisible(true);
        });
    }

    public void showResultIfFinished() {
        // Edit end-of-game popup flow here.
        if (gameState.getPhase() != GameState.Phase.FINISHED) {
            return;
        }

        boolean playAgain = ResultPopup.showGameResult(this, gameState.getOutcomeMessage());
        if (playAgain) {
            if (flowHandler != null) {
                flowHandler.onRestartRequested();
            }
        } else if (flowHandler != null) {
            flowHandler.onBackToMenuRequested();
        }

        refresh();
    }
}
