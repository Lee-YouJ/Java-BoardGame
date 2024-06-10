import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;

public class QuoridorGame extends JFrame {
    private JButton[][] buttons;
    private int currentPlayer;
    private boolean moveInProgress;
    private boolean wallPlacementInProgress;
    private int wallRow;
    private int wallCol;
    private boolean isVerticalWall;
    private int[] wallCounts;

    public QuoridorGame() {
        setTitle("Quoridor");
        setSize(700, 700);
        setLayout(new GridLayout(17, 17));
        setLocationRelativeTo(null);

        buttons = new JButton[17][17];
        currentPlayer = 1;
        moveInProgress = false;
        wallPlacementInProgress = false;
        wallCounts = new int[3];

        initializeBoard();
        setInitialPlayerPositions();

        setVisible(true);
        startPlayerTurn();
    }

    private void initializeBoard() {
        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 17; j++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(40, 40));
                button.setContentAreaFilled(false);
                button.setOpaque(true);
                if (i % 2 == 0 && j % 2 == 0) {
                    button.setBackground(Color.WHITE);
                } else {
                    button.setBackground(Color.LIGHT_GRAY);
                }
                button.addActionListener(new MoveListener());
                buttons[i][j] = button;
                add(button);
            }
        }
    }

    private void setInitialPlayerPositions() {
        buttons[0][8].setText("1");
        buttons[16][8].setText("2");
    }

    private void startPlayerTurn() {
        String[] options;
        if (wallPlacementInProgress) {
            options = new String[]{"수직", "수평"};
        } else {
            options = new String[]{"이동"};
            if (wallCounts[currentPlayer] < 10) {
                options = new String[]{"이동", "벽 생성 (남은 횟수: " + (10 - wallCounts[currentPlayer]) + ")"};
            }
        }

        int choice = JOptionPane.showOptionDialog(null, "플레이어 " + currentPlayer + "의 차례입니다. 이동할 것인지 벽을 생성할 것인지 선택하세요.", "행동 선택", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            if (wallPlacementInProgress) {
                JOptionPane.showMessageDialog(null, "벽의 방향을 수직으로 선택하셨습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                isVerticalWall = true;
                startWallPlacement();
            } else {
                JOptionPane.showMessageDialog(null, "이동을 선택하셨습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                moveInProgress = true;
            }
        } else if (choice == 1) {
            JOptionPane.showMessageDialog(null, "벽 생성을 선택하셨습니다. 벽의 방향을 선택하세요.", "벽 생성 선택", JOptionPane.INFORMATION_MESSAGE);
            String[] wallDirectionOptions = new String[]{"수직", "수평"};
            int wallDirectionChoice = JOptionPane.showOptionDialog(null, "벽의 방향을 선택하세요.", "벽 방향 선택", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, wallDirectionOptions, wallDirectionOptions[0]);

            if (wallDirectionChoice == 0) {
                isVerticalWall = true;
            } else if (wallDirectionChoice == 1) {
                isVerticalWall = false;
            }

            startWallPlacement();
        }
    }

    private void startWallPlacement() {
        if (!wallPlacementInProgress) {
            wallPlacementInProgress = true;
        } else {
            JOptionPane.showMessageDialog(null, "이미 벽을 배치하셨습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            startPlayerTurn();
        }
    }

    private void endWallPlacement() {
        wallPlacementInProgress = false;
        moveInProgress = false;
        changePlayer();
        startPlayerTurn();
    }

    private void placeWall() {
        if (isVerticalWall) {
            if (!checkOverlap(wallRow - 1, wallCol) && !checkOverlap(wallRow, wallCol) && !checkOverlap(wallRow + 1, wallCol)) {
                for (int i = -1; i <= 1; i++) {
                    buttons[wallRow + i][wallCol].setBackground(Color.BLACK);
                }
                if (canBothPlayersReachGoals()) {
                    wallCounts[currentPlayer]++;
                    endWallPlacement();
                } else {
                    for (int i = -1; i <= 1; i++) {
                        buttons[wallRow + i][wallCol].setBackground(Color.LIGHT_GRAY);
                    }
                    JOptionPane.showMessageDialog(null, "벽이 경로를 차단합니다. 다른 위치에 벽을 생성하세요.", "알림", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "벽이 겹쳐서 생성할 수 없는 위치입니다.", "알림", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (!checkOverlap(wallRow, wallCol - 1) && !checkOverlap(wallRow, wallCol) && !checkOverlap(wallRow, wallCol + 1)) {
                for (int i = -1; i <= 1; i++) {
                    buttons[wallRow][wallCol + i].setBackground(Color.BLACK);
                }
                if (canBothPlayersReachGoals()) {
                    wallCounts[currentPlayer]++;
                    endWallPlacement();
                } else {
                    for (int i = -1; i <= 1; i++) {
                        buttons[wallRow][wallCol + i].setBackground(Color.LIGHT_GRAY);
                    }
                    JOptionPane.showMessageDialog(null, "벽이 경로를 차단합니다. 다른 위치에 벽을 생성하세요.", "알림", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "벽이 겹쳐서 생성할 수 없는 위치입니다.", "알림", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean canBothPlayersReachGoals() {
        int player1Row = -1, player1Col = -1;
        int player2Row = -1, player2Col = -1;
        
        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 17; j++) {
                if (buttons[i][j].getText().equals("1")) {
                    player1Row = i;
                    player1Col = j;
                }
                if (buttons[i][j].getText().equals("2")) {
                    player2Row = i;
                    player2Col = j;
                }
            }
        }

        return bfs(player1Row, player1Col, 16) && bfs(player2Row, player2Col, 0);
    }

    private boolean bfs(int startRow, int startCol, int goalRow) {
        boolean[][] visited = new boolean[17][17];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

        int[] dRow = {-2, 2, 0, 0};
        int[] dCol = {0, 0, -2, 2};

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int row = pos[0];
            int col = pos[1];

            if ((goalRow == 16 && row == goalRow) || (goalRow == 0 && row == goalRow)) {
                return true;
            }

            for (int i = 0; i < 4; i++) {
                int newRow = row + dRow[i];
                int newCol = col + dCol[i];
                int midRow = (row + newRow) / 2;
                int midCol = (col + newCol) / 2;

                if (newRow >= 0 && newRow < 17 && newCol >= 0 && newCol < 17 && buttons[newRow][newCol].getBackground() == Color.WHITE && !visited[newRow][newCol] && buttons[midRow][midCol].getBackground() != Color.BLACK) {
                    visited[newRow][newCol] = true;
                    queue.add(new int[]{newRow, newCol});
                }
            }
        }

        return false;
    }

    private boolean checkOverlap(int row, int col) {
        return row >= 0 && row < 17 && col >= 0 && col < 17 && buttons[row][col].getBackground() == Color.BLACK;
    }

    private class MoveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton clickedButton = (JButton) e.getSource();
            int row = -1, col = -1;
            for (int i = 0; i < 17; i++) {
                for (int j = 0; j < 17; j++) {
                    if (buttons[i][j] == clickedButton) {
                        row = i;
                        col = j;
                        break;
                    }
                }
                if (row != -1) break;
            }

            if (row != -1 && col != -1) {
                if (moveInProgress) {
                    if (!wallPlacementInProgress && (buttons[row][col].getText().isEmpty() || buttons[row][col].getText().equals(String.valueOf(currentPlayer)))) {
                        if (isValidMove(row, col)) {
                            clearPreviousPosition();
                            buttons[row][col].setText(String.valueOf(currentPlayer));
                            checkWinCondition(row, col);
                            changePlayer();
                            startPlayerTurn();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "해당 위치에는 벽을 생성할 수 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else if (wallPlacementInProgress && buttons[row][col].getBackground() == Color.LIGHT_GRAY && row % 2 != 0 && col % 2 != 0) {
                    wallRow = row;
                    wallCol = col;
                    placeWall();
                } else {
                    JOptionPane.showMessageDialog(null, "벽을 놓을 수 없는 위치입니다. 다시 선택하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }

        private boolean isValidMove(int row, int col) {
            int playerRow = -1;
            int playerCol = -1;

            for (int i = 0; i < 17; i++) {
                for (int j = 0; j < 17; j++) {
                    if (buttons[i][j].getText().equals(String.valueOf(currentPlayer))) {
                        playerRow = i;
                        playerCol = j;
                        break;
                    }
                }
                if (playerRow != -1 && playerCol != -1) {
                    break;
                }
            }

            if ((row == playerRow || col == playerCol) && Math.abs(row - playerRow) <= 2 && Math.abs(col - playerCol) <= 2) {
                if (buttons[row][col].getBackground() != Color.WHITE) {
                    JOptionPane.showMessageDialog(null, "해당 칸은 이동하실 수 없습니다!", "이동 불가", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                if (!buttons[row][col].getText().isEmpty() && !buttons[row][col].getText().equals(String.valueOf(currentPlayer))) {
                    int nextRow = 2 * row - playerRow;
                    int nextCol = 2 * col - playerCol;
                    if (nextRow >= 0 && nextRow < 17 && nextCol >= 0 && nextCol < 17 && buttons[nextRow][nextCol].getBackground() == Color.WHITE) {
                        clearPreviousPosition();
                        buttons[nextRow][nextCol].setText(String.valueOf(currentPlayer));
                        checkWinCondition(nextRow, nextCol);
                        changePlayer();
                        startPlayerTurn();
                        return true;
                    }
                } else {
                    if (row == playerRow) {
                        int minCol = Math.min(playerCol, col);
                        int maxCol = Math.max(playerCol, col);
                        for (int c = minCol; c <= maxCol; c++) {
                            if (buttons[row][c].getBackground() == Color.BLACK) {
                                JOptionPane.showMessageDialog(null, "벽을 뛰어넘어서 이동할 수 없습니다.", "이동 불가", JOptionPane.ERROR_MESSAGE);
                                return false;
                            }
                        }
                    } else {
                        int minRow = Math.min(playerRow, row);
                        int maxRow = Math.max(playerRow, row);
                        for (int r = minRow; r <= maxRow; r++) {
                            if (buttons[r][col].getBackground() == Color.BLACK) {
                                JOptionPane.showMessageDialog(null, "벽을 뛰어넘어서 이동할 수 없습니다.", "이동 불가", JOptionPane.ERROR_MESSAGE);
                                return false;
                            }
                        }
                    }
                    return true;
                }
            }
            JOptionPane.showMessageDialog(null, "해당 칸은 이동하실 수 없습니다!", "이동 불가", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        private void clearPreviousPosition() {
            for (int i = 0; i < 17; i++) {
                for (int j = 0; j < 17; j++) {
                    if (buttons[i][j].getText().equals(String.valueOf(currentPlayer))) {
                        buttons[i][j].setText("");
                    }
                }
            }
        }

        private void checkWinCondition(int row, int col) {
            if ((currentPlayer == 1 && row == 16) || (currentPlayer == 2 && row == 0)) {
                JOptionPane.showMessageDialog(null, "플레이어 " + currentPlayer + "가 승리했습니다!", "게임 종료", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        }

    }

    private void changePlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        moveInProgress = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new QuoridorGame();
        });
    }
}