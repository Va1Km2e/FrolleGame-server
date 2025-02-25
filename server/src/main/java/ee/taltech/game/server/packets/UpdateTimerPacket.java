package ee.taltech.game.server.packets;

import com.esotericsoftware.kryo.serializers.DefaultSerializers;

public class UpdateTimerPacket extends Packet {
    private int timeInSeconds;
    public UpdateTimerPacket() {}

    public UpdateTimerPacket(int timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }
    public int getTimeInSeconds() {
        return timeInSeconds;
    }
    public void setTimeInSeconds(int timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }
}
