package team;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RobotGame extends JPanel implements KeyListener {
    private final int ROWS = 25;
    private final int COLS = 25;
    private final int CELL_SIZE = 25;
    private final int GAP = 1;

    private char[][] grid;
    private int robotRow;
    private int robotCol;
    private int playerRow;
    private int playerCol;
    private int targetRow;
    private int targetCol;
    private int moves;
    private List<Point> obstacles;

    public RobotGame() {
        setPreferredSize(new Dimension(COLS * (CELL_SIZE + GAP), ROWS * (CELL_SIZE + GAP)));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        initializeGrid();
        createButtons();
    }

    private void initializeGrid() {
        grid = new char[ROWS][COLS];
        moves = 0;
        obstacles = new ArrayList<>();

        generateMaze();
        generateRandomTarget();
        generateRandomStart();
        generateRandomPlayerStart();

        grid[robotRow][robotCol] = 'R';
        grid[playerRow][playerCol] = 'P';
        grid[targetRow][targetCol] = 'G';
        for (Point obstacle : obstacles) {
            grid[obstacle.x][obstacle.y] = 'D';
        }
    }

    private void generateMaze() {
        Random random = new Random();

        // Initialize the grid with more open spaces
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                grid[i][j] = (random.nextFloat() < 0.7) ? '.' : 'D'; // 70% chance of open space
            }
        }

        // Ensure the parameter walls are solid
        for (int i = 0; i < ROWS; i++) {
            grid[i][0] = 'D';
            grid[i][COLS - 1] = 'D';
        }
        for (int j = 0; j < COLS; j++) {
            grid[0][j] = 'D';
            grid[ROWS - 1][j] = 'D';
        }
    }

    private void generateRandomTarget() {
        Random random = new Random();
        do {
            targetRow = random.nextInt(ROWS);
            targetCol = random.nextInt(COLS);
        } while (grid[targetRow][targetCol] != '.');
    }

    private void generateRandomStart() {
        Random random = new Random();
        do {
            robotRow = random.nextInt(ROWS);
            robotCol = random.nextInt(COLS);
        } while (grid[robotRow][robotCol] != '.' || (robotRow == targetRow && robotCol == targetCol));
    }

    private void generateRandomPlayerStart() {
        Random random = new Random();
        do {
            playerRow = random.nextInt(ROWS);
            playerCol = random.nextInt(COLS);
        } while (grid[playerRow][playerCol] != '.' || (playerRow == targetRow && playerCol == targetCol) ||
                (playerRow == robotRow && playerCol == robotCol));
    }

    private void moveRobot(int newRow, int newCol) {
        if (newRow < 0 || newRow >= ROWS || newCol < 0 || newCol >= COLS || grid[newRow][newCol] == 'D' || 
        		(newRow == playerRow && newCol == playerCol)) {
            return;
        }

        grid[robotRow][robotCol] = '.';
        robotRow = newRow;
        robotCol = newCol;
        grid[robotRow][robotCol] = 'R';
        moves++;

        if (robotRow == targetRow && robotCol == targetCol) {
            gameWon();
        }
    }

    private void movePlayer(int newRow, int newCol) {
        if (newRow < 0 || newRow >= ROWS || newCol < 0 || newCol >= COLS || grid[newRow][newCol] == 'D' || 
        		(newRow == robotRow && newCol == robotCol)) {
            return;
        }

        grid[playerRow][playerCol] = '.';
        playerRow = newRow;
        playerCol = newCol;
        grid[playerRow][playerCol] = 'P';
        moves++;

        if (playerRow == targetRow && playerCol == targetCol) {
            gameWon();
        }
    }

    private void gameWon() {
        int option = JOptionPane.showOptionDialog(this,
            "축하합니다! " + moves + " 번 만에 도착했네요! \n다시 하시겠습니까?",
            "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
            null, null, null);
        if (option == JOptionPane.YES_OPTION) {
            initializeGrid();
            requestFocusInWindow();
            repaint();
        } else {
            System.exit(0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                Color color;
                switch (grid[i][j]) {
                    case 'R':
                        color = Color.RED;
                        break;
                    case 'P':
                        color = Color.BLUE;
                        break;
                    case 'G':
                        color = Color.GREEN;
                        break;
                    case 'D':
                        color = Color.BLACK;
                        break;
                    default:
                        color = Color.WHITE;
                        break;
                }
                g2d.setColor(color);
                g2d.fillRect(j * (CELL_SIZE + GAP), i * (CELL_SIZE + GAP), CELL_SIZE, CELL_SIZE);
                g2d.setColor(Color.GRAY);
                g2d.drawRect(j * (CELL_SIZE + GAP), i * (CELL_SIZE + GAP), CELL_SIZE, CELL_SIZE);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                moveRobotInDirection(-1, 0);
                break;
            case KeyEvent.VK_DOWN:
                moveRobotInDirection(1, 0);
                break;
            case KeyEvent.VK_LEFT:
                moveRobotInDirection(0, -1);
                break;
            case KeyEvent.VK_RIGHT:
                moveRobotInDirection(0, 1);
                break;
            case KeyEvent.VK_W:
                movePlayerInDirection(-1, 0);
                break;
            case KeyEvent.VK_S:
                movePlayerInDirection(1, 0);
                break;
            case KeyEvent.VK_A:
                movePlayerInDirection(0, -1);
                break;
            case KeyEvent.VK_D:
                movePlayerInDirection(0, 1);
                break;
        }
        repaint();
    }

    private void moveRobotInDirection(int dRow, int dCol) {
        int newRow = robotRow;
        int newCol = robotCol;
        while (true) {
            newRow += dRow;
            newCol += dCol;
            if (newRow < 0 || newRow >= ROWS || newCol < 0 || newCol >= COLS || grid[newRow][newCol] == 'D' || (newRow == playerRow && newCol == playerCol)) {
                break;
            }
            if (grid[newRow][newCol] == 'G') {
                gameWon();
                return;
            }
        }

        if (!(newRow == robotRow && newCol == robotCol)) {
            grid[robotRow][robotCol] = '.';
            robotRow = newRow - dRow;
            robotCol = newCol - dCol;
            grid[robotRow][robotCol] = 'R';
            moves++;
        }
    }

    private void movePlayerInDirection(int dRow, int dCol) {
        int newRow = playerRow;
        int newCol = playerCol;
        while (true) {
            newRow += dRow;
            newCol += dCol;
            if (newRow < 0 || newRow >= ROWS || newCol < 0 || newCol >= COLS || grid[newRow][newCol] == 'D' || (newRow == robotRow && newCol == robotCol)) {
                break;
            }
            if (grid[newRow][newCol] == 'G') {
                gameWon();
                return;
            }
        }

        if (!(newRow == playerRow && newCol == playerCol)) {
            grid[playerRow][playerCol] = '.';
            playerRow = newRow - dRow;
            playerCol = newCol - dCol;
            grid[playerRow][playerCol] = 'P';
            moves++;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void createButtons() {
        JButton retryButton = new JButton("Retry");
        JButton exitButton = new JButton("Exit");

        retryButton.addActionListener(e -> {
            initializeGrid();
            requestFocusInWindow();
            repaint();
        });

        exitButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(exitButton);
            if (window instanceof JFrame) {
                ((JFrame) window).dispose();
            }
        });


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(retryButton);
        buttonPanel.add(exitButton);

        JFrame frame = new JFrame("Robot Game");
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.add(this, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RobotGame::new);
    }
}

