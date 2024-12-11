import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class CreateAccountWindow extends JFrame {

    private Image backgroundImage;

    public CreateAccountWindow() {
        setTitle("Create Account");

        setSize(Toolkit.getDefaultToolkit().getScreenSize()); // Set to full-screen size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Disable resizing
        setResizable(false);

        // Set the frame to full-screen mode
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Load the background image
        ImageIcon icon = new ImageIcon("src/createaccbg.png"); // Use your uploaded image
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
        JLabel firstNameLabel = new JLabel("First Name");
        firstNameLabel.setBounds(346, 269, 200, 30);
        firstNameLabel.setForeground(Color.WHITE);
        firstNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        backgroundPanel.add(firstNameLabel);

        JTextField firstNameField = new JTextField();
        firstNameField.setBounds(484, 270, 222, 30);
        firstNameField.setFont(new Font("Arial", Font.PLAIN, 16));
        backgroundPanel.add(firstNameField);

        JLabel firstNameStatusLabel = new JLabel();
        firstNameStatusLabel.setBounds(484, 300, 400, 30);
        firstNameStatusLabel.setForeground(new Color(139, 0, 0)); // Dark red
        firstNameStatusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        backgroundPanel.add(firstNameStatusLabel);

        JLabel lastNameLabel = new JLabel("Last Name");
        lastNameLabel.setBounds(844, 269, 200, 30);
        lastNameLabel.setForeground(Color.WHITE);
        lastNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        backgroundPanel.add(lastNameLabel);

        JTextField lastNameField = new JTextField();
        lastNameField.setBounds(971, 270, 222, 30);
        lastNameField.setFont(new Font("Arial", Font.PLAIN, 16));
        backgroundPanel.add(lastNameField);

        JLabel lastNameStatusLabel = new JLabel();
        lastNameStatusLabel.setBounds(971, 300, 400, 30);
        lastNameStatusLabel.setForeground(new Color(139, 0, 0)); // Dark red
        lastNameStatusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        backgroundPanel.add(lastNameStatusLabel);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(346, 387, 200, 30);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        backgroundPanel.add(usernameLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(484, 388, 222, 30);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        backgroundPanel.add(usernameField);

        JLabel usernameStatusLabel = new JLabel();
        usernameStatusLabel.setBounds(484, 418, 400, 30);
        usernameStatusLabel.setForeground(new Color(139, 0, 0)); // Dark red
        usernameStatusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        backgroundPanel.add(usernameStatusLabel);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(346, 516, 200, 30);
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 18));
        backgroundPanel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(484, 517, 222, 30);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setEnabled(false);
        backgroundPanel.add(passwordField);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password");
        confirmPasswordLabel.setBounds(781, 516, 200, 30);
        confirmPasswordLabel.setForeground(Color.WHITE);
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 18));
        backgroundPanel.add(confirmPasswordLabel);

        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(971, 517, 222, 30);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 16));
        confirmPasswordField.setEnabled(false);
        backgroundPanel.add(confirmPasswordField);

        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setBounds(482, 649, 200, 40);
        createAccountButton.setBackground(new Color(101, 67, 33)); // Dark brown
        createAccountButton.setForeground(Color.WHITE); // Set text to white
        createAccountButton.setFont(new Font("Arial", Font.BOLD, 18));
        createAccountButton.setBorderPainted(false);
        createAccountButton.setFocusPainted(false);
        createAccountButton.setEnabled(false);
        backgroundPanel.add(createAccountButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(912, 649, 150, 40);
        backButton.setBackground(new Color(101, 67, 33)); // Dark brown
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backgroundPanel.add(backButton);


        // Add Document Listener for dynamic username validation
        usernameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateUsername();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateUsername();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateUsername();
            }

            private void validateUsername() {
                String username = usernameField.getText().trim();
                if (!username.isEmpty() && AccountManager.isUsernameTaken(username)) {
                    usernameStatusLabel.setText("Username already exists. Please choose another.");
                    passwordField.setEnabled(false);
                } else if (!username.isEmpty()) {
                    usernameStatusLabel.setText("");
                    passwordField.setEnabled(true);
                }
            }
        });

        // Enable fields dynamically for skipped fields
        DocumentListener fieldEnablerListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                enableFields();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                enableFields();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                enableFields();
            }

            private void enableFields() {
                boolean firstNameValid = !firstNameField.getText().trim().isEmpty();
                boolean lastNameValid = !lastNameField.getText().trim().isEmpty();
                boolean usernameValid = !usernameField.getText().trim().isEmpty() && !AccountManager.isUsernameTaken(usernameField.getText().trim());
                boolean passwordValid = !new String(passwordField.getPassword()).trim().isEmpty();
                boolean confirmPasswordValid = !new String(confirmPasswordField.getPassword()).trim().isEmpty();

                passwordField.setEnabled(firstNameValid && lastNameValid && usernameValid);
                confirmPasswordField.setEnabled(passwordValid);
                createAccountButton.setEnabled(confirmPasswordValid);
            }
        };

        firstNameField.getDocument().addDocumentListener(fieldEnablerListener);
        lastNameField.getDocument().addDocumentListener(fieldEnablerListener);
        passwordField.getDocument().addDocumentListener(fieldEnablerListener);
        confirmPasswordField.getDocument().addDocumentListener(fieldEnablerListener);

        // Add Focus Listeners for skipped fields
        firstNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (firstNameField.getText().trim().isEmpty()) {
                    firstNameStatusLabel.setText("First name cannot be empty.");
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
                firstNameStatusLabel.setText("");
            }
        });

        lastNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (lastNameField.getText().trim().isEmpty()) {
                    lastNameStatusLabel.setText("Last name cannot be empty.");
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
                lastNameStatusLabel.setText("");
            }
        });

        // Arrow navigation remains unchanged
        JComponent[] components = {
                firstNameField, lastNameField, usernameField, passwordField, confirmPasswordField, createAccountButton
        };

        for (int i = 0; i < components.length; i++) {
            int currentIndex = i;

            InputMap inputMap = components[i].getInputMap(JComponent.WHEN_FOCUSED);
            ActionMap actionMap = components[i].getActionMap();

            inputMap.put(KeyStroke.getKeyStroke("DOWN"), "next");
            inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "next");
            actionMap.put("next", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    components[(currentIndex + 1) % components.length].requestFocusInWindow();
                }
            });

            inputMap.put(KeyStroke.getKeyStroke("UP"), "previous");
            inputMap.put(KeyStroke.getKeyStroke("LEFT"), "previous");
            actionMap.put("previous", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    components[(currentIndex - 1 + components.length) % components.length].requestFocusInWindow();
                }
            });
        }

        backButton.addActionListener(e -> {
            new LoginWindow();
            dispose();
        });

        createAccountButton.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AccountManager.saveAccount(firstName, lastName, username, password);
            JOptionPane.showMessageDialog(this, "Account Created Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginWindow();
            dispose();
        });

        setVisible(true);
    }
}