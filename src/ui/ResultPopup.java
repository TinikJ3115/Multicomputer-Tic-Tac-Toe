package ui;

import java.awt.Component;

import javax.swing.JOptionPane;

public final class ResultPopup {
    private ResultPopup() {
    }

    public static boolean showGameResult(Component parent, String message) {
        // Edit the win/draw popup text or buttons here.
        int result = JOptionPane.showConfirmDialog(
            parent,
            message + "\n\nWould you like to play again?",
            "Game Over",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );

        return result == JOptionPane.YES_OPTION;
    }

    public static void showError(Component parent, String message) {
        // Edit invalid-move / connection error popup text here.
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Invalid Move",
            JOptionPane.WARNING_MESSAGE
        );
    }
}
