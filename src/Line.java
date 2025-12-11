import java.awt.*;

/**
 * Represents the "Psionic Hand" (Grappling Hook) in the game.
 * <p>
 * This class manages the physics, rendering, and state machine of the hook.
 * It handles the swinging motion, extension, collision detection with GameObjects,
 * and the retrieval mechanics including D20 rolls and weight calculations.
 * </p>
 */
public class Line {

    // --- Constants (Magic Numbers removed) ---
    private static final int START_X = 550;
    private static final int START_Y = 120;
    private static final int MIN_LENGTH = 100;
    private static final int MAX_LENGTH = 900;
    private static final double SWING_SPEED = 0.005;
    private static final int EXTEND_SPEED = 10;
    private static final int RETRACT_SPEED = 10;

    // --- Coordinates & Physics ---
    /** The pivot point X coordinate. */
    int x = START_X;
    /** The pivot point Y coordinate. */
    int y = START_Y;
    /** The current end X coordinate of the hook. */
    int endx = 550;
    /** The current end Y coordinate of the hook. */
    int endy = 500;
    /** Current length of the line. */
    double length = MIN_LENGTH;
    /** Angle factor (0.0 to 1.0) for swinging. */
    double angleFactor = 0;
    /** 1 for clockwise, -1 for counter-clockwise. */
    int direction = 1;

    // --- State Machine ---
    // 0: Swing, 1: Extend, 2: Empty Retract, 3: Haul Item
    int state;

    // --- Components ---
    Image hand = Toolkit.getDefaultToolkit().getImage("images/hand-2.png");
    GameWin frame;

    // --- Gameplay Mechanics ---
    int d20Result = 0;
    boolean showDice = false;

    /**
     * Constructor.
     * @param frame Reference to the main game window to access object lists.
     */
    Line(GameWin frame) {
        this.frame = frame;
    }

    /**
     * Main update and render method called every frame.
     * Acts as the controller for the State Machine.
     *
     * @param g Graphics context
     * @throws InterruptedException due to Thread.sleep in hauling logic
     */
    void paintSelf(Graphics g) throws InterruptedException {
        // Run collision detection logic first
        detectCollision();

        // State Machine
        switch (state) {
            case 0:
                swing(g);
                break;
            case 1:
                extend(g);
                break;
            case 2:
                retractEmpty(g);
                break;
            case 3:
                haulObject(g);
                break;
            default:
                System.err.println("Invalid State: " + state);
                break;
        }
    }

    // ===========================
    // STATE BEHAVIORS
    // ===========================

    /** State 0: Idle Swinging */
    private void swing(Graphics g) {
        if (angleFactor < 0.1) {
            direction = 1;
        } else if (angleFactor > 0.9) {
            direction = -1;
        }
        angleFactor = angleFactor + SWING_SPEED * direction;
        drawLine(g);
    }

    /** State 1: Extending forward */
    private void extend(Graphics g) {
        if (length < MAX_LENGTH) {
            length = length + EXTEND_SPEED;
            drawLine(g);
        } else {
            // Max length reached without hitting anything
            state = 2;
        }
    }

    /** State 2: Retracting without an item */
    private void retractEmpty(Graphics g) {
        if (length > MIN_LENGTH) {
            length = length - RETRACT_SPEED;
            drawLine(g);
        } else {
            // Fully retracted
            resetState();
        }
    }

    /** State 3: Hauling an item back */
    private void haulObject(Graphics g) throws InterruptedException {
        int delayTime = 10; // Default speed

        // 1. Find and update the grabbed object
        for (GameObject obj : this.frame.objectList) {
            if (obj.flag) {
                int currentWeight = obj.weight;

                // D20 Critical Success Logic (Nat 20)
                if (d20Result >= 18) {
                    currentWeight = 1;
                }

                // Potion Logic (Speed Potion)
                if (Background.potionOfSpeedFlag && d20Result != 20) {
                    currentWeight = Math.max(1, currentWeight / 5);
                }

                delayTime = currentWeight;

                // Sync object position to hook
                obj.x = endx - obj.getWidth() / 2;
                obj.y = endy;
            }
        }

        // 2. Retract or Finish
        if (length > MIN_LENGTH) {
            length = length - 10;
            drawLine(g);
        } else {
            // Finished hauling
            drawLine(g); // Draw final frame
            processHaulResult();
        }

        // 3. Simulate weight friction
        Thread.sleep(delayTime);
    }

    // ===========================
    // LOGIC & HELPER METHODS
    // ===========================

