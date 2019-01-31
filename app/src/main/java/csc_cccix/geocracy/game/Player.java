package csc_cccix.geocracy.game;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import csc_cccix.geocracy.SerializableVec3;
import csc_cccix.geocracy.world.Continent;
import csc_cccix.geocracy.world.Territory;
import glm_.vec3.Vec3;

import static glm_.Java.glm;

public class Player implements Serializable {

    private static final long serialVersionUID = 0L; // INCREMENT IF INSTANCE VARIABLES ARE CHANGED

    private int id; // starts at 1. 0 indicates no player
    public String name;
    private Set<Territory> territories;
    private SerializableVec3 color;
    private int armyPool;
    private int armies;
    private Set<Continent> ownedContinents; // which continents the player owns all territories of
    private int bonus;
    private int numArmiesAttacking;
    private int[] die;

    public Player(int id, Vec3 color) {
        this.id = id;
        this.color = new SerializableVec3(new Vec3(color));
        this.territories = new HashSet<>();
        this.armies = 0;
        this.ownedContinents = new HashSet<>();
        this.die = new int[] {-1,-1,-1};
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
        this.ownedContinents.remove(territory.getContinent());
        calcBonus();
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Vec3 getColor() { return color.get(); }
    public Set<Territory> getTerritories() {
        return territories;
    }
    public int getNTerritories() {return territories.size();}
    public int getNArmies(){
        return this.armies;
    }
    public int getArmyPool(){
        return this.armyPool;
    }
    public int getBonus() {
        return bonus;
    }

    public void setArmyPool(int poolSize){
        this.armyPool = poolSize;
    }

    public void addOrRemoveNArmies(int numArmies){
        this.armies += numArmies;
    }
    public void addOrRemoveNArmiesToPool(int numArmies){
        this.armyPool += numArmies;
    }

    private void calcBonus() {
        this.bonus = glm.max(this.territories.size() / 3, 3);
        for (Continent continent : this.ownedContinents) {
            this.bonus += continent.getBonus();
        }
    }

    // Returns the owners territory that contains the most units
    public Territory findTerrWithMaxArmies(){
        int max = 2;
        Territory maxTerr = null;
        for(Territory terr: territories){
            if(terr.getNArmies()>=max){
                maxTerr = terr;
                max = terr.getNArmies();
            }
        }

        return maxTerr;
    }

    public boolean placeUnitsInOwnedTerritory(Territory territory, int unitCount) {
        if (this.territories.contains(territory)) {
            if (getArmyPool() >= unitCount ) {
                setArmyPool(getArmyPool() - unitCount);
                territory.setNArmies(territory.getNArmies() + unitCount);
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.name + "\n" + this.territories.size();
    }
}
