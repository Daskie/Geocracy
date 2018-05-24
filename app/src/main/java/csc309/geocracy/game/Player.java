package csc309.geocracy.game;

import com.github.javafaker.Faker;

import java.util.HashSet;
import java.util.Random;

import csc309.geocracy.world.Territory;
import glm_.vec3.Vec3;

public class Player {

    private static int fakerSeed = 1;
    private static Faker faker = new Faker(new Random(fakerSeed));

    private int id; // starts at 1. 0 indicates no player
    private String name;
    private HashSet<Territory> territories;
    private Vec3 color;

    public Player(int id, Vec3 color) {
        this.id = id;
        this.name = faker.name().fullName();
        this.color = new Vec3(color);
    }

    public void addTerritory(Territory territory) {
        territories.add(territory);
    }

    public void removeTerritory(Territory territory) {
        territories.remove(territory);
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
