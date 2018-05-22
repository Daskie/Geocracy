package csc309.geocracy.world;

import java.util.HashSet;
import java.util.Iterator;

import csc309.geocracy.game.Player;
import glm_.vec3.Vec3;

public class Continent {

    private int id; // starts at 1. 0 indicates no continent
    private World world;
    private HashSet<Territory> territories;
    private Vec3 color;

    public Continent(int id, World world, HashSet<Territory> territories, Vec3 color) {
        this.id = id;
        this.world = world;
        this.territories = territories;
        this.color = color;
    }

    // Returns the player that possesses all the continent's territories, or null if no such player exists
    public Player getOwner() {
        Iterator<Territory> it = territories.iterator();
        Player owner = it.next().getOwner();
        while (it.hasNext()) {
            if (it.next().getOwner() != owner) {
                return null;
            }
        }
        return owner;
    }

    public int getId() {
        return id;
    }

    public HashSet<Territory> getTerritories() {
        return territories;
    }

    public Vec3 getColor() {
        return color;
    }

}