    /**
     * Checks if the hook tip collides with any GameObject.
     * Only runs when State == 1 (Extending).
     */
    void detectCollision() {
        if (state != 1) return;

        for (GameObject obj : this.frame.objectList) {
            if (endx > obj.x && endx < obj.x + obj.width
                    && endy > obj.y && endy < obj.y + obj.height) {

                // 1. Check for Explosive Barrel
                if (obj.type == 5) { // GameConstants.TYPE_BARREL
                    explode(obj);
                    state = 2; // Retract empty
                    return;
                }

                // 2. Handle D20 Roll
                performD20Roll();

                // 3. Handle Critical Miss (Nat 1)
                if (d20Result <= 3) {
                    state = 2; // Slip and retract empty
                    return;
                }

                // 4. Successful Grab
                state = 3;
                obj.flag = true;
            }
        }
    }

    /**
     * Calculates the D20 roll outcome.
     */
    private void performD20Roll() {
        d20Result = (int) (Math.random() * 20) + 1;
        showDice = true;
        System.out.println("Rolled a " + d20Result);
    }

    /**
     * Processes score, quest items, and resets state after a successful haul.
     */
    private void processHaulResult() {
        for (GameObject obj : this.frame.objectList) {
            if (obj.flag) {
                // Remove from screen
                obj.x = -150;
                obj.y = -150;
                obj.flag = false;

                // Calculate Score
                int finalValue = obj.value;
                if (Background.hasVolosEye && (obj instanceof Gold)) {
                    finalValue *= 2;
                }
                Background.totalValue += finalValue;

                // Check Quest Items
                if (obj.type == 1) Background.hasArtifact = true; // Constants preferred
                if (obj.type == 2) Background.hasGale = true;
                if (obj.type == 3) Background.hasParasite = true;
            }
        }

        // Reset turn flags
        Background.potionOfSpeedFlag = false;
        resetState();
    }

    /**
     * Handles the logic for the "Thunderwave" ability (destroy grabbed item).
     */
    void thunderwave() {
        for (GameObject obj : this.frame.objectList) {
            if (obj.flag) {
                obj.x = -150;
                obj.y = -150;
                obj.flag = false;

                Background.potionOfSpeedFlag = false;

                // Immediate reset
                state = 0;
                length = MIN_LENGTH;
            }
        }
    }

    /**
     * Handles the logic for the Explosive Barrel.
     * @param centerBarrel The barrel that was hit.
     */
    void explode(GameObject centerBarrel) {
        int centerX = centerBarrel.x + centerBarrel.getWidth() / 2;
        int centerY = centerBarrel.y + centerBarrel.height / 2;

        // Remove the barrel immediately
        centerBarrel.x = -999;
        centerBarrel.flag = false;

        int radius = 150;

        for (GameObject target : this.frame.objectList) {
            if (target.x == -999) continue;

            // Distance calculation
            int targetCenterX = target.x + target.getWidth() / 2;
            int targetCenterY = target.y + target.height / 2;
            double distance = Math.sqrt(Math.pow(centerX - targetCenterX, 2) + Math.pow(centerY - targetCenterY, 2));

            if (distance < radius) {
                // Do not destroy Quest Items
                if (target.type != 1 && target.type != 2 && target.type != 3) {
                    target.x = -999;
                    target.flag = false;
                    if (target.type == 4) Background.totalValue += 5; // Bonus for enemies
                }
            }
        }
        System.out.println("KABOOM! Explosive barrel triggered.");
    }

    /**
     * Calculates coordinates and draws the line/hand/dice.
     */
    void drawLine(Graphics g) {
        endx = (int) (x + length * Math.cos(angleFactor * Math.PI));
        endy = (int) (y + length * Math.sin(angleFactor * Math.PI));

        // Draw Line
        g.setColor(Color.RED); // Changed to RED for better visibility
        g.drawLine(x - 1, y, endx - 1, endy);
        g.drawLine(x, y, endx, endy);
        g.drawLine(x + 1, y, endx + 1, endy);

        // Draw Hand
        g.drawImage(hand, endx - 20, endy - 2, null);

        // Draw Dice Result
        if (showDice) {
            g.setFont(new Font("Arial", Font.BOLD, 25));
            if (d20Result >= 18) {
                g.setColor(Color.GREEN);
                g.drawString("CRIT! " + d20Result, endx + 20, endy);
            } else if (d20Result <= 3) {
                g.setColor(Color.RED);
                g.drawString("MISS! " + d20Result, endx + 20, endy);
            } else {
                g.setColor(Color.WHITE);
                g.drawString("Roll: " + d20Result, endx + 20, endy);
            }
        }
    }

    /**
     * Resets the hook to its initial state.
     */
    void resetState() {
        state = 0;
        showDice = false;
    }

    /**
     * Hard reset for Game Restart (Game Over/Win).
     */
    void reset() {
        angleFactor = 0;
        length = MIN_LENGTH;
        state = 0;
        showDice = false;
    }
}
