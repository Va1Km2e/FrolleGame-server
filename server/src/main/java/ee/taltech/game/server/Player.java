package ee.taltech.game.server;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final int id;
    private String name;
    private int score;
    private List<Integer> inventory;
    private static final int MAX_INVENTORY = 5;

    public Player(String name, int id) {
        this.id = id;
        this.name = name;
        this.inventory = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<Integer> getInventory() {
        return inventory;
    }

    public boolean addItemToInventory(Integer fruit) {
        if (inventory.size() >= MAX_INVENTORY) {
            return false;
        }
        inventory.add(fruit);
        return true;
    }

    public void removeFromInventory(Integer fruit) {
        inventory.remove(fruit);
    }

    public void clearInventory() {
        inventory.clear();
    }
}
