import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Parasite extends GameObject {
    Parasite() {
        this.x = ThreadLocalRandom.current().nextInt(100, 900);
        this.y = ThreadLocalRandom.current().nextInt(500, 800);
        this.width = 40;  // 标本瓶子很小，难抓
        this.height = 40;
        this.weight = 10; // 很轻，抓取速度快
        this.value = 0; // 价值连城
        this.flag = false;
        this.type = 3;    // 标记为任务物品类型 3
        // 请确保 images 文件夹下有 parasite.png
        this.image = Toolkit.getDefaultToolkit().getImage("images/parasite.png");
    }
}