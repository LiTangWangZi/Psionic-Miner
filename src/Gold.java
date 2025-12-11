import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Gold extends GameObject {

    Gold() {
        this.x = ThreadLocalRandom.current().nextInt(100,800);
        this.y = ThreadLocalRandom.current().nextInt(400,900);
        this.width = 50;
        this.height = 50;
        this.weight = 50;
        this.value = 5;
        this.flag = false;
        this.image = Toolkit.getDefaultToolkit().getImage("images/gold.png");
    }
}
