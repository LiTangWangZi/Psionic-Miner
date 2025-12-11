import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class IntellectDevourer extends GameObject {
    int speed;
    int direction = 1;

    IntellectDevourer() {
        this.x = ThreadLocalRandom.current().nextInt(100, 900);
        this.y = ThreadLocalRandom.current().nextInt(400, 600);
        this.width = 60;
        this.height = 50;
        this.weight = 60;
        this.value = 1;
        this.flag = false;
        this.type = 4;
        this.speed = ThreadLocalRandom.current().nextInt(2, 6);
        this.direction = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
        this.image = Toolkit.getDefaultToolkit().getImage("images/Intellect_Devourer.png");
    }

    @Override
    void logic() {
        if (!flag) {
            x += speed * direction;


            if (x > 1024 - width) {
                x = 1024 - width;
                direction = -1;
            }

            if (x < 0) {
                x = 0;
                direction = 1;
            }
        }
    }
}
