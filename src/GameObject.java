import java.awt.*;

public class GameObject {
    int x;
    int y;
    int width;
    int height;
    int weight;
    int value;
    boolean flag;
    int type = 0; // 0:Gold and Debris, 1:Artifact, 2:Gale, 3:Parasite, 4:Enemy
    Image image;

    void paintSelf(Graphics g) {
        logic();
        g.drawImage(image, x, y, null);
    }

    void logic() {
    }

    int getWidth() {
        return width;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
