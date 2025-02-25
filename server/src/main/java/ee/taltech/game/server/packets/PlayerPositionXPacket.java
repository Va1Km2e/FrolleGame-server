package ee.taltech.game.server.packets;

public class PlayerPositionXPacket extends Packet {

    private float x;
    private int connectionId;
    public PlayerPositionXPacket(float x, int gameId) {
        this.x = x;
        this.gameId = gameId;
    }

    public PlayerPositionXPacket() { }

    public int getConnectionId() { return connectionId; }
    public float getX() { return x; }
}
