package csc309.geocracy.game;

import com.github.javafaker.Faker;

import java.util.HashSet;
import java.util.Random;

import csc309.geocracy.world.Territory;
import glm_.vec3.Vec3;

public abstract class Player {
    private int id; // starts at 1. 0 indicates no player
    public String name;
    private HashSet<Territory> territories;
    private Vec3 color;

    public Player(int id, Vec3 color) {
        this.id = id;
        this.color = new Vec3(color);
        this.territories = new HashSet<Territory>();
    }

    public void addTerritory(Territory territory) {
        this.territories.add(territory);
    }

    public void removeTerritory(Territory territory) {
        this.territories.remove(territory);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public HashSet<Territory> getTerritories() {
        return territories;
    }

    public Vec3 getColor() {
        return color;
    }
}
