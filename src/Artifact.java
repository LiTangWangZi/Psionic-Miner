import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Artifact extends GameObject {
    Artifact() {
        this.x = ThreadLocalRandom.current().nextInt(200, 800);
        this.y = ThreadLocalRandom.current().nextInt(400, 600);
        this.width = 60;
        this.height = 60;
        this.weight = 30;
        this.value = 0;
        this.flag = false;
        this.type = 1;
        this.image = Toolkit.getDefaultToolkit().getImage("images/artifact.png");
    }
}