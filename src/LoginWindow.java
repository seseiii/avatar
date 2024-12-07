import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginWindow extends JFrame {

    private final Image backgroundImage;
    private JProgressBar progressBar;

    public LoginWindow() {

        setSize(Toolkit.getDefaultToolkit().getScreenSize()); // Set to full-screen size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Disable resizing
        setResizable(false);

        // Set the frame to full-screen mode
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Load the background image
        ImageIcon icon = new ImageIcon("src/img/login.png");
        backgroundImage = icon.getImage();

        // Create a custom panel for the background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel); // Set as content pane for the frame

        // Add UI Components
        JLabel userLabel = new JLabel("Username");
        userLabel.setBounds( 770, 300, 200, 40);
        userLabel.setForeground(new Color(101, 67, 33));
        userLabel.setFont(new Font("Arial", Font.BOLD, 18));
        backgroundPanel.add(userLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(880, 300, 250, 40);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        backgroundPanel.add(usernameField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setBounds(770, 420, 200, 40);
        passLabel.setForeground(new Color(101, 67, 33));
        passLabel.setFont(new Font("Arial", Font.BOLD, 18));
        backgroundPanel.add(passLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(880, 420, 250, 40);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        backgroundPanel.add(passwordField);

        // Key Bindings for Arrow Navigation
        InputMap usernameInputMap = usernameField.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap usernameActionMap = usernameField.getActionMap();

        InputMap passwordInputMap = passwordField.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap passwordActionMap = passwordField.getActionMap();

        // Arrow Down on usernameField -> Move to passwordField
        usernameInputMap.put(KeyStroke.getKeyStroke("DOWN"), "moveToPassword");
        usernameActionMap.put("moveToPassword", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwordField.requestFocusInWindow();
            }
        });

        // Arrow Up on passwordField -> Move to usernameField
        passwordInputMap.put(KeyStroke.getKeyStroke("UP"), "moveToUsername");
        passwordActionMap.put("moveToUsername", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usernameField.requestFocusInWindow();
            }
        });

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(760, 525, 150, 40);
        loginButton.setBackground(new Color(181, 101, 29));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        backgroundPanel.add(loginButton);

        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(139, 69, 19));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(181, 101, 29));
            }
        });

        ActionListener loginAction = e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (AccountManager.isValidAccount(username, password)) {
                showLoadingScreen(); // Show loading animation
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials! Please try again.");
            }
        };

        // Add the same action listener to both text fields and the login button
        usernameField.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);
        loginButton.addActionListener(loginAction);

        // Create Account Button
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setBounds(950, 525, 200, 40);
        createAccountButton.setBackground(new Color(181, 101, 29));
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.setFont(new Font("Arial", Font.BOLD, 18));
        createAccountButton.setBorderPainted(false);
        createAccountButton.setFocusPainted(false);
        backgroundPanel.add(createAccountButton);

        createAccountButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                createAccountButton.setBackground(new Color(139, 69, 19));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                createAccountButton.setBackground(new Color(181, 101, 29));
            }
        });

        createAccountButton.addActionListener(e -> {
            //new CreateAccountWindow(); // Assuming this class exists
            dispose();
        });

        setVisible(true);
    }

    private void showLoadingScreen() {
        // Create a loading panel
        JPanel loadingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon loadingIcon = new ImageIcon("src/img/loading.jpg");
                g.drawImage(loadingIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 10));
                String loadingText = (progressBar.getValue() == 100) ? "Completed" : "Loading...";
                int textWidth = g.getFontMetrics().stringWidth(loadingText);
                int x = (getWidth() - textWidth) / 2;
                int y = getHeight() - 30;
                g.drawString(loadingText, x, y);
            }
        };

        loadingPanel.setLayout(null);
        loadingPanel.setBounds(0, 0, 300, 200);
        add(loadingPanel);

        // Configure progress bar
        progressBar = new JProgressBar();
        progressBar.setBounds(0, 190, 300, 10);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        loadingPanel.add(progressBar);

        // Create a loading frame
        JFrame loadingFrame = new JFrame();
        loadingFrame.setSize(300, 200);
        loadingFrame.setLocationRelativeTo(null);
        loadingFrame.setUndecorated(true);
        loadingFrame.setLayout(null);
        loadingFrame.add(loadingPanel);
        loadingFrame.setVisible(true);

        // Start loading animation
        Timer timer = new Timer(100, new ActionListener() {
            int percentage = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                percentage += 2;
                if (percentage > 100) {
                    percentage = 100;
                }
                progressBar.setValue(percentage);
                loadingPanel.repaint();
                if (percentage == 100) {
                    ((Timer) e.getSource()).stop();
                    progressBar.setVisible(false);
                    SwingUtilities.invokeLater(() -> {
                        // Open StoryWindow
                        new StoryWindow().setVisible(true);

                        // Dispose LoginWindow and loading frame
                        dispose(); // Dispose of LoginWindow
                        loadingFrame.dispose();
                    });
                }
            }
        });
        timer.start();
    }

}