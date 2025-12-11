import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Debris extends GameObject {
    Debris() {
        this.x = ThreadLocalRandom.current().nextInt(100,800);
        this.y = ThreadLocalRandom.current().nextInt(400,700);
        this.width = 75;
        this.height = 50;
        this.weight = 100;
        this.value = 1;
        this.flag = false;
        this.image = Toolkit.getDefaultToolkit().getImage("images/debris.png");
    }
}
