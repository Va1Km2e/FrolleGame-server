package ee.taltech.game.server;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.*;

import ee.taltech.game.server.packets.*;

public class GameServer {
    public static Server server;
    private Map<Integer, List<Player>> gameIdsAndPlayers;
    private static int currentGameId = 0;
    private static List<Game> games = new ArrayList<>();
    private static List<Player> players = new ArrayList<>();

    public GameServer() {
        gameIdsAndPlayers = new HashMap<>();
        server = new Server();
        server.start();
        registerPackets();
        try {
            server.bind(8080, 8081);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof JoinServerPacket) {;
                    server.sendToUDP(connection.getID(), object);
                    Player newPlayer = new Player("Player" + connection.getID(), connection.getID());
                    players.add(newPlayer);

                    System.out.println("Client joined!");
                }

                if (object instanceof PlayerPositionYPacket) {
                    for (Player player : gameIdsAndPlayers.get(((PlayerPositionYPacket) object).getGameId())) {
                        if (!Objects.equals(player.getId(), connection.getID())) {
                            server.sendToUDP(player.getId(), object);
                        }
                    }
                }

                if (object instanceof PlayerPositionXPacket) {
                    for (Player player : gameIdsAndPlayers.get(((PlayerPositionXPacket) object).getGameId())) {
                        if (!Objects.equals(player.getId(), connection.getID())) {
                            server.sendToUDP(player.getId(), object);
                        }
                    }
                }

                if (object instanceof RequestGamesPacket) {
                    for (Game game : games) {
                        if (game.isJoinable()) {
                            ((RequestGamesPacket) object).addGames(game.getGameId(),
                                    gameIdsAndPlayers.get(game.getGameId()).get(0).getName());
                        }
                    }
                    server.sendToUDP(connection.getID(), object);
                    System.out.println("Refresh packet:" + object);
                }

                if (object instanceof NamePacket) {
                    for (Player player : players) {
                        if (player.getId() == connection.getID()) {
                            player.setName(((NamePacket) object).getName());
                        }
                    }
                }

                if (object instanceof CreateNewGamePacket) {
                    int id = getNextGameId();

                    List<Player> playersInGame = gameIdsAndPlayers.getOrDefault(id, new ArrayList<>());
                    Player player = players.stream()
                            .filter(p -> p.getId() == connection.getID()).findFirst().get();
                    playersInGame.add(player);
                    gameIdsAndPlayers.put(currentGameId, playersInGame);
                    Game newGame = new Game(currentGameId, player);
                    newGame.setJoinable(true);
                    games.add(newGame);

                    ((CreateNewGamePacket) object).setGameId(id);
                    server.sendToUDP(connection.getID(), object);
                }

                if (object instanceof JoinGamePacket) {
                    Game game = games.stream().filter(g -> g.getGameId() == ((JoinGamePacket) object).getGameId())
                            .findFirst().get();

                    List<Player> playersInGame = gameIdsAndPlayers.getOrDefault(game.getGameId(), new ArrayList<>());
                    Player player = players.stream()
                            .filter(p -> p.getId() == connection.getID()).findFirst().get();
                    playersInGame.add(player);
                    gameIdsAndPlayers.put(currentGameId, playersInGame);
                    game.joinGame(players.stream().filter(p -> p.getId() == connection.getID()).findFirst().get());
                    game.setJoinable(false);
                }

                if (object instanceof FruitCollisionPacket) {
                    handleFruitCollision(connection.getID(), (FruitCollisionPacket) object);
                }

                if (object instanceof UnloadFruitsPacket) {
                    Player player = players.stream().filter(p -> p.getId() == connection.getID()).findFirst().orElseThrow();
                    for (int fruitPoints : player.getInventory()) {
                        player.setScore(player.getScore() + fruitPoints);
                    }
                    player.clearInventory();
                    server.sendToUDP(connection.getID(), new UpdatedScorePacket(player.getScore()));
                }

                if (object instanceof SheepAteFruitPacket) {
                    Game game = games.stream().filter(g -> g.getGameId() == ((SheepAteFruitPacket) object).getGameId()).findFirst().orElseThrow();
                    game.removeFruitById(((SheepAteFruitPacket) object).getFruitId());
                }

                if (object instanceof SheepAttackedPlayerPacket) {
                    Player player = players.stream().filter(p -> p.getId() == connection.getID()).findFirst().get();
                    if (!player.getInventory().isEmpty()) {
                        Integer lastFruit = player.getInventory().get(player.getInventory().size() - 1);
                        player.removeFromInventory(lastFruit);
                        server.sendToUDP(connection.getID(), new UpdatedInventoryPacket((ArrayList<Integer>) player.getInventory()));
                    }
                }

                if (object instanceof PlayerLeftGamePacket) {
                    removePlayerFromGame(connection.getID());
                }
            }

