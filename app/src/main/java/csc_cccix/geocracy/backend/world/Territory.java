package csc_cccix.geocracy.backend.world;

import com.github.javafaker.Faker;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.backend.Game;
import csc_cccix.geocracy.backend.Player;
import glm_.vec3.Vec3;

public class Territory {

    private static int fakerSeed = 1;
    private static Faker faker = new Faker(new Random(fakerSeed));

    private int id; // starts at 1. 0 indicates no territory
    private String territoryName;
    private World world;
    private Continent continent;
    private Set<Territory> adjacentTerritories;
    private Vec3 center;
    private Player owner;
    private int nArmies;

    public Territory(int id, World world, Continent continent, Set<Territory> adjacentTerritories, Vec3 center) {
        this.id = id;
        this.territoryName = faker.address().country();
        this.world = world;
        this.continent = continent;
        this.adjacentTerritories = adjacentTerritories;
        this.center = center;
        nArmies = 0;
    }

    public void select() {
        world.selectTerritory(this);
    }

    public void target() {
        world.targetTerritory(this);
    }

    public void highlight() {
        world.highlightTerritory(this);
    }

    public void setOwner(Player player) {
        if (player == owner) {
            return;
        }

        Player prevOwner = owner;
        owner = player;
        continent.ownershipChange();

        if (prevOwner != null) prevOwner.removeTerritory(this);
        owner.addTerritory(this);

        world.setOwnershipChange(this);
    }

    public void setNArmies(int n) {

        int clampedNArmies = Util.clamp(n, 0, Game.MAX_ARMIES_PER_TERRITORY);

        if (n <= clampedNArmies) {
            nArmies = clampedNArmies;
            world.setArmyChange();
        }

    }

    public boolean isSelected() {
        return world.getSelectedTerritory() == this;
    }

    public boolean isTargeted() {
        return world.getTargetedTerritory() == this;
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

    public Set<Territory> getAdjacentTerritories() {
        return adjacentTerritories;
    }

    // Returns a set of adjacent territories with the same owner, or null if none exist
    public Set<Territory> getAdjacentFriendlyTerritories() {
        Set<Territory> territories = new HashSet<>();
        for (Territory terr : adjacentTerritories) {
            if (terr.getOwner() == owner) {
                territories.add(terr);
            }
        }
        return territories.isEmpty() ? null : territories;
    }

    // Returns a set of adjacent territories with a different owner, or null if none exist
    public Set<Territory> getAdjacentEnemyTerritories() {
        Set<Territory> territories = new HashSet<>();
        for (Territory terr : adjacentTerritories) {
            if (terr.getOwner() != owner) {
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

    public boolean hasOwner() {
        return owner != null;
    }

    public int getNArmies() {
        return nArmies;
    }

}
