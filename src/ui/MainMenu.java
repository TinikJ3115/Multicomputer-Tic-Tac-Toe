package ui;

import game.GameState;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MainMenu extends JFrame {
    private final JTextField playerXField;
    private final JTextField playerOField;

    public MainMenu() {
        super("Multicomputer Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        setSize(380, 220);
        setLocationByPlatform(true);

        JLabel title = new JLabel("Start a Tic Tac Toe Match", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        form.add(new JLabel("Player X Name:"));
        playerXField = new JTextField("Player X");
        form.add(playerXField);
        form.add(new JLabel("Player O Name:"));
        playerOField = new JTextField("Player O");
        form.add(playerOField);
        add(form, BorderLayout.CENTER);

        JButton startButton = new JButton("Start Local Demo");
        startButton.addActionListener(e -> launchGame());
        add(startButton, BorderLayout.SOUTH);
    }

    private void launchGame() {
        GameState state = new GameState();
        state.configurePlayers(playerXField.getText(), playerOField.getText());
        state.startGame();

        GameWindow window = new GameWindow(state);
        window.setMoveHandler((row, col) -> {
            boolean applied = state.applyMove(row, col);
            if (applied && state.getPhase() == GameState.Phase.FINISHED) {
                SwingUtilities.invokeLater(window::showResultIfFinished);
            }
            return applied;
        });
        window.setFlowHandler(new GameWindow.FlowHandler() {
            @Override
            public void onRestartRequested() {
                state.resetForRematch();
            }

            @Override
            public void onBackToMenuRequested() {
                window.dispose();
                setVisible(true);
            }
        });

        setVisible(false);
        window.showWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                MainMenu menu = new MainMenu();
                menu.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    null,
                    "Unable to launch the game UI: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
