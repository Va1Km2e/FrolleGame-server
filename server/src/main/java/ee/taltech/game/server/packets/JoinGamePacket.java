package ee.taltech.game.server.packets;

public class JoinGamePacket extends Packet {
    public JoinGamePacket() { }
    public JoinGamePacket(int id) {
        gameId = id;
    }
}
