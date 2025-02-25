package ee.taltech.game.server.packets;

public class NamePacket extends Packet{
    private String name;
    public NamePacket(String name) {
        this.name = name;
    }

    public NamePacket() {}

    public String getName() {
        return name;
    }
}
