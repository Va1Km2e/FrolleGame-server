package ee.taltech.game.server.packets;

public class FruitCollisionPacket extends Packet {
    private int fruitId;
    private float playerX;
    private float playerY;
    private int playerId;

    public FruitCollisionPacket() {}

    public FruitCollisionPacket(int fruitId, float playerX, float playerY, int gameId) {
        this.gameId = gameId;
        this.fruitId = fruitId;
        this.playerX = playerX;
        this.playerY = playerY;
    }

    public int getFruitId() {
        return fruitId;
    }

    public float getPlayerX() {
        return playerX;
    }

    public float getPlayerY() {
        return playerY;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setFruitId(int fruitId) {
        this.fruitId = fruitId;
    }
}
