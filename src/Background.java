import java.awt.*;

/**
 * Manages the game's background rendering, UI overlays, and global game statistics.
 * <p>
 * This class handles the visual presentation of different game states (Start, Shop, Gameplay, Game Over)
 * and holds static variables for player inventory and level progress.
 * </p>
 */
public class Background {

    // ==========================================
    // GLOBAL GAME STATE (Static for accessibility)
    // ==========================================
    public static int level = 1;
    public int goal = level * 5; // Goal score for the current level
    public static int totalValue = 0; // Current score

    // Inventory & Buffs
    public static int potionOfSpeedNumber = 1;
    public static boolean potionOfSpeedFlag = false;
    public static int thunderwaveCount = 0;
    public static boolean hasVolosEye = false;
    public static int extraTime = 0;

    // Quest Item Flags
    public static boolean hasArtifact = false;
    public static boolean hasGale = false;
    public static boolean hasParasite = false;

    // Logic Variables
    long startTime;
    long endTime;

    // Random Shop Prices
    int pricePotion = (int) (Math.random() * 5) + 3;
    int priceScroll = (int) (Math.random() * 5) + 3;
    int priceEye = (int) (Math.random() * 5) + 7;
    int priceTime = (int) (Math.random() * 5) + 7;

    // ==========================================
    // ASSETS (Images & Fonts)
    // ==========================================
    // Backgrounds
    Image bg1 = Toolkit.getDefaultToolkit().getImage("images/bg1.png");
    Image shopBg = Toolkit.getDefaultToolkit().getImage("images/shop.jpg");
    Image startBg = Toolkit.getDefaultToolkit().getImage("images/start_bg.jpg");
    Image winBg = Toolkit.getDefaultToolkit().getImage("images/win_bg.jpg");
    Image failBg = Toolkit.getDefaultToolkit().getImage("images/fail_bg.jpg");

    // UI Elements
    Image character = Toolkit.getDefaultToolkit().getImage("images/character.png");
    Image potionOfSpeed = Toolkit.getDefaultToolkit().getImage("images/potionOfSpeed.png");
    Image potionOfSpeed_1 = Toolkit.getDefaultToolkit().getImage("images/potionOfSpeed_1.png");
    Image imgScroll = Toolkit.getDefaultToolkit().getImage("images/scroll.png");
    Image imgScroll_1 = Toolkit.getDefaultToolkit().getImage("images/scroll_1.png");
    Image imgEye = Toolkit.getDefaultToolkit().getImage("images/eye.png");
    Image imgEye_1 = Toolkit.getDefaultToolkit().getImage("images/eye_1.png");
    Image imgTime = Toolkit.getDefaultToolkit().getImage("images/time.png");

    // Pre-loaded Fonts
    Font fontTitle = new Font("Times New Roman", Font.BOLD, 80);
    Font fontSubTitle = new Font("Times New Roman", Font.BOLD, 40);
    Font fontLarge = new Font("Arial", Font.BOLD, 50);
    Font fontMedium = new Font("Arial", Font.BOLD, 30);
    Font fontStory = new Font("Arial", Font.ITALIC, 22);

    // ==========================================
    // RENDERING LOGIC
    // ==========================================

    /**
     * Main rendering method. Switches drawing logic based on the current GameState.
     * @param g The Graphics context.
     */
    public void paintSelf(Graphics g) {
        switch (GameWin.gameState) {
            case 0:
                drawStartScreen(g);
                break;
            case 1:
                drawGameplay(g);
                break;
            case 2:
                drawShop(g);
                break;
            case 3:
                drawGameOver(g);
                break;
            case 4:
                drawVictory(g);
                break;
            case 5:
                drawTutorial(g);
                break;
            default:
                break;
        }
    }

    // --- HELPER METHODS FOR RENDERING ---

    private void drawStartScreen(Graphics g) {
        g.drawImage(startBg, 0, 0, 1024, 1200, null);

        // Main Title (Gold with Black Shadow)
        g.setFont(fontTitle);
        g.setColor(Color.BLACK);
        g.drawString("Psionic Miner", 265, 305);
        g.setColor(new Color(255, 215, 0));
        g.drawString("Psionic Miner", 260, 300);

        // Subtitle
        g.setFont(fontSubTitle);
        g.setColor(Color.BLACK);
        g.drawString("Nautiloid Wreckage", 303, 383);
        g.setColor(Color.WHITE);
        g.drawString("Nautiloid Wreckage", 300, 380);

        // Start Prompt
        g.setFont(fontMedium);
        g.setColor(Color.GREEN);
        g.drawString("Press Space to start!", 350, 600);
    }

    private void drawGameplay(Graphics g) {
        g.drawImage(bg1, 0, 0, null);
        g.drawImage(character, 500, 30, null);

        // Score info
        g.setColor(Color.RED);
        g.setFont(new Font("Times New Roman", Font.BOLD, 30)); // Keep this local if it changes often, or make global
        g.drawString("Value:" + totalValue + " / " + goal, 30, 150);

        // Quest Objectives
        g.setColor(Color.YELLOW);
        String missionText = "";
        if (level == 1) missionText = "Mission: Get Artifact! " + (hasArtifact ? "(OK)" : "(X)");
        else if (level == 2) missionText = "Mission: Rescue Gale! " + (hasGale ? "(OK)" : "(X)");
        else if (level == 3) missionText = "Mission: Get Parasite! " + (hasParasite ? "(OK)" : "(X)");
        g.drawString(missionText, 50, 100);

        // Inventory UI (Potions & Scrolls)
        g.drawImage(potionOfSpeed, 600, 40, null);
        g.setColor(Color.RED);
        g.drawString("*" + potionOfSpeedNumber, 660, 70);

        g.drawImage(imgScroll, 800, 40, 50, 50, null);
        g.drawString("*" + thunderwaveCount, 860, 70);

        // Level & Time
        g.drawString("Level:" + level, 30, 50);

        endTime = System.currentTimeMillis();
        long timeLimit = 30 + extraTime;
        long time = timeLimit - (endTime - startTime) / 1000;
        g.drawString("Time:" + (time >= 0 ? time : 0), 700, 150);
    }

    private void drawShop(Graphics g) {
        g.drawImage(shopBg, 0, 0, 1024, 1200, null);
        g.setColor(Color.WHITE);

        g.setFont(fontMedium);
        g.drawString("Welcome to Withers' Shop", 300, 100);

        // Draw Items
        // 1. Potion
        g.drawImage(potionOfSpeed_1, 100, 250, null);
        g.drawString("1. Speed Potion (" + pricePotion + ")", 100, 500);

        // 2. Scroll
        g.drawImage(imgScroll_1, 450, 250, null);
        g.drawString("2. Thunderwave (" + priceScroll + ")", 400, 500);

        // 3. Volo's Eye
        g.drawImage(imgEye_1, 750, 225, null);
        g.drawString("3. Volo's Eye (" + priceEye + ")", 750, 500);

        // 4. Time
        g.drawImage(imgTime, 450, 500, null);
        g.drawString("4. Healing Potion (" + priceTime + ")", 400, 750);

        // Footer
        g.drawString("Current Gold: " + totalValue, 300, 850);
        g.drawString("Press N to Next Level", 300, 950);
    }

    private void drawGameOver(Graphics g) {
        g.drawImage(failBg, 0, 0, 1024, 1200, null);
        g.setColor(Color.RED);

        g.setFont(fontLarge);
        g.drawString("Game Failed!", 300, 400);

        g.setFont(fontMedium);
        if (level == 1 && !hasArtifact) g.drawString("Missed Artifact...", 350, 450);
        if (level == 2 && !hasGale) g.drawString("Gale was left behind...", 300, 450);

        g.drawString("Press Space to Restart", 280, 500);
    }

    private void drawVictory(Graphics g) {
        g.drawImage(winBg, 0, 0, 1024, 1200, null);
        g.setColor(Color.YELLOW);

        g.setFont(fontLarge);
        g.drawString("Victory!", 400, 400);

        g.setFont(fontMedium);
        g.drawString("The Artifact is ours.", 350, 450);
        g.drawString("Press Space to Restart", 340, 600);
    }

    private void drawTutorial(Graphics g) {
        g.drawImage(startBg, 0, 0, 1024, 1200, null);

        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(100, 200, 824, 600);

        // Title
        g.setColor(new Color(255, 215, 0));
        g.setFont(fontSubTitle);
        g.drawString("Prologue: The Crash", 320, 260);

        // Story Text
        g.setColor(Color.WHITE);
        g.setFont(fontStory);
        drawStringMultiLine(g,
                "You awaken amidst the burning wreckage of the Nautiloid.\n" +
                        "The Mind Flayer parasite squirms behind your eye.\n" +
                        "You must salvage resources, rescue companions,\n" +
                        "and find the Artifact before the transformation begins.",
                150, 320, 30);

        g.drawLine(150, 460, 874, 460);

        // Controls
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.drawString("HOW TO PLAY", 420, 500);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("▼ [Down Arrow]: Launch Psionic Hand", 250, 550);
        g.drawString("★ [Space]: Start / Reset Game", 250, 600);
        g.drawString("⚗ [Q]: Drink Speed Potion (Faster Retract)", 250, 650);
        g.drawString("💥 [W]: Cast Thunderwave (Destroy Grabbed Item)", 250, 700);
        g.drawString("🛒 [1/2/3/4]: Buy Items in Shop", 250, 750);

        // Prompt
        g.setColor(Color.GREEN);
        g.setFont(fontMedium);
        g.drawString("Press Space to Begin your Journey...", 280, 850);
    }

    // ==========================================
    // LOGIC & UTILITIES
    // ==========================================

    /**
     * Checks if the time limit for the level has been exceeded.
     * @return true if time is up, false otherwise.
     */
    public boolean gameTime() {
        long time = (endTime - startTime) / 1000;
        return time > (30 + extraTime);
    }

    /**
     * Resets all game statistics to Level 1 defaults.
     */
    void resetGame() {
        level = 1;
        goal = level * 5;
        totalValue = 0;

        // Reset Quest flags
        hasArtifact = false;
        hasGale = false;
        hasParasite = false;

        // Reset Inventory
        potionOfSpeedNumber = 1;
        thunderwaveCount = 0;
        hasVolosEye = false;
        extraTime = 0;
    }

    /**
     * Helper method to draw strings that contain newline characters (\n).
     * @param g Graphics context
     * @param text The text string (can contain \n)
     * @param x X coordinate
     * @param y Y coordinate
     * @param lineHeight Space between lines
     */
    void drawStringMultiLine(Graphics g, String text, int x, int y, int lineHeight) {
        for (String line : text.split("\n")) {
            g.drawString(line, x, y);
            y += lineHeight;
        }
    }
}