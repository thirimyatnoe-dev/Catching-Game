import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.net.URL;

public class CatchingGame extends JFrame {
    private GamePanel gamePanel;
    private Timer timer;
    private JButton startButton;

    public CatchingGame() {
        setTitle("Catching Game");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the start button
        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Merriweather", Font.BOLD, 24));
        startButton.addActionListener(e -> startGame());

        // Initial start screen text
        JLabel startText = new JLabel("Click Start to feed Fluffy", SwingConstants.CENTER);
        startText.setFont(new Font("Merriweather", Font.BOLD, 30));
        startText.setForeground(Color.BLACK);

        // Center alignment for start screen
        JPanel startPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        //to print the screen text of the first page in the center
        // Add start text
        gbc.gridx = 0;
        gbc.gridy = 0;
        startPanel.add(startText, gbc);

        // Add start button
        gbc.gridy = 1;
        startPanel.add(startButton, gbc);

        startPanel.setOpaque(false);

        gamePanel = new GamePanel(this);
        gamePanel.setLayout(new BorderLayout());
        gamePanel.add(startPanel, BorderLayout.CENTER);
        add(gamePanel);

        // Game timer
        timer = new Timer(10, e -> {
            gamePanel.updateGame();
            if (gamePanel.isGameOver()) {
                timer.stop();
                showFluffyGoneMessage();
            }
        });
    }

    private void startGame() {
        gamePanel.resetGame();
        gamePanel.removeAll();
        gamePanel.revalidate();
        gamePanel.repaint();

        timer.start();

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                gamePanel.startDrag(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                gamePanel.stopDrag();
            }
        });
        gamePanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                gamePanel.dragPlayer(e.getX(), e.getY());
            }
        });

        gamePanel.startSpawningObjects();
    }

    public void showFluffyGoneMessage() {
        gamePanel.setFluffyGoneMessage(true);
        gamePanel.repaint();
//to delay the screen text(giving time for the read the text) before the game over box appears
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws InterruptedException {
                Thread.sleep(2000);
                return null;
            }

            @Override
            protected void done() {
                gamePanel.setFluffyGoneMessage(false);
                gamePanel.repaint();
                JOptionPane.showMessageDialog(null, "Game Over");
                gamePanel.resetGame();
                showStartScreen();
            }
        };
        worker.execute();
    }

    public void showStartScreen() {
        gamePanel.removeAll();
        gamePanel.revalidate();
        gamePanel.repaint();

        JLabel startText = new JLabel("Click Start to feed Fluffy", SwingConstants.CENTER);
        startText.setFont(new Font("Merriweather", Font.BOLD, 30));
        startText.setForeground(Color.BLACK);

        JPanel startPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Add start text
        gbc.gridx = 0;
        gbc.gridy = 0;
        startPanel.add(startText, gbc);

        // Add start button
        gbc.gridy = 1;
        startPanel.add(startButton, gbc);

        startPanel.setOpaque(false);
        gamePanel.add(startPanel, BorderLayout.CENTER);
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CatchingGame().setVisible(true));
    }
}

class GamePanel extends JPanel {
    private int playerX = 375;
    private int playerY = 640; // Lowered the player position
    private int playerWidth = 70;
    private int playerHeight = 60;
    private ArrayList<FallingObject> fallingObjects;
    private Random random;
    private boolean dragging = false;
    private int offsetX, offsetY;
    private boolean gameOver = false;
    private int score = 0;
    private int speed = 2;
    private int lives = 3;
    private boolean fluffyGoneMessageDisplayed = false;
    private Timer spawnTimer;
    private BufferedImage backgroundImage;
    private BufferedImage playerImage;
    private BufferedImage[] fallingImages = new BufferedImage[5];
    private CatchingGame mainGame;
    private String warningMessage = "";
    private Timer messageTimer;

    public GamePanel(CatchingGame mainGame) {
        this.mainGame = mainGame;
        fallingObjects = new ArrayList<>();
        random = new Random();
        loadBackgroundImage();
        loadPlayerImage();
        loadFallingImages();
    }

    private void loadBackgroundImage() {
        try{

            URL imageUrl = new URL("https://i.ibb.co/HgfYXkV/bgimage.jpg");
            backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //error handling
    private void loadPlayerImage() {
        try {

            URL imageUrl = new URL("https://i.ibb.co/NKKtfxk/playericon.jpg");
            playerImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //error handling
    private void loadFallingImages() {
        String[] imageNames = {"https://i.ibb.co/Zmv5SCX/steak.png", "https://i.ibb.co/R4R7y7t/cookie.png", "https://i.ibb.co/r68LXc1/sushi.png", "https://i.ibb.co/sJzFGwT/watermelon.png", "https://i.ibb.co/Lzrf7FC/broccoli.png"};
        for (int i = 0; i < imageNames.length; i++) {
            try {
                URL imageUrl = new URL(imageNames[i]);
                fallingImages[i] = ImageIO.read(imageUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//timer for falling objects
    public void startSpawningObjects() {
        spawnTimer = new Timer(1000, e -> spawnObject());
        spawnTimer.start();
    }

    private void spawnObject() {
        int x = random.nextInt(750);
        int imageIndex = random.nextInt(fallingImages.length);
        fallingObjects.add(new FallingObject(x, 0, imageIndex));
    }

    public void updateGame() {
        Iterator<FallingObject> iterator = fallingObjects.iterator();
        while (iterator.hasNext()) {
            FallingObject obj = iterator.next();
            obj.y += speed;
//tracking the collision
            if (obj.getBounds().intersects(new Rectangle(playerX, playerY, playerWidth, playerHeight))) {
                iterator.remove();
                score++;
                if (score % 10 == 0) speed++;
            } else if (obj.y >= playerY + playerHeight) {   //making the missing food to be counted right after it fell past the player instead of when the food reaches the bottom of the screen
                iterator.remove();
                lives--;

                if (lives == 2) {
                    showWarningMessage("You missed one. Don't make Fluffy Hungry.");
                } else if (lives == 1) {
                    showWarningMessage("Fluffy is starving. Don't miss again.");
                }

                if (lives <= 0) {
                    gameOver = true;
                    spawnTimer.stop();
                }
            }
        }
        repaint();
    }

    private void showWarningMessage(String message) {
        warningMessage = message;

        if (messageTimer != null && messageTimer.isRunning()) {
            messageTimer.stop();
        }
        messageTimer = new Timer(2000, e -> {
            warningMessage = "";
            repaint();
        });
        messageTimer.setRepeats(false);
        messageTimer.start();

        repaint();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void resetGame() {
        playerX = 375;
        playerY = 640;
        fallingObjects.clear();
        score = 0;
        speed = 2;
        lives = 3;
        gameOver = false;
        if (spawnTimer != null) spawnTimer.stop();
    }

    public void setFluffyGoneMessage(boolean show) {
        this.fluffyGoneMessageDisplayed = show;
    }

    public void startDrag(int mouseX, int mouseY) {
        if (mouseX >= playerX && mouseX <= playerX + playerWidth && mouseY >= playerY && mouseY <= playerY + playerHeight) {
            dragging = true;
            offsetX = mouseX - playerX;
        }
    }

    public void stopDrag() {
        dragging = false;
    }

    public void dragPlayer(int mouseX, int mouseY) {
        if (dragging) {
            playerX = mouseX - offsetX;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        if (playerImage != null) {
            g2d.drawImage(playerImage.getScaledInstance(playerWidth, playerHeight, Image.SCALE_SMOOTH), playerX, playerY, this);
        }

        for (FallingObject obj : fallingObjects) {
            g2d.drawImage(fallingImages[obj.imageIndex], obj.x, obj.y, 50, 50, this);
        }

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Merriweather", Font.BOLD, 24));
        g2d.drawString("Score: " + score, getWidth() - 180, 40);

        if (!warningMessage.isEmpty()) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Merriweather", Font.BOLD, 20));
            g2d.drawString(warningMessage, getWidth() / 2 - 200, getHeight() / 2 - 50);
        }

        if (fluffyGoneMessageDisplayed) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Merriweather", Font.BOLD, 30));
            g2d.drawString("Fluffy is gone. You don't deserve Fluffy.", getWidth() / 2 - 250, getHeight() / 2);
        }
//for the lives three circles
        for (int i = 0; i < lives; i++) {
            g2d.fillOval(20 + i * 40, 20, 30, 30);
        }
    }

    private class FallingObject {
        int x, y, imageIndex;

        FallingObject(int x, int y, int imageIndex) {
            this.x = x;
            this.y = y;
            this.imageIndex = imageIndex;
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, 50, 50);
        }
    }
}
