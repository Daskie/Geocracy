package csc_cccix.geocracy.backend.world;

import java.util.Iterator;
import java.util.Set;

import csc_cccix.geocracy.backend.Player;
import glm_.vec3.Vec3;

import static glm_.Java.glm;

public class Continent {

    private int id; // starts at 1. 0 indicates no continent
    private World world;
    private Set<Territory> territories;
    private Vec3 color;
    private int bonus;
    private Player owner;

    public Continent(int id, World world, Set<Territory> territories, Vec3 color) {
        this.id = id;
        this.world = world;
        this.territories = territories;
        this.color = color;
        this.bonus = glm.max(this.territories.size() / 2, 1);
    }

    // Returns the player that possesses all the continent's territories, or null if no such player exists
    public Player getOwner() {
        return owner;
    }

    public int getId() {
        return id;
    }

    public Set<Territory> getTerritories() {
        return territories;
    }

    public Vec3 getColor() {
        return color;
    }

    public int getBonus() {
        return bonus;
    }

    void ownershipChange() {
        Iterator<Territory> it = territories.iterator();
        owner = it.next().getOwner();
        while (it.hasNext()) {
            if (it.next().getOwner() != owner) {
                owner = null;
                break;
            }
        }
    }

}