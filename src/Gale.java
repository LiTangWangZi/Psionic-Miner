import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Gale extends GameObject {
    Gale() {
        this.x = ThreadLocalRandom.current().nextInt(100, 900);
        this.y = ThreadLocalRandom.current().nextInt(500, 900);
        this.width = 80;
        this.height = 100;
        this.weight = 80;
        this.value = 0;
        this.flag = false;
        this.type = 2;
        this.image = Toolkit.getDefaultToolkit().getImage("images/gale.png");
    }
}