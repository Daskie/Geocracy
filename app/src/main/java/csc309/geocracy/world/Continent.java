package csc309.geocracy.world;

import java.util.HashSet;

import glm_.vec3.Vec3;

public class Continent {

    private int id;
    private World world;
    private HashSet<Territory> territories;
    private Vec3 color;

    public Continent(int id, World world, HashSet<Territory> territories, Vec3 color) {
        this.id = id;
        this.world = world;
        this.territories = territories;
        this.color = color;
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
