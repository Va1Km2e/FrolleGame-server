package ee.taltech.game.server.packets;

public class UpdatedScorePacket extends Packet {
    private int score;

    public UpdatedScorePacket(int score) {
        this.score = score;
    }

    public UpdatedScorePacket() { }

    public int getScore() {
        return score;
    }
}
