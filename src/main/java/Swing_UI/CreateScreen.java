package Swing_UI;

import javax.swing.*;
import java.awt.*;

public class CreateScreen extends JFrame {

    private static final long serialVersionUID = 1L;
    private JComboBox<String> optionCombo;

    public CreateScreen() {
        setTitle("METAL HMI");
        setSize(1366, 768); // Manually set to a common laptop resolution
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setLayout(new BorderLayout());

        // Main vertical panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
       
        // Title Label
        JLabel welcome = new JLabel("Welcome to Primetals Technologies!");
        JLabel welcome2 = new JLabel("METAL MANAGEMENT SYSTEM");
        welcome.setFont(new Font("Arial", Font.BOLD, 20));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcome2.setFont(new Font("Arial", Font.BOLD, 15));
        welcome2.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(welcome);
        mainPanel.add(welcome2);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer

        // ComboBox + Button Panel
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        optionCombo = new JComboBox<>(new String[]{"CREATE", "READ", "UPDATE", "DELETE"});
        comboPanel.add(optionCombo);
        JButton goBtn = new JButton("GO");
        comboPanel.add(goBtn);
        mainPanel.add(comboPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        

        // Image Panel
        ImageIcon imageIcon = new ImageIcon("src/main/resources/assets/steelPlant.jpg");
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(imageLabel);

        // Add main panel to frame
        add(mainPanel, BorderLayout.NORTH);

        // Button Action
        goBtn.addActionListener(e -> openSelectedScreen());

        setVisible(true);
    }

    private void openSelectedScreen() {
        String selected = (String) optionCombo.getSelectedItem();
        switch (selected) {
            case "CREATE":
                new Create();
                break;
            case "READ":
                new Read();
                break;
            case "UPDATE":
                new Update();
                break;
            case "DELETE":
                new Delete();
                break;
        }
    }

    public static void main(String[] args) {
        new CreateScreen();
    }
}