package team;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormationGame extends JFrame {
    private final int GRID_SIZE = 7;
    private JButton[][] buttons = new JButton[GRID_SIZE][GRID_SIZE];
    private boolean isBlueTurn = true;
    private Point lastBlueCube = null, lastPinkCube = null;

    public FormationGame() {
        setTitle("포메이션 게임");
        setSize(700, 700);
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창 닫기 동작 설정
        setLocationRelativeTo(null);

        // Initialize buttons
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                JButton button = new JButton();
                button.setBackground(Color.WHITE); // Set default background color to white
                button.addActionListener(new ButtonListener(i, j));
                buttons[i][j] = button;
                add(button);
            }
        }
        setVisible(true);
    }

    private class ButtonListener implements ActionListener {
        private int x, y;

        public ButtonListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isCubePlacable(x, y)) {
                if (isBlueTurn) {
                    buttons[x][y].setBackground(Color.BLUE);
                    lastBlueCube = new Point(x, y);
                } else {
                    buttons[x][y].setBackground(Color.PINK);
                    lastPinkCube = new Point(x, y);
                }
                buttons[x][y].setEnabled(false); // Disable button after placing the cube

                if (checkVictory(x, y)) {
                    int playAgain = JOptionPane.showConfirmDialog(null, "게임을 다시 플레이 하시겠습니까? (Y/N)", "승리!", JOptionPane.YES_NO_OPTION);
                    if (playAgain == JOptionPane.YES_OPTION) {
                        resetGame();
                    } else {
                        dispose(); // 현재 창만 닫기
                    }
                }
                isBlueTurn = !isBlueTurn; // Switch turn
            }
        }
    }

    private boolean isCubePlacable(int x, int y) {
        // First turn, can place anywhere
        if (lastBlueCube == null && lastPinkCube == null) {
            return true;
        }

        // Check if there are any valid adjacent positions for the current player
        Point lastCube = isBlueTurn ? lastPinkCube : lastBlueCube;
        if (lastCube != null && hasPlacableAdjacentCube(lastCube.x, lastCube.y)) {
            // Only allow placing adjacent to the last placed cube if there are valid adjacent positions
            return Math.abs(lastCube.x - x) <= 1 && Math.abs(lastCube.y - y) <= 1 && buttons[x][y].getBackground().equals(Color.WHITE);
        } else {
            // If no adjacent positions are available, allow placing anywhere
            return buttons[x][y].getBackground().equals(Color.WHITE);
        }
    }

    private boolean hasPlacableAdjacentCube(int x, int y) {
        // Check if there are any empty adjacent positions
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = x + i;
                int newY = y + j;
                if (newX >= 0 && newX < GRID_SIZE && newY >= 0 && newY < GRID_SIZE && !(i == 0 && j == 0)) {
                    if (buttons[newX][newY].getBackground().equals(Color.WHITE)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkVictory(int x, int y) {
        Color currentColor = buttons[x][y].getBackground();

        // Check in all four directions: horizontal, vertical, and two diagonals
        int[][] directions = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        for (int[] dir : directions) {
            int count = 1; // Start with the current position
            count += countCubesInDirection(x, y, dir[0], dir[1], currentColor);
            count += countCubesInDirection(x, y, -dir[0], -dir[1], currentColor);

            // Win condition: 4 or more in a line
            if (count >= 4) {
                return true;
            }
        }
        return false;
    }

    private int countCubesInDirection(int x, int y, int dx, int dy, Color color) {
        int count = 0;
        x += dx;
        y += dy;

        // Count consecutive cubes in the specified direction
        while (x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE && buttons[x][y].getBackground().equals(color)) {
            count++;
            x += dx;
            y += dy;
        }
        return count;
    }

    private void resetGame() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                buttons[i][j].setBackground(Color.WHITE);
                buttons[i][j].setEnabled(true);
            }
        }
        isBlueTurn = true;
        lastBlueCube = null;
        lastPinkCube = null;
    }

    public static void main(String[] args) {
        new FormationGame();
    }
}