package ee.taltech.game.server;

import ee.taltech.game.server.packets.FruitSpoliedPacket;
import ee.taltech.game.server.packets.SendFruitPacket;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Fruit {
    private static int currentId = 1;
    private final int id;
    private final float x;
    private final float y;
    private final int points;
    private int gameId;
    private ScheduledExecutorService timer;
    private int spoiledStage;
    private fruitType type;

    public int getGameId() {
        return gameId;
    }

    private enum fruitType {
        GREEN {
            @Override
            public int getPeriod() {
                return 4;
            }
        },
        BLUE {
            @Override
            public int getPeriod() {
                return 2;
            }
        },
        WHITE {
            @Override
            public int getPeriod() {
                return 3;
            }
        },
        GOLD {
            @Override
            public int getPeriod() {
                return 4;
            }
        };


        public int getPeriod() {
            return 0;
        }
    }

    public static int getNextId() {
        return currentId++;
    }

    public Fruit(float x, float y, int points, int gameId) {
        spoiledStage = 0;
        id = getNextId();
        this.x = x;
        this.y = y;
        this.points = points;
        type = getTypeFromPoints();
        startListener();
        this.gameId = gameId;
    }


    public Fruit(Fruit oppositeFruit) {
        id = getNextId();
        this.x = oppositeFruit.getX();
        this.y = oppositeFruit.getY();
        this.points = oppositeFruit.getPoints();
        type = getTypeFromPoints();
        startListener();
    }

    private fruitType getTypeFromPoints() {
        if (points == 100) {
            return fruitType.GREEN;
        } else if (points == 200) {
            return fruitType.BLUE;
        } else if (points == 300) {
            return fruitType.WHITE;
        } else {
            return fruitType.GOLD;
        }
    }

    private void startListener() {
        timer = Executors.newScheduledThreadPool(1);
        timer.scheduleAtFixedRate(() -> {
            if (spoiledStage > 4) {
                sendFruitSpoiledPacketToClient();
                removeFruitFromGame();
                timer.shutdown();
            }
            sendFruitSpoiledPacketToClient();
            spoiledStage++;
            System.out.println("Fruit " + id + "spoiled to stage " + spoiledStage);
        }, type.getPeriod(), type.getPeriod(), TimeUnit.SECONDS);
    }

    private void sendFruitSpoiledPacketToClient() {
        Optional<Game> game = GameServer.getGames().stream().filter(g -> g.getGameId() == gameId).findFirst();
        if (game.isEmpty()) {
            return;
        }
        for (Player player : game.get().getPlayers()) {
            GameServer.server.sendToUDP(player.getId(), new FruitSpoliedPacket(id, spoiledStage));
        }
    }

    private void removeFruitFromGame() {
        Optional<Game> game = GameServer.getGames().stream().filter(g -> g.getGameId() == gameId).findFirst();
        if (game.isEmpty()) {
            return;
        }
        game.get().removeFruitById(id);
    }



    public int getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getPoints() {
        return points;
    }
}
