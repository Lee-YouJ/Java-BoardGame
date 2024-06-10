//swing, awt 라이브러리 사용을 위한 패키지 임포트, 사용자 액선 처리 클래스 임포트, BFS 알고리즘 활용 중 Queue 인터페이스를 사용하기 위해 패키지를 임포트함
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;

//JFrame을 상속받아 GUI윈도우 생성, 각각 게임 보드 버튼, 이동 진행, 벽 배치, 벽 배치시 행 위치, 벽 배치 시 열 위치, 벽의 방향, 벽 배치 횟수를 나타내는 배열 및 변수임
public class QuoridorGame extends JFrame {
    private JButton[][] buttons;
    private int currentPlayer;
    private boolean moveInProgress;
    private boolean wallPlacementInProgress;
    private int wallRow;
    private int wallCol;
    private boolean isVerticalWall;
    private int[] wallCounts;

//보드게임 GUI를 17*17 그리드레이아웃으로 설정, 로직을 구현.
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

//보드 초기화 및 GUI 버튼 배치. 짝수 행과 열은 흰색으로, 홀수 행과 열은 회색으로 색칠하여 플레이어 이동 칸과 벽 생성 칸 나눔.
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

//각 플레이어의 게임 시작 시 위치 설정
    private void setInitialPlayerPositions() {
        buttons[0][8].setText("1");
        buttons[16][8].setText("2");
    }


//현재 플레이어의 차례 시작, 이동 또는 벽 생성을 선택하고 벽 생성 선택 시 벽의 방향도 선택하도록 안내, 벽 생성 횟수를 세어 남은 횟수를 출력함(벽을 10번 생성 시 벽 생성 버튼이 사라지도록 함)
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


//벽 배치 모드를 시작
    private void startWallPlacement() {
    wallPlacementInProgress = true;
}


//벽 배치 완료 후 플레이어 턴을 변경
    private void endWallPlacement() {
        wallPlacementInProgress = false;
        moveInProgress = false;
        changePlayer();
        startPlayerTurn();
    }

//벽 생성 모드 중 선택한 위치가 유효한지 판단, 유효할 시 벽 생성
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

// 각 플레이어의 현재 위치를 확인
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

/**이하 너비 우선 탐색 BFS 알고리즘 활용. 
BFS 알고리즘은 시작점부터 가까운 곳을 먼저 방문하고 멀리 떨어져 있는 곳을 나중에 방문하는 알고리즘으로, 이동 가능한 경로를 최대한 파악한 후 더 이상 방문할 곳이 없을 때 탐색을 종료함.
방문한 노드들을 차례로 저장한 후 꺼낼 수 있는 자료구조인 "큐(Queue)"를 사용**/

        return bfs(player1Row, player1Col, 16) && bfs(player2Row, player2Col, 0);
    }

//17*17의 배열을 생성, 방문한 위치를 기록함
    private boolean bfs(int startRow, int startCol, int goalRow) {
        boolean[][] visited = new boolean[17][17];

//BFS 알고리즘 구현을 위해 큐 생성, 큐는 방문할 위치를 저장하고 순서대로 처리함
        Queue<int[]> queue = new LinkedList<>();

//시작 위치에 큐를 추가하여 BFS 탐색을 시작하고, 시작 위치는 이미 방문하였음을 기록
        queue.add(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

//이동 시 수직, 수평 방향의 이동 위치를 정의
        int[] dRow = {-2, 2, 0, 0};
        int[] dCol = {0, 0, -2, 2};

//큐를 반복, 큐의 다음 위치를 가져오고 현재 위치의 행과 열을 가져옴
        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int row = pos[0];
            int col = pos[1];

//승리 조건에 도달할 수 있을 때 true를 반환
            if ((goalRow == 16 && row == goalRow) || (goalRow == 0 && row == goalRow)) {
                return true;
            }


//이동한 위치가 유효하고, 이전에 방문한 적이 없으며, 벽을 통과할 수 있는지를 확인
            for (int i = 0; i < 4; i++) {
                int newRow = row + dRow[i];
                int newCol = col + dCol[i];
                int midRow = (row + newRow) / 2;
                int midCol = (col + newCol) / 2;

                if (newRow >= 0 && newRow < 17 && newCol >= 0 && newCol < 17 && buttons[newRow][newCol].getBackground() == Color.WHITE && !visited[newRow][newCol] && buttons[midRow][midCol].getBackground() != Color.BLACK) {

//새로운 위치를 방문하였음을 표시
                    visited[newRow][newCol] = true;

//이동할 수 있는 위치를 큐에 추가
                    queue.add(new int[]{newRow, newCol});

                }
            }
        }

//승리 조건에 도달하지 못하는 경우 false를 반환하여 해당 위치에 벽을 생성하지 못하도록 함
        return false;

    }

//해당 위치에 벽이 존재하는지 여부를 '배경색 BLACK'으로 판단
    private boolean checkOverlap(int row, int col) {
        return row >= 0 && row < 17 && col >= 0 && col < 17 && buttons[row][col].getBackground() == Color.BLACK;
    }

//버튼 클릭에 대한 이벤트를 처리, 이동 및 벽 배치 동작 처리
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

//플레이어가 조건에 따라 유효한 이동을 수행하는 지 확인, 이동이 불가능한 곳일 경우 칸 선택으로 반환
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

//이동 후 기존 위치에서 플레이어를 삭제
        private void clearPreviousPosition() {
            for (int i = 0; i < 17; i++) {
                for (int j = 0; j < 17; j++) {
                    if (buttons[i][j].getText().equals(String.valueOf(currentPlayer))) {
                        buttons[i][j].setText("");
                    }
                }
            }
        }

//플레이어가 승리 조건(맞은 편 끝 행에 도착)을 달성하였는지 확인, 달성 시 알림 창 출력 및 시스템 종료
        private void checkWinCondition(int row, int col) {
            if ((currentPlayer == 1 && row == 16) || (currentPlayer == 2 && row == 0)) {
                JOptionPane.showMessageDialog(null, "플레이어 " + currentPlayer + "가 승리했습니다!", "게임 종료", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        }

    }

//현재 플레이어를 변경
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