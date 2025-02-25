package ee.taltech.game.server;

import ee.taltech.game.server.packets.GameOverPacket;
import ee.taltech.game.server.packets.SendFruitPacket;
import ee.taltech.game.server.packets.UpdateTimerPacket;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game {
    private static final int MAX_PLAYERS = 2;
    public static final int PLAY_AREA_WIDTH = 630;
    public static final int PLAY_AREA_HEIGHT = 580;
    private static final int GAME_LENGTH_SECONDS = 120;
    private boolean inProgress;
    private boolean joinable;
    private int gameId;
    private List<Player> players = new ArrayList<>();
    private ScheduledExecutorService fruitScheduler;
    private ScheduledExecutorService timer;
    private List<Fruit> fruits;
    private int timeInSeconds = GAME_LENGTH_SECONDS;

    public Game(int gameId, Player creator) {
        Random random = new Random();
        fruits = new ArrayList<>();
        this.gameId = gameId;
        players.add(creator);

        fruitScheduler = Executors.newScheduledThreadPool(1);
        fruitScheduler.scheduleAtFixedRate(() -> {
            int fruitProbablity = random.nextInt(0, 11);
            int points = calculateFruitPoints(fruitProbablity);
            Fruit fruit = new Fruit(random.nextFloat(200, PLAY_AREA_WIDTH),
                    random.nextFloat(90, PLAY_AREA_HEIGHT), points, gameId);
            Fruit oppositeFruit = new Fruit(fruit);
            fruits.add(fruit);
            fruits.add(oppositeFruit);
            int i = 0;
            for (Player player : players) {
                GameServer.server.sendToUDP(player.getId(), new SendFruitPacket(i == 0 ? fruit.getId() : oppositeFruit.getId(), fruit.getX(), fruit.getY(), fruit.getPoints(), i == 0 ? oppositeFruit.getId() : fruit.getId()));
                i++;
            }
            System.out.println("fruit");
        }, 4, 4, TimeUnit.SECONDS);

        timer = Executors.newScheduledThreadPool(1);
        timer.scheduleAtFixedRate(() -> {
            if (timeInSeconds <= 0) {
                Map<String, Integer> scores = new HashMap<>();
                for (Player player : players) {
                    scores.put(player.getName(), player.getScore());
                    player.setScore(0);
                    GameServer.removeGameFromGames(this);
                }
                for (Player player : players) {
                    GameServer.server.sendToUDP(player.getId(), new GameOverPacket(scores));
                    System.out.println("Sending score " + player.getId() + " " + player.getScore());
                }
                timer.shutdown();
                fruitScheduler.shutdown();
            }

            timeInSeconds--;
            for (Player player : players) {
                GameServer.server.sendToUDP(player.getId(), new UpdateTimerPacket(timeInSeconds));
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private int calculateFruitPoints(int probablity) {
        if (probablity < 5) {
            return 100;
        } else if (probablity < 8) {
            return 200;
        } else if (probablity < 10) {
            return 300;
        } else {
            return 500;
        }
    }

    public void joinGame(Player player) {
        if (players.size() <= MAX_PLAYERS) {
            players.add(player);
        }
    }

    public void leaveGame(Player player) {
        players.remove(player);
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public boolean isJoinable() {
        return joinable;
    }

    public void setJoinable(boolean joinable) {
        this.joinable = joinable;
    }

    public int getGameId() {
        return gameId;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Fruit> getFruits() {
        return fruits;
    }

    public void removeFruitById(int id) {
        fruits.removeIf(f -> f.getId() == id);
    }
}
