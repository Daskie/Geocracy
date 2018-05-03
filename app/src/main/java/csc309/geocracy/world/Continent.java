package csc309.geocracy.world;

import java.util.HashSet;

public class Continent {

    private int id;
    private World world;
    private HashSet<Territory> territories;

    public Continent(int id, World world, HashSet<Territory> territories) {
        this.id = id;
        this.world = world;
        this.territories = territories;
    }

    public int getID() {
        return id;
    }

    public HashSet<Territory> getTerritories() {
        return territories;
    }

}
