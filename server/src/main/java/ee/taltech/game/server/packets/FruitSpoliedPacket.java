package ee.taltech.game.server.packets;

public class FruitSpoliedPacket extends Packet {
    private int spoiledStage;
    private int fruitId;

    public FruitSpoliedPacket() { }
    public FruitSpoliedPacket(int fruitTd, int spoiledStage) {
        this.spoiledStage = spoiledStage;
        this.fruitId = fruitTd;
    }

    public int getFruitId() {
        return fruitId;
    }

    public int getSpoiledStage() {
        return spoiledStage;
    }
}
