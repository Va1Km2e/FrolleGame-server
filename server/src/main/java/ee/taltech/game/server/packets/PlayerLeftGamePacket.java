package ee.taltech.game.server.packets;

public class PlayerLeftGamePacket extends Packet {
    public PlayerLeftGamePacket() {}

    public PlayerLeftGamePacket(int gameId) {
        this.gameId = gameId;
    }
}
