import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Barrel extends GameObject {
    Barrel() {
        this.x = ThreadLocalRandom.current().nextInt(100, 900);
        this.y = ThreadLocalRandom.current().nextInt(300, 600);
        this.width = 100;
        this.height = 100;
        this.weight = 0;
        this.value = 0;
        this.flag = false;
        this.type = 5; // explosive
        this.image = Toolkit.getDefaultToolkit().getImage("images/barrel.png");
    }
}