import javax.swing.*;

import team.FormationGame;
import team.RobotGame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// JFrame을 상속받아 GameLauncher 클래스를 정의, 이 클래스는 윈도우를 생성합니다.
public class GameLauncher extends JFrame {
   // 클래스 생성자
    public GameLauncher() {
        setTitle("머글와트"); //윈도우의 제목을 설정합니다.
        setSize(400, 300); //윈도우의 크기를 설정합니다.
        setLocationRelativeTo(null); // 창을 화면 중앙에 배치 합니다.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 윈도우 닫기 버튼을 누르면 프로그램이 종료 되도록 하는 코드 입니다.
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("머글와트", SwingConstants.CENTER);
        titleLabel.setFont(new Font("바탕체", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH); //레이블을 윈도우 상단(북쪽)에 추가

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1));

        JButton formationButton = new JButton("Formation");
        formationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FormationGame().setVisible(true);
            }
        });

        JButton QuoridorButton = new JButton("Quoridor");
        QuoridorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new QuoridorGame().setVisible(true);
            }
        });
      
        JButton miniGameButton = new JButton("MiniGame");
              miniGameButton.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                      new MiniGameGUI().setVisible(true);
                  }
              });

        JButton RobotGameButton = new JButton("미로게임");
        RobotGameButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
              new RobotGame().setVisible(true);
           }
        });
        

        

        buttonPanel.add(formationButton);
        buttonPanel.add(QuoridorButton);
        buttonPanel.add(miniGameButton);
        buttonPanel.add(RobotGameButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new GameLauncher().setVisible(true);
        });
    }
}