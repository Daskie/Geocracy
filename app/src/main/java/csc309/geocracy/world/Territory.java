package csc309.geocracy.world;

import csc309.geocracy.game.Player;

import com.github.javafaker.Faker;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Random;

import glm_.vec3.Vec3;

public class Territory implements Serializable {

    private static int fakerSeed = 1;
    private static Faker faker = new Faker(new Random(fakerSeed));

    private int id; // starts at 1. 0 indicates no territory
    private String territoryName;
    private World world;
    private Continent continent;
    private HashSet<Territory> adjacentTerritories;
    private Vec3 center;
    private Player owner;
    private int nArmies;

    public Territory(int id, World world, Continent continent, HashSet<Territory> adjacentTerritories, Vec3 center) {
        this.id = id;
        this.territoryName = faker.address().country();
        this.world = world;
        this.continent = continent;
        this.adjacentTerritories = adjacentTerritories;
        this.center = center;
        nArmies = 0;
    }

    public void setOwner(Player player) {
        owner = player;
    }

    public void setNArmies(int n) {
        nArmies = n;

        world.setArmyChange();
    }

    public boolean isSelected() {
        return world.getSelectedTerritory() == this;
    }

    public boolean isHighlighted() {
        return world.getHighlightedTerritories().contains(this);
    }

    public int getId() {
        return id;
    }

    public String getTerritoryName() {
        return territoryName;
    }


    public Continent getContinent() {
        return continent;
    }

    public HashSet<Territory> getAdjacentTerritories() {
        return adjacentTerritories;
    }

    // Returns a set of adjacent territories with the same owner, or null if none exist
    public HashSet<Territory> getAdjacentFriendlyTerritories() {
        HashSet<Territory> territories = new HashSet<>();
        for (Territory terr : adjacentTerritories) {
            if (terr.getOwner() == owner) {
                territories.add(terr);
            }
        }
        return territories.isEmpty() ? null : territories;
    }

    // Returns a set of adjacent territories with a different owner, or null if none exist
    public HashSet<Territory> getAdjacentEnemyTerritories() {
        HashSet<Territory> territories = new HashSet<>();
        for (Territory terr : adjacentTerritories) {
            if (terr.getOwner() == owner) {
                territories.add(terr);
            }
        }
        return territories.isEmpty() ? null : territories;
    }

    public Vec3 getCenter() {
        return center;
    }

    public Player getOwner() {
        return owner;
    }

    public int getNArmies() {
        return nArmies;
    }

}
