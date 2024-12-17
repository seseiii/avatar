import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.ActionListener; // Add this import



public class Game2 extends JFrame {

    private final RoadMapWindow roadMapWindow;
    private JFrame parentFrame;
    private Image energyIcon;

    static class Card {

        String cardName;
        ImageIcon cardImageIcon;

        Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        public String toString() {
            return cardName;
        }
    }

    String[] cardList = { // Track cardNames
            "pair1", "pair2", "pair3", "pair4", "pair5",
            "pair6", "pair7", "pair8", "pair9", "pair10"
    };

    int rows = 4;
    int columns = 5;
    int cardWidth = 110;
    int cardHeight = 148;

    ArrayList<Card> cardSet; // Create a deck of cards with cardNames and cardImageIcons
    ImageIcon cardBackImageIcon;

    int boardHeight = rows * cardHeight; // 4*148 = 592px

    JFrame frame = new JFrame("Match Cards");
    JLabel errorLabel = new JLabel();
    JLabel scoreLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    int score = 0;
    int lives = 3;
    int errorCount = 0;
    ArrayList<JButton> board;
    Timer hideCardTimer;
    boolean gameReady = false;
    JButton card1Selected;
    JButton card2Selected;


    public Game2(RoadMapWindow roadMapWindow) {
        this.roadMapWindow = roadMapWindow;

        new StartScreen(
                this,
                "src/img/earthmission.png",
                new Color(137, 95, 37),new Runnable() {
                @Override
                public void run() {
                    GameManual gameManual = new GameManual(parentFrame, "",new Color(0, 155, 155),null); // Use the main game frame as the parent
                    gameManual.game2Manual(); // Start the manual
                    gameManual.setVisible(true);
                }
            }
        ).setVisible(true);

        setupCards();
        shuffleCards();

        frame.setVisible(true);
        frame.setLayout(new BorderLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and add the custom background panel
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout()); // Use BorderLayout for the background panel
        frame.add(backgroundPanel, BorderLayout.CENTER);
        energyIcon = new ImageIcon(getClass().getResource("/img/energy.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);

        if (energyIcon == null) {
            System.out.println("Energy icon not loaded correctly!");
        } else {
            System.out.println("Energy icon loaded successfully!");
        }
        // error,lives, score

        errorLabel.setFont(new Font("Arial", Font.PLAIN, 40));
        errorLabel.setText("Errors: " + errorCount);
        errorLabel.setForeground(Color.WHITE); // Set text color to white
        errorLabel.setBounds(8, 140, 300, 30);  // Adjusted position for the second red line

        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 40));
        scoreLabel.setText("Score: " + score);
        scoreLabel.setForeground(Color.WHITE); // Set text color to white
        scoreLabel.setBounds(8, 230, 300, 30);  // Adjusted position for the third red line

        // Set text panel size and position on the right side
        textPanel.setPreferredSize(new Dimension(300, boardHeight));
        textPanel.setOpaque(false);  // Make text panel background transparent
        textPanel.setLayout(null); // Use absolute layout for custom positioning

        // Add labels to the panel
        textPanel.add(errorLabel);  // Error on the second red line
        textPanel.add(scoreLabel);  // Score on the third red line

        backgroundPanel.add(textPanel, BorderLayout.EAST); // Add text panel to the right of the background panel

        // Card game board (on the left side)
        board = new ArrayList<JButton>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        boardPanel.setOpaque(false);


        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight)); // Set size based on card size
            tile.setIcon(cardSet.get(i).cardImageIcon);
            tile.setFocusable(false);
            tile.setBorderPainted(false); // Remove border
            tile.setContentAreaFilled(false); // Make button background transparent
            tile.setOpaque(false); // Make button itself transparent

            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!gameReady) {
                        return;
                    }
                    JButton tile = (JButton) e.getSource();

                    if (tile.getIcon() == cardBackImageIcon) {
                        if (card1Selected == null) {
                            card1Selected = tile;
                            int index = board.indexOf(card1Selected);
                            card1Selected.setIcon(cardSet.get(index).cardImageIcon);
                        } else if (card2Selected == null) {
                            card2Selected = tile;
                            int index = board.indexOf(card2Selected);
                            card2Selected.setIcon(cardSet.get(index).cardImageIcon);

                            if (card1Selected.getIcon() != card2Selected.getIcon()) {
                                errorCount++;
                                errorLabel.setText("Errors: " + errorCount);

                                // Reduce lives after every 2 errors
                                if (errorCount % 3 == 0) {
                                    lives--; // Decrease the lives count

                                    // Update the UI
                                    textPanel.revalidate();
                                    textPanel.repaint();

                                    if (lives == 0) {
                                        MissionFailedDialog dialog = new MissionFailedDialog(Game2.this, roadMapWindow);
                                        dialog.showMissionFailed();
                                        frame.dispose();
                                        roadMapWindow.dispose();
                                        Game2.this.setVisible(false);
                                    }
                                }
                                hideCardTimer.start();
                            } else {
                                score += 5; // Increment score for a match
                                scoreLabel.setText("Score: " + score);

                                card1Selected = null;
                                card2Selected = null;
                            }
                            if (allCardsFaceUp()) {
                                SwingUtilities.invokeLater(() -> {
                                    // Call the MissionCompleteDialog class and pass necessary parameters
                                    MissionCompleteDialog missionDialog = new MissionCompleteDialog(Game2.this, roadMapWindow);
                                    missionDialog.showMissionComplete();
                                    frame.dispose();
                                    Game2.this.setVisible(false); // Hide the current Game2 window
                                    roadMapWindow.unlockGame3();
                                });

                            }
                        }
                    }
                }
            });
            board.add(tile);
            boardPanel.add(tile);
        }

        backgroundPanel.add(boardPanel, BorderLayout.CENTER);

        // Timer to hide all cards after the initial 4-second delay
        Timer initialHideCardTimer = new Timer(4000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards(); // Hide all cards
            }
        });
        initialHideCardTimer.setRepeats(false);
        initialHideCardTimer.start(); // Start the initial timer

        // Timer to hide mismatched cards after 1.5 seconds
        hideCardTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards(); // Hide only mismatched cards
            }
        });
        hideCardTimer.setRepeats(false);
    }


    void setupCards() {
        cardSet = new ArrayList<Card>();
        for (String cardName : cardList) {
            // Load each card image
            Image cardImg = new ImageIcon("src/img/" + cardName + ".png").getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));

            // Create card object and add to cardSet
            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        }
        cardSet.addAll(cardSet);

        // Load the back card image
        Image cardBackImg = new ImageIcon("src/img/cardcover.png").getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));

    }

    void shuffleCards() {
        System.out.println(cardSet);
        // Shuffle
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size()); // Get random index
            // Swap
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
        System.out.println(cardSet);
    }


    void hideCards() {
        if (gameReady && card1Selected != null && card2Selected != null) { // Only flip 2 cards
            card1Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected.setIcon(cardBackImageIcon);
            card2Selected = null;
        } else { // Flip all cards face down
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardBackImageIcon);
            }
            gameReady = true;
        }
    }

    private boolean allCardsFaceUp() {
        for (JButton card : board) {
            if (card.getIcon() == cardBackImageIcon) {
                return false; // Found a card that is still face down
            }
        }
        return true; // All cards are face up
    }

    // Custom JPanel class to display background image
    class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        public BackgroundPanel() {
            // Load the image (adjust the path to your image file)
            backgroundImage = new ImageIcon("src/img/earth_temple.png").getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the background image, scaling it to fill the panel
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            // Draw energy icons at the top
            // Draw energy icons with larger size
            // Draw enlarged energy icons with original spacing
            g.setFont(new Font("Arial", Font.PLAIN, 42));
            g.setColor(Color.WHITE); // Ensure the text is visible
            for (int i = 0; i < lives; i++) {
                int x = 1105 + (i * 45);
                int y = 50;
                g.drawString("Lives: " , 990, 80);
                g.drawImage(energyIcon, x, y, 40, 40, this); // Enlarged icon: 40x40 size
            }
        }
    }
}