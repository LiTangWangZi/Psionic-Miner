public interface GameConstants {
    // Window Size
    int WIN_WIDTH = 1024;
    int WIN_HEIGHT = 1200;

    // Game States
    int STATE_READY = 0;
    int STATE_RUNNING = 1;
    int STATE_SHOP = 2;
    int STATE_FAIL = 3;
    int STATE_WIN = 4;
    int STATE_TUTORIAL = 5;

    // Object Types (For Factory)
    int TYPE_GOLD = 0;
    int TYPE_DEBRIS = 1;
    int TYPE_ARTIFACT = 2;
    int TYPE_GALE = 3;
    int TYPE_PARASITE = 4;
    int TYPE_ENEMY = 5;
    int TYPE_BARREL = 6;
}
