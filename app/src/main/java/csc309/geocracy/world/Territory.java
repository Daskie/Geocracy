package csc309.geocracy.world;

import java.util.HashSet;

import glm_.vec3.Vec3;

public class Territory {

    private int id;
    private World world;
    private Continent continent;
    private HashSet<Territory> adjacentTerritories;
    private Vec3 center;

    public Territory(int id, World world, Continent continent, HashSet<Territory> adjacentTerritories, Vec3 center) {
        this.id = id;
        this.world = world;
        this.continent = continent;
        this.adjacentTerritories = adjacentTerritories;
        this.center = center;
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

    public Continent getContinent() {
        return continent;
    }

    public HashSet<Territory> getAdjacentTerritories() {
        return adjacentTerritories;
    }

    public Vec3 getCenter() {
        return center;
    }

}