            @Override
            public void disconnected(Connection connection) {
                super.disconnected(connection);
                removePlayerFromGame(connection.getID());
            }

        });
    }

    private void removePlayerFromGame(int playerId) {
        Player player = players.stream().filter(p -> p.getId() == playerId).findFirst().get();
        Optional<Game> game = games.stream().filter(g -> g.getPlayers().contains(player)).findFirst();
        if (game.isEmpty()) {
            return;
        }
        game.get().leaveGame(player);
        gameIdsAndPlayers.put(game.get().getGameId(), game.get().getPlayers());
        game.get().setJoinable(false);
    }

    public static int getNextGameId() {
        return ++currentGameId;
    }


    public static void removeGameFromGames(Game game) {
        games.remove(game);
    }

    private void registerPackets() {
        Kryo kryo = server.getKryo();
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);
        kryo.register(JoinServerPacket.class);
        kryo.register(PlayerPositionXPacket.class);
        kryo.register(PlayerPositionYPacket.class);
        kryo.register(RequestGamesPacket.class);
        kryo.register(NamePacket.class);
        kryo.register(CreateNewGamePacket.class);
        kryo.register(JoinGamePacket.class);
        kryo.register(SendFruitPacket.class);
        kryo.register(FruitCollisionPacket.class);
        kryo.register(UpdatedScorePacket.class);
        kryo.register(UpdatedInventoryPacket.class);
        kryo.register(GameOverPacket.class);
        kryo.register(UpdateTimerPacket.class);
        kryo.register(UnloadFruitsPacket.class);
        kryo.register(FruitSpoliedPacket.class);
        kryo.register(SheepAteFruitPacket.class);
        kryo.register(SheepAttackedPlayerPacket.class);
        kryo.register(PlayerLeftGamePacket.class);
    }

    private void handleFruitCollision(int connectionId, FruitCollisionPacket fcp) {
        System.out.println(fcp.getFruitId());
        fcp.setPlayerId(connectionId);
        try {
            Game game = games.stream().filter(g -> g.getGameId() == fcp.getGameId()).findFirst().orElseThrow();
            Fruit fruit = game.getFruits()
                    .stream()
                    .filter(f -> f.getId() == fcp.getFruitId())
                    .findAny()
                    .orElse(null);
            if (fruit == null) {
                return;
            }

            if (Math.abs(fruit.getX() - fcp.getPlayerX()) < 50
                    && Math.abs(fruit.getY()) - fcp.getPlayerY() < 50
                    && fruit.getId() == fcp.getFruitId()) {

                for (Player player : game.getPlayers()) {
                    if (player.getId() == connectionId) {
                        if (!player.addItemToInventory(fruit.getPoints())) {
                            System.out.println("Returning!~!!");
                            return;
                        }
                        System.out.println("New Fruit in inventory! " + player.getInventory());
                        server.sendToUDP(connectionId, new UpdatedInventoryPacket((ArrayList<Integer>) player.getInventory()));
                    }
                    server.sendToUDP(player.getId(), fcp);
                }

                game.removeFruitById(fruit.getId());
            }
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Element not found at handleFruitCollision!");
        }
    }

    public static void main(String[] args) {
        GameServer gameServer = new GameServer();
    }

    public static List<Game> getGames() {
        return games;
    }
}
