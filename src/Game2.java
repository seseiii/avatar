import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;


public class Game2 extends JFrame {

    private final RoadMapWindow roadMapWindow;

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
    ArrayList<JLabel> lifeIcons = new ArrayList<>();
    private Image energyIcon;

    int boardHeight = rows * cardHeight; // 4*148 = 592px

    JFrame frame = new JFrame("Match Cards");
    JLabel errorLabel = new JLabel();
    JLabel scoreLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    int lives = 3;
    int score = 0;
    int errorCount = 0;
    ArrayList<JButton> board;
    Timer hideCardTimer;
    Timer InitialHideCard;

    boolean gameReady = false;
    JButton card1Selected;
    JButton card2Selected;


    public Game2(RoadMapWindow roadMapWindow) {
        this.roadMapWindow = roadMapWindow;

        new StartScreen(
                this,
                "src/img/earthmission.png",
                new Color(137, 95, 37),
                null
        ).setVisible(true);
        showGameManual();

        setupCards();
        shuffleCards();
        InitialHideCardTimer();

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

        // error,lives, score
        for (int i = 0; i < lives; i++) {

            JLabel textLabel = new JLabel("Lives: ");
            textLabel.setFont(new Font("Arial", Font.PLAIN, 30)); // Set font and size for the text
            textLabel.setForeground(Color.WHITE); // Set the text color to white
            textLabel.setBounds(8, 50, 300, 30); // Position the text label (adjust size and position)
            textPanel.add(textLabel);  // Add the text label to the panel

            JLabel lifeIconLabel = new JLabel(new ImageIcon(energyIcon));
            int xPosition = 100 + (i * 40); // Adjust the 40 to control the spacing between icons
            lifeIconLabel.setBounds(xPosition, 50, 30, 30); // Fixed y, variable x to position horizontally            lifeIcons.add(lifeIconLabel);
            textPanel.add(lifeIconLabel);  // Add life label to the panel
        }

        errorLabel.setFont(new Font("Arial", Font.PLAIN, 40));
        errorLabel.setText("Errors: " + errorCount);
        errorLabel.setForeground(Color.WHITE); // Set text color to white
        errorLabel.setBounds(8, 120, 300, 30);  // Adjusted position for the second red line

        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 40));
        scoreLabel.setText("Score: " + score);
        scoreLabel.setForeground(Color.WHITE); // Set text color to white
        scoreLabel.setBounds(8, 200, 300, 30);  // Adjusted position for the third red line

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

                                    // Remove the last life icon from the list and the UI
                                    if (!lifeIcons.isEmpty()) {
                                        JLabel lifeToRemove = lifeIcons.removeLast(); // Remove the last life
                                        textPanel.remove(lifeToRemove); // Remove it from the UI
                                    }

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

        // Create timer to hide cards after a delay (used for flipping cards back)
        hideCardTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });
        hideCardTimer.setRepeats(false);
        hideCardTimer.start();
    }

    void InitialHideCardTimer(){
        InitialHideCard = new Timer(4000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });
        InitialHideCard.setRepeats(false);
        InitialHideCard.start();
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

        //energyIcon image lives
        Image energyIconImage = new ImageIcon("src/img/energy.png").getImage();
        if (energyIconImage == null) {
            System.out.println("Energy icon image not found.");
        } else {
            energyIcon = energyIconImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        }
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

    void showGameManual(){
        JFrame parentFrame = new JFrame("Game Manual");
        GameManual gameManual = new GameManual(parentFrame, "");
        gameManual.game2Manual();
        gameManual.setVisible(true);
    }

    void skipManual(){
        JFrame parentFrame = new JFrame("Game Manual");
        GameManual gameManual = new GameManual(parentFrame, "");
        gameManual.setVisible(false);
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
        }
    }
}