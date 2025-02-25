package ee.taltech.game.server.packets;

public class PlayerPositionYPacket extends Packet {

    private float y;
    private int connectionId;
    public PlayerPositionYPacket(float y, int gameId) {
        this.y = y;
        this.gameId = gameId;
    }

    public PlayerPositionYPacket() { }

    public int getConnectionId() { return connectionId; }
    public float getY() { return y; }
}
