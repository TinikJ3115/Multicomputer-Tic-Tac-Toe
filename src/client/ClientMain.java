package client;

import ui.MainMenu;

import javax.swing.SwingUtilities;

public class ClientMain {
    public static void main(String[] args) {
        // This is the simplest client-side entry point for launching the UI.
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }
}
