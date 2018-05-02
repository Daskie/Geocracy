package csc309.geocracy.world;

import java.util.HashSet;

public class Territory {

    private int id;
    private World world;
    private Continent continent;
    private HashSet<Territory> adjacentTerritories;
    private boolean isSelected;
    private boolean isHighlighted;

    public Territory(int id, World world, Continent continent, HashSet<Territory> adjacentTerritories) {
        this.id = id;
        this.world = world;
        this.continent = continent;
        this.adjacentTerritories = adjacentTerritories;
        isSelected = false;
        isHighlighted = false;
    }

    // Sets this territory as selected
    public void select() {
        if (!isSelected) {
            deselect();
            isSelected = true;
            world.getTerrain().selectedTerritoryChanged();
        }
    }

    public void highlight() {
        if (!isHighlighted) {
            deselect();
            isHighlighted = true;
            world.getTerrain().highlightedTerritoriesChanged();
        }
    }

    public void deselect() {
        if (isSelected) {
            isSelected = false;
            world.getTerrain().selectedTerritoryChanged();
        }
        if (isHighlighted) {
            isHighlighted = false;
            world.getTerrain().highlightedTerritoriesChanged();
        }
    }

    // Sets all friendly adjacent territories as selected
    public void highlightFriendlyAdjacent() {
        // TODO
    }

    // Sets all hostile adjacent territories as selected
    public void highlightHostileAdjacent() {
        // TODO
    }

    // Sets all adjacent territories as selected
    public void highlightAllAdjacent() {
        for (Territory terr : adjacentTerritories) terr.highlight();
    }

    public int getID() {
        return id;
    }

    public Continent getContinent() {
        return continent;
    }

    public HashSet<Territory> getAdjacentTerritories() {
        return adjacentTerritories;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

}
