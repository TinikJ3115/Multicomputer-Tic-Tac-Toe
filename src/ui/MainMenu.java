package ui;

import client.GameClient;
import game.GameState;
import server.ServerMain;
import shared.Constants;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;

import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
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
        setMinimumSize(new Dimension(560, 430));
        setSize(560, 430);
        setLocationByPlatform(true);

        JLabel title = new JLabel("Start a Tic Tac Toe Match", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel localPanel = new JPanel(new BorderLayout(8, 8));
        localPanel.setBorder(BorderFactory.createTitledBorder("Local Demo"));
        playerXField = new JTextField("Player X");
        playerXField.setColumns(18);
        playerOField = new JTextField("Player O");
        playerOField.setColumns(18);
        JPanel localForm = createFormPanel(
            new String[] {"Player X Name:", "Player O Name:"},
            new JComponent[] {playerXField, playerOField}
        );
        localPanel.add(localForm, BorderLayout.CENTER);

        JButton startLocalButton = new JButton("Start Local Demo");
        startLocalButton.addActionListener(e -> launchLocalGame());
        localPanel.add(startLocalButton, BorderLayout.SOUTH);

        JPanel networkPanel = new JPanel(new BorderLayout(8, 8));
        networkPanel.setBorder(BorderFactory.createTitledBorder("Network Game"));
        networkNameField = new JTextField("Player");
        networkNameField.setColumns(18);
        hostField = new JTextField(Constants.DEFAULT_HOST);
        hostField.setColumns(18);
        portField = new JTextField(String.valueOf(Constants.DEFAULT_PORT));
        portField.setColumns(18);
        JPanel networkForm = createFormPanel(
            new String[] {"Your Name:", "Server Host:", "Port:"},
            new JComponent[] {networkNameField, hostField, portField}
        );
        networkPanel.add(networkForm, BorderLayout.CENTER);

        hostIpLabel = new JLabel("Host LAN IP: " + detectLanIp(), SwingConstants.LEFT);
        hostIpLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        networkPanel.add(hostIpLabel, BorderLayout.NORTH);

        JPanel networkActions = new JPanel(new GridBagLayout());
        GridBagConstraints actionConstraints = new GridBagConstraints();
        actionConstraints.insets = new Insets(0, 4, 0, 4);
        actionConstraints.fill = GridBagConstraints.HORIZONTAL;
        actionConstraints.weightx = 1.0;
        JButton hostButton = new JButton("Host and Join");
        hostButton.addActionListener(e -> hostAndJoinGame());
        JButton joinButton = new JButton("Join Server");
        joinButton.addActionListener(e -> joinNetworkGame(false));
        actionConstraints.gridx = 0;
        networkActions.add(hostButton, actionConstraints);
        actionConstraints.gridx = 1;
        networkActions.add(joinButton, actionConstraints);
        networkPanel.add(networkActions, BorderLayout.SOUTH);

        localPanel.setAlignmentX(LEFT_ALIGNMENT);
        networkPanel.setAlignmentX(LEFT_ALIGNMENT);
        localPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        networkPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        content.add(localPanel);
        content.add(javax.swing.Box.createVerticalStrut(12));
        content.add(networkPanel);
        add(content, BorderLayout.CENTER);
    }

    private JPanel createFormPanel(String[] labels, JComponent[] fields) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < labels.length; i++) {
            constraints.gridx = 0;
            constraints.gridy = i;
            constraints.weightx = 0;
            form.add(new JLabel(labels[i]), constraints);

            constraints.gridx = 1;
            constraints.weightx = 1.0;
            form.add(fields[i], constraints);
        }

        return form;
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
