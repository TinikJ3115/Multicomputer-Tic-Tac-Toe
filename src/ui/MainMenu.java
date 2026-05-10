package ui;

import client.GameClient;
import game.GameState;
import server.ServerMain;
import shared.Constants;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;

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
    private final JTextField networkNameField;
    private final JTextField hostField;
    private final JTextField portField;
    private final JLabel hostIpLabel;

    public MainMenu() {
        super("Multicomputer Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        setSize(460, 360);
        setLocationByPlatform(true);

        JLabel title = new JLabel("Start a Tic Tac Toe Match", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(2, 1, 12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel localPanel = new JPanel(new BorderLayout(8, 8));
        localPanel.setBorder(BorderFactory.createTitledBorder("Local Demo"));
        JPanel localForm = new JPanel(new GridLayout(2, 2, 8, 8));
        localForm.add(new JLabel("Player X Name:"));
        playerXField = new JTextField("Player X");
        localForm.add(playerXField);
        localForm.add(new JLabel("Player O Name:"));
        playerOField = new JTextField("Player O");
        localForm.add(playerOField);
        localPanel.add(localForm, BorderLayout.CENTER);

        JButton startLocalButton = new JButton("Start Local Demo");
        startLocalButton.addActionListener(e -> launchLocalGame());
        localPanel.add(startLocalButton, BorderLayout.SOUTH);

        JPanel networkPanel = new JPanel(new BorderLayout(8, 8));
        networkPanel.setBorder(BorderFactory.createTitledBorder("Network Game"));
        JPanel networkForm = new JPanel(new GridLayout(3, 2, 8, 8));
        networkForm.add(new JLabel("Your Name:"));
        networkNameField = new JTextField("Player");
        networkForm.add(networkNameField);
        networkForm.add(new JLabel("Server Host:"));
        hostField = new JTextField(Constants.DEFAULT_HOST);
        networkForm.add(hostField);
        networkForm.add(new JLabel("Port:"));
        portField = new JTextField(String.valueOf(Constants.DEFAULT_PORT));
        networkForm.add(portField);
        networkPanel.add(networkForm, BorderLayout.CENTER);

        hostIpLabel = new JLabel("Host LAN IP: " + detectLanIp(), SwingConstants.LEFT);
        networkPanel.add(hostIpLabel, BorderLayout.NORTH);

        JPanel networkActions = new JPanel(new GridLayout(1, 2, 8, 8));
        JButton hostButton = new JButton("Host and Join");
        hostButton.addActionListener(e -> hostAndJoinGame());
        JButton joinButton = new JButton("Join Server");
        joinButton.addActionListener(e -> joinNetworkGame(false));
        networkActions.add(hostButton);
        networkActions.add(joinButton);
        networkPanel.add(networkActions, BorderLayout.SOUTH);

        content.add(localPanel);
        content.add(networkPanel);
        add(content, BorderLayout.CENTER);
    }

    private void launchLocalGame() {
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

    private void hostAndJoinGame() {
        int port = parsePort();
        if (port < 0) {
            return;
        }

        String lanIp = detectLanIp();

        try {
            ServerMain.startInBackground(port);
            Thread.sleep(250L);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to host the server: " + e.getMessage(),
                "Server Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        hostField.setText(lanIp);
        hostIpLabel.setText("Host LAN IP: " + lanIp);
        JOptionPane.showMessageDialog(
            this,
            "Server started.\n\nOn the other computer, use this host address:\n" + lanIp + "\nPort: " + port,
            "Host Information",
            JOptionPane.INFORMATION_MESSAGE
        );
        joinNetworkGame(true);
    }

    private void joinNetworkGame(boolean hostedLocally) {
        int port = parsePort();
        if (port < 0) {
            return;
        }

        String host;
        if (hostedLocally) {
            host = Constants.DEFAULT_HOST;
        } else {
            host = hostField.getText().trim().isEmpty() ? Constants.DEFAULT_HOST : hostField.getText().trim();
        }
        String playerName = networkNameField.getText().trim().isEmpty() ? "Player" : networkNameField.getText().trim();

        GameState networkState = new GameState();
        networkState.syncState(
            networkState.getBoardSnapshot(),
            'X',
            GameState.Phase.WAITING_FOR_PLAYERS,
            GameState.Outcome.NONE,
            hostedLocally ? "Hosting server and connecting..." : "Connecting to server...",
            playerName,
            "Waiting for opponent"
        );
        GameWindow window = new GameWindow(networkState);
        final GameClient[] clientHolder = new GameClient[1];
        final boolean[] resultDialogShown = new boolean[1];

        GameClient client = new GameClient(host, port, playerName, new GameClient.Listener() {
            @Override
            public void onAssignedSymbol(char symbol) {
                SwingUtilities.invokeLater(() ->
                    window.setTitle("Multicomputer Tic Tac Toe - Player " + symbol)
                );
            }

            @Override
            public void onStateUpdated(GameState state) {
                SwingUtilities.invokeLater(() -> {
                    window.refresh();
                    if (state.getPhase() == GameState.Phase.FINISHED && !resultDialogShown[0]) {
                        resultDialogShown[0] = true;
                        window.showResultIfFinished();
                    } else if (state.getPhase() == GameState.Phase.IN_PROGRESS) {
                        resultDialogShown[0] = false;
                    }
                });
            }

            @Override
            public void onInfo(String message) {
                SwingUtilities.invokeLater(() -> updateClientStatus(networkState, message, window));
            }

            @Override
            public void onWaiting(String message) {
                SwingUtilities.invokeLater(() -> updateClientStatus(networkState, message, window));
            }

            @Override
            public void onInvalidMove(String message) {
                SwingUtilities.invokeLater(() -> ResultPopup.showError(window, message));
            }

            @Override
            public void onDisconnected(String message) {
                SwingUtilities.invokeLater(() -> {
                    ResultPopup.showError(window, message);
                    window.dispose();
                    setVisible(true);
                });
            }

            @Override
            public void onError(String message) {
                SwingUtilities.invokeLater(() -> ResultPopup.showError(window, message));
            }
        }, networkState);
        clientHolder[0] = client;

        window.setMoveHandler((row, col) -> {
            client.sendMove(row, col);
            return true;
        });
        window.setFlowHandler(new GameWindow.FlowHandler() {
            @Override
            public void onRestartRequested() {
                clientHolder[0].requestRematch();
            }

            @Override
            public void onBackToMenuRequested() {
                clientHolder[0].disconnect();
                window.dispose();
                setVisible(true);
            }
        });

        try {
            client.connect();
            setVisible(false);
            window.showWindow();
        } catch (Exception e) {
            if (hostedLocally) {
                ResultPopup.showError(this, "Server started, but the client could not connect: " + e.getMessage());
            } else {
                ResultPopup.showError(this, "Could not connect to the server: " + e.getMessage());
            }
        }
    }

    private void updateClientStatus(GameState state, String message, GameWindow window) {
        state.syncState(
            state.getBoardSnapshot(),
            state.getCurrentPlayer(),
            state.getPhase(),
            state.getOutcome(),
            message,
            state.getPlayerXName(),
            state.getPlayerOName()
        );
        window.refresh();
    }

    private int parsePort() {
        try {
            return Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException e) {
            ResultPopup.showError(this, "Please enter a valid port number.");
            return -1;
        }
    }

    private String detectLanIp() {
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }

                for (InetAddress address : Collections.list(networkInterface.getInetAddresses())) {
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "Not detected";
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
