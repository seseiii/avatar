import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;

public class Game3 extends JPanel implements ActionListener, KeyListener {

    private static final long serialVersionUID = 1L;
    private JFrame parentFrame;
    private final RoadMapWindow roadMapWindow;
    private Image energyIcon;

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U'; // U D L R
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize / 4;
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize / 4;
            } else if (this.direction == 'L') {
                this.velocityX = -tileSize / 4;
                this.velocityY = 0;
            } else if (this.direction == 'R') {
                this.velocityX = tileSize / 4;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private final int rowCount = 21;
    private final int columnCount = 19;
    private final int tileSize = 32;

    private final Image wallImage;
    private final Image lordOzaiImage;
    private final Image zukoImage;
    private final Image princessAzulaImage;
    private final Image kuviraImage;
    private final Image goldCoinImage;

    private final Image avatarUpImage;
    private final Image avatarDownImage;
    private final Image avatarLeftImage;
    private final Image avatarRightImage;

    private final String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "X       bpo       X",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    private Image backgroundImage;

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    public Game3(RoadMapWindow roadMapWindow) {
        this.roadMapWindow = roadMapWindow;
        JFrame frame = new JFrame("Fire Element Challenge");

        goldCoinImage = new ImageIcon("src/img/powerFood.png").getImage();
        backgroundImage = new ImageIcon("src/img/bgfire.png").getImage();

        setLayout(new BorderLayout());
        addKeyListener(this);
        setFocusable(true);

        frame.setLayout(new BorderLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);

        wallImage = new ImageIcon(getClass().getResource("/img/wall.jpg")).getImage();
        lordOzaiImage = new ImageIcon(getClass().getResource("/img/lordOzai.png")).getImage();
        zukoImage = new ImageIcon(getClass().getResource("/img/zuko.png")).getImage();
        princessAzulaImage = new ImageIcon(getClass().getResource("/img/princessAzula.png")).getImage();
        kuviraImage = new ImageIcon(getClass().getResource("/img/kuvira.png")).getImage();

        avatarUpImage = new ImageIcon(getClass().getResource("/img/up.png")).getImage();
        avatarDownImage = new ImageIcon(getClass().getResource("/img/down.png")).getImage();
        avatarLeftImage = new ImageIcon(getClass().getResource("/img/left.png")).getImage();
        avatarRightImage = new ImageIcon(getClass().getResource("/img/right.png")).getImage();
        energyIcon = new ImageIcon(getClass().getResource("/img/energy.png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);

        if (energyIcon == null) {
            System.out.println("Energy icon not loaded correctly!");
        } else {
            System.out.println("Energy icon loaded successfully!");
        }

        new StartScreen(
                frame,
                "src/img/fireMission.png",
                new Color(124, 15, 15),
                new Runnable() {
                    @Override
                    public void run() {
                        GameManual gameManual = new GameManual(parentFrame, "",new Color(0, 155, 155),null); // Use the main game frame as the parent
                        gameManual.game3Manual(); // Start the manual
                        gameManual.setVisible(true);
                    }
                }
        ).setVisible(true);

        startGame();
        /*loadMap();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        gameLoop = new Timer(60, this); // 20fps
        gameLoop.start();*/
        frame.setVisible(true);
    }

    private void startGame() {
        loadMap();
        resetPositions();
        score = 0;
        gameOver = false;
        if (gameLoop != null) {
            gameLoop.stop();
        }
        gameLoop = new Timer(60, e -> {
            move();
            repaint();
            if (gameOver) {
                gameLoop.stop();
            }
        });
        gameLoop.setRepeats(true);
        gameLoop.start();
    }

    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tileMapChar = tileMap[r].charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;

                switch (tileMapChar) {
                    case 'X':
                        walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                        break;
                    case 'b':
                        ghosts.add(new Block(lordOzaiImage, x, y, tileSize, tileSize));
                        break;
                    case 'o':
                        ghosts.add(new Block(zukoImage, x, y, tileSize, tileSize));
                        break;
                    case 'p':
                        ghosts.add(new Block(princessAzulaImage, x, y, tileSize, tileSize));
                        break;
                    case 'r':
                        ghosts.add(new Block(kuviraImage, x, y, tileSize, tileSize));
                        break;
                    case 'P':
                        pacman = new Block(avatarRightImage, x, y, tileSize, tileSize);
                        break;
                    case ' ':
                        foods.add(new Block(goldCoinImage, x + 14, y + 14, 6, 6));
                        break;
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {


        // Rest of the game drawing logic
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        int mapWidth = columnCount * tileSize;
        int mapHeight = rowCount * tileSize;
        int xOffset = (getWidth() - mapWidth) / 7;
        int yOffset = (getHeight() - mapHeight) / 7;

        // Draw energy icons at the top
        // Draw energy icons with larger size
        // Draw enlarged energy icons with original spacing
        g.setFont(new Font("Arial", Font.PLAIN, 70));
        g.setColor(Color.WHITE); // Ensure the text is visible
        for (int i = 0; i < lives; i++) {
            int x = 990 + (i * 80); // Increase spacing to 55 pixels
            int y = 75;            // Position icons slightly below the text
            g.drawString("Lives: " , 775, 125);
            g.drawImage(energyIcon, x, y, 60, 60, this); // Enlarged icon: 40x40 size
        }



        g.drawImage(pacman.image, pacman.x + xOffset, pacman.y + yOffset, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x + xOffset, ghost.y + yOffset, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x + xOffset, wall.y + yOffset, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.drawImage(food.image, food.x + xOffset, food.y + yOffset, food.width, food.height, null);
        }

        g.setFont(new Font("Arial", Font.PLAIN, 70));
        g.setColor(Color.WHITE); // Ensure the text is visible
        if (gameOver) {
            g.drawString("Score: " + score, 775, 275); // Adjusted position for score and lives
//            g.drawString("", 830, 100); // Adjusted position for score and lives
        } else {

//            g.drawString("Lives: " + GameOverDialog.getEnergy(), 990, 100); // Adjusted position for score and lives
            g.drawString("Score: " + score, 775, 275); // Adjusted position for score and lives
        }
    }


    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        for (Block ghost : ghosts) {
            if (collision(ghost, pacman)) {
                gameOver = true;

                lives--;
                repaint();

                GameOverDialog.handleGameOver(SwingUtilities.getWindowAncestor(this), this::startGame);
                return;
            }

            if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    ghost.updateDirection(directions[random.nextInt(4)]);
                }
            }
        }

        foods.removeIf(food -> {
            if (collision(pacman, food)) {
                score++;
                return true;
            }
            return false;
        });
        if (score == 50 && !gameOver) {
            gameOver = true;


            MissionCompleteDialog missionDialog = new MissionCompleteDialog(null, roadMapWindow);
            missionDialog.showMissionComplete();
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.dispose();
            }
            this.setVisible(false);
            roadMapWindow.unlockGame4();


        }
    }

    public boolean collision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.updateDirection(directions[random.nextInt(4)]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }
        // System.out.println("KeyEvent: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }

        if (pacman.direction == 'U') {
            pacman.image = avatarUpImage;
        }
        else if (pacman.direction == 'D') {
            pacman.image = avatarDownImage;
        }
        else if (pacman.direction == 'L') {
            pacman.image = avatarLeftImage;
        }
        else if (pacman.direction == 'R') {
            pacman.image = avatarRightImage;
        }
    }
}