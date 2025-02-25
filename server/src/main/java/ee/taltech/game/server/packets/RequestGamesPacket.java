package ee.taltech.game.server.packets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestGamesPacket extends Packet {
    Map<Integer, String> gameIdsAndNames;
    public RequestGamesPacket() {
        gameIdsAndNames = new HashMap<>();
    }

    public void addGames(Map<Integer, String> map) {
        gameIdsAndNames.putAll(map);
    }

    public void addGames(int gameId, String name) {
        gameIdsAndNames.put(gameId, name);
    }

    public Map<Integer, String> getGames() {
        return gameIdsAndNames;
    }
}
