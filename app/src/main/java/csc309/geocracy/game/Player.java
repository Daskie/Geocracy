package csc309.geocracy.game;

import java.util.HashSet;

import csc309.geocracy.world.Continent;
import csc309.geocracy.world.Territory;
import glm_.vec3.Vec3;

import static glm_.Java.glm;

public abstract class Player {
    private int id; // starts at 1. 0 indicates no player
    public String name;
    private HashSet<Territory> territories;
    private Vec3 color;
    private int armies;
    private HashSet<Continent> ownedContinents; // which continents the player owns all territories of
    private int bonus;

    public Player(int id, Vec3 color) {
        this.id = id;
        this.color = new Vec3(color);
        this.territories = new HashSet<>();
        this.armies = 0;
        this.ownedContinents = new HashSet<>();
    }

    // Called by Territory.setOwner
    public void addTerritory(Territory territory) {
        this.territories.add(territory);
        if (territory.getContinent().getOwner() == this) {
            ownedContinents.add(territory.getContinent());
        }
        calcBonus();
    }

    // Called by Territory.setOwner
    public void removeTerritory(Territory territory) {
        this.territories.remove(territory);
        ownedContinents.remove(territory.getContinent());
        calcBonus();
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

    public void addNArmies(int numArmies){
        this.armies += numArmies;
    }

    public void removeNArmies(int numArmies){
        this.armies -= numArmies;
    }

    public int getNArmies(){
        return this.armies;
    }

    public int getBonus() {
        return bonus;
    }

    private void calcBonus() {
        bonus = glm.max(territories.size() / 3, 3);
        for (Continent continent : ownedContinents) {
            bonus += continent.getBonus();
        }
    }

    @Override
    public String toString() {
        return this.name + "\n" + this.territories.size();
    }
}
