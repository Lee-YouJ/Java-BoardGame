import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MiniGameGUI extends JFrame {
    private Random random = new Random();
    private int randomNumber;
    private JTextField guessField;
    private JLabel resultLabel;
    private int attempts;

    public MiniGameGUI() {
        setTitle("숫자 맞추기 미니게임");
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        JLabel titleLabel = new JLabel("1부터 100 사이의 숫자를 맞춰보세요.");
        titleLabel.setFont(new Font("맑은고딕", Font.BOLD, 18));
        panel.add(titleLabel);

        guessField = new JTextField();
        guessField.setFont(new Font("맑은고딕", Font.PLAIN, 16));
        panel.add(guessField);

        JButton guessButton = new JButton("추측");
        guessButton.setFont(new Font("맑은고딕", Font.BOLD, 15));
        guessButton.addActionListener(new GuessButtonListener());
        panel.add(guessButton);

        resultLabel = new JLabel();
        panel.add(resultLabel);

        add(panel);
        generateRandomNumber();
    }

    private void generateRandomNumber() {
        randomNumber = random.nextInt(100) + 1;
    }

    private void checkGuess() {
        try {
            int guessedNumber = Integer.parseInt(guessField.getText());
            attempts++;
            if (guessedNumber > randomNumber) {
                resultLabel.setText("추측한 숫자가 더 큽니다.");
            } else if (guessedNumber < randomNumber) {
                resultLabel.setText("추측한 숫자가 더 작습니다.");
            } else {
                resultLabel.setText("축하합니다! 숫자를 맞췄습니다.\n시도한 횟수: " + attempts);
                guessField.setEnabled(false);
            }
        } catch (NumberFormatException e) {
            resultLabel.setText("올바른 숫자를 입력하세요.");
        }
    }

    private class GuessButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            checkGuess();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MiniGameGUI().setVisible(true));
    }
}