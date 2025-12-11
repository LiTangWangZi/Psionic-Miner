import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class GameWin extends JFrame {

    // Global Game State
    static int gameState = GameConstants.STATE_READY;

    // Components
    Background bg = new Background();
    MusicPlayer bgm = new MusicPlayer();
    Line line = new Line(this);

    // Game Entities
    List<GameObject> objectList = new ArrayList<>();

    Image offScreenImage;

    public static void main(String[] args) {
        GameWin gameWin = new GameWin();
        gameWin.launch();
    }

    void launch() {
        initializeWindow();

        // Initial setup
        createObjects();
        bgm.playMusic("src/music/bgm.wav");

        // Input Handling
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                handleInput(e.getKeyCode());
            }
        });

        // Game Loop
        while (true) {
            repaint();
            checkLevelProgression();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initializeWindow() {
        this.setVisible(true);
        this.setSize(GameConstants.WIN_WIDTH, GameConstants.WIN_HEIGHT);
        this.setLocationRelativeTo(null);
        this.setTitle("Psionic Miner - Nautiloid Wreckage");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setFocusable(true);
    }

    // --- LOGIC: Input Handling ---

    private void handleInput(int keyCode) {
        switch (gameState) {
            case GameConstants.STATE_READY:
                if (keyCode == KeyEvent.VK_SPACE) gameState = GameConstants.STATE_TUTORIAL;
                break;

            case GameConstants.STATE_TUTORIAL:
                if (keyCode == KeyEvent.VK_SPACE) startGame();
                break;

            case GameConstants.STATE_RUNNING:
                handleGameplayInput(keyCode);
                break;

            case GameConstants.STATE_SHOP:
                handleShopInput(keyCode);
                break;

            case GameConstants.STATE_FAIL:
            case GameConstants.STATE_WIN:
                if (keyCode == KeyEvent.VK_SPACE) restartGame();
                break;
        }
    }

    private void handleGameplayInput(int keyCode) {
        if (keyCode == KeyEvent.VK_DOWN) {
            if (line.state == 0) line.state = 1;
        }
        if (keyCode == KeyEvent.VK_Q) {
            if (line.state == 0 && Background.potionOfSpeedNumber > 0) {
                Background.potionOfSpeedFlag = true;
                Background.potionOfSpeedNumber--;
            }
        }
        if (keyCode == KeyEvent.VK_W) {
            if (line.state == 3 && Background.thunderwaveCount > 0) {
                Background.thunderwaveCount--;
                line.thunderwave();
            }
        }
    }

    private void handleShopInput(int keyCode) {
        if (keyCode == KeyEvent.VK_1) buyItem(bg.pricePotion, () -> Background.potionOfSpeedNumber++);
        if (keyCode == KeyEvent.VK_2) buyItem(bg.priceScroll, () -> Background.thunderwaveCount++);
        if (keyCode == KeyEvent.VK_3) {
            if (Background.totalValue >= bg.priceEye && !Background.hasVolosEye) {
                Background.totalValue -= bg.priceEye;
                Background.hasVolosEye = true;
            }
        }
        if (keyCode == KeyEvent.VK_4) {
            if (Background.totalValue >= bg.priceTime) {
                Background.totalValue -= bg.priceTime;
                Background.extraTime += 10;
            }
        }
        if (keyCode == KeyEvent.VK_N) {
            gameState = GameConstants.STATE_RUNNING;
            bg.startTime = System.currentTimeMillis();
        }
    }

    // Helper interface for shop actions
    interface ShopAction { void execute(); }

    private void buyItem(int price, ShopAction action) {
        if (Background.totalValue >= price) {
            Background.totalValue -= price;
            action.execute();
        }
    }

    // --- LOGIC: Game Flow ---

    private void startGame() {
        gameState = GameConstants.STATE_RUNNING;
        bg.startTime = System.currentTimeMillis();
    }

    private void restartGame() {
        bgm.stopMusic();
        gameState = GameConstants.STATE_READY;
        bg.resetGame();
        line.reset();
        createObjects();
        bgm.playMusic("src/music/bgm.wav");
    }

    public void checkLevelProgression() {
        if (bg.gameTime() && gameState == GameConstants.STATE_RUNNING) {
            boolean scoreMet = Background.totalValue >= bg.goal;
            boolean taskMet = checkTaskCompletion();

            if (scoreMet && taskMet) {
                if (Background.level == 3) {
                    gameState = GameConstants.STATE_WIN;
                } else {
                    advanceLevel();
                }
            } else {
                gameState = GameConstants.STATE_FAIL;
            }
        }
    }

    private boolean checkTaskCompletion() {
        if (Background.level == 1 && Background.hasArtifact) return true;
        if (Background.level == 2 && Background.hasGale) return true;
        if (Background.level == 3 && Background.hasParasite) return true;
        return false;
    }

    private void advanceLevel() {
        dispose();
        Background.level++;
        Background.extraTime = 0;
        Background.hasVolosEye = false;
        gameState = GameConstants.STATE_SHOP;
        GameWin gameWin = new GameWin();
        gameWin.launch();
    }

    // --- LOGIC: Object Generation (Using Factory) ---

    void createObjects() {
        objectList.clear();

        // 1. Level Specific Items
        if (Background.level == 1) tryAddObject(GameConstants.TYPE_ARTIFACT);
        else if (Background.level == 2) tryAddObject(GameConstants.TYPE_GALE);
        else if (Background.level == 3) tryAddObject(GameConstants.TYPE_PARASITE);

        // 2. Barrels
        for (int i = 0; i < 2; i++) tryAddObject(GameConstants.TYPE_BARREL);

        // 3. Enemies
        for (int i = 0; i < Background.level; i++) tryAddObject(GameConstants.TYPE_ENEMY);

        // 4. Gold
        for (int i = 0; i < 3 + Background.level; i++) tryAddObject(GameConstants.TYPE_GOLD);

        // 5. Debris
        for (int i = 0; i < 3 + Background.level; i++) tryAddObject(GameConstants.TYPE_DEBRIS);
    }

    /**
     * Tries to add an object using the Factory.
     * Handles collision detection recursively.
     */
    public void tryAddObject(int type) {
        // USE THE FACTORY PATTERN HERE
        GameObject newObj = GameObjectFactory.createObject(type);

        boolean isPlaceable = true;
        for (GameObject existingObj : objectList) {
            if (newObj.getBounds().intersects(existingObj.getBounds())) {
                isPlaceable = false;
                break;
            }
        }

        if (isPlaceable) {
            objectList.add(newObj);
        } else {
            // Recursively try again (The factory creates a new instance with random coords)
            tryAddObject(type);
        }
    }

    // --- GRAPHICS ---

    @Override
    public void paint(Graphics g) {
        offScreenImage = this.createImage(GameConstants.WIN_WIDTH, GameConstants.WIN_HEIGHT);
        Graphics gImage = offScreenImage.getGraphics();

        bg.paintSelf(gImage);

        if (gameState == GameConstants.STATE_RUNNING) {
            for (GameObject object : objectList) {
                object.paintSelf(gImage);
            }
            try {
                line.paintSelf(gImage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        g.drawImage(offScreenImage, 0, 0, this);
    }
}