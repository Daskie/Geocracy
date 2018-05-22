package csc309.geocracy.game;

import java.util.HashSet;

import csc309.geocracy.world.Territory;
import glm_.vec3.Vec3;

public class Player {

    private HashSet<Territory> territories;
    private Vec3 color;

    public Player(Vec3 color) {
        color = new Vec3(color);
    }

    public void addTerritory(Territory territory) {
        territories.add(territory);
    }

    public void removeTerritory(Territory territory) {
        territories.remove(territory);
    }

    public HashSet<Territory> getTerritories() {
        return territories;
    }

    public Vec3 getColor() {
        return color;
    }

}
