package ee.taltech.game.server.packets;

public class Packet {
    int gameId;
    public Packet() { }

    public Packet(int gameId) {
        this.gameId = gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getGameId() {
        return gameId;
    }
}
