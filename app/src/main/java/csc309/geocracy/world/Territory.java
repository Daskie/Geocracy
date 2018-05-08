package csc309.geocracy.world;

import java.util.HashSet;

public class Territory {

    private int id;
    private World world;
    private Continent continent;
    private HashSet<Territory> adjacentTerritories;

    public Territory(int id, World world, Continent continent, HashSet<Territory> adjacentTerritories) {
        this.id = id;
        this.world = world;
        this.continent = continent;
        this.adjacentTerritories = adjacentTerritories;
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

    public boolean isSelected() {
        return world.getSelectedTerritory() == this;
    }

    public boolean isHighlighted() {
        return world.getHighlightedTerritories().contains(this);
    }

}
