package ui;

import game.Board;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class BoardPanel extends JPanel {
    // This class controls the clickable 3x3 board UI.
    public interface MoveListener {
        void onMoveSelected(int row, int col);
    }

    private final JButton[][] buttons;
    private MoveListener moveListener;

    public BoardPanel() {
        this.buttons = new JButton[Board.SIZE][Board.SIZE];
        // Edit board spacing or button font here to change board appearance.
        setLayout(new GridLayout(Board.SIZE, Board.SIZE, 8, 8));

        Font cellFont = new Font("SansSerif", Font.BOLD, 42);
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                JButton button = new JButton(" ");
                button.setFont(cellFont);
                // Edit click behavior here if the professor asks you to change how moves are selected.
                final int currentRow = row;
                final int currentCol = col;
                button.addActionListener(e -> {
                    if (moveListener != null) {
                        moveListener.onMoveSelected(currentRow, currentCol);
                    }
                });
                buttons[row][col] = button;
                add(button);
            }
        }
    }

    public void setMoveListener(MoveListener moveListener) {
        this.moveListener = moveListener;
    }

    public void renderBoard(char[][] board) {
        // Edit how X/O values are displayed on screen here.
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                char value = board[row][col];
                buttons[row][col].setText(value == ' ' ? " " : String.valueOf(value));
                buttons[row][col].setEnabled(value == ' ');
            }
        }
    }

    public void setBoardEnabled(boolean enabled) {
        // Edit when squares become clickable or disabled here.
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (!" ".equals(buttons[row][col].getText())) {
                    buttons[row][col].setEnabled(false);
                } else {
                    buttons[row][col].setEnabled(enabled);
                }
            }
        }
    }
}
