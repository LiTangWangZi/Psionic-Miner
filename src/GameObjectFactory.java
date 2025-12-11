public class GameObjectFactory {

    /**
     * Factory Method to create game objects.
     * This decouples the logic of "what to create" from the GameWin class.
     */
    public static GameObject createObject(int type) {
        switch (type) {
            case GameConstants.TYPE_GOLD:
                return new Gold();
            case GameConstants.TYPE_DEBRIS:
                return new Debris();
            case GameConstants.TYPE_ARTIFACT:
                return new Artifact();
            case GameConstants.TYPE_GALE:
                return new Gale();
            case GameConstants.TYPE_PARASITE:
                return new Parasite();
            case GameConstants.TYPE_ENEMY:
                return new IntellectDevourer();
            case GameConstants.TYPE_BARREL:
                return new Barrel();
            default:
                throw new IllegalArgumentException("Unknown object type: " + type);
        }
    }
}