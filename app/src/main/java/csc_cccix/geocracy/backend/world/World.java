package csc_cccix.geocracy.backend.world;

import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.backend.game.Game;
import csc_cccix.geocracy.backend.graphics.Camera;
import csc_cccix.geocracy.backend.graphics.Mesh;
import csc_cccix.geocracy.backend.graphics.MeshMaker;
import glm_.vec3.Vec3;

public class World implements Serializable {

    private static final long serialVersionUID = 0L; // INCREMENT IF INSTANCE VARIABLES ARE CHANGED

    public static final int TESSELLATION_DEGREE = 5; // Should really not change
    private static final int MAX_N_TERRITORIES = 40; // Cannot be greater than 63
    private static final int MAX_N_CONTINENTS = 15; // Cannot be greater than 15

    // IF CHANGING INSTANCE VARIABLES, INCREMENT serialVersionUID !!!
    Game game;
    private long seed;
    private Territory[] territories;
    private Continent[] continents;
    // IF CHANGING INSTANCE VARIABLES, INCREMENT serialVersionUID !!!

    private transient Terrain terrain;
    private transient OceanRenderer oceanRenderer;
    private transient Waterways waterways;
    private transient ArmyRenderer armyRenderer;
    private transient ArrowRenderer arrowRenderer;
    private transient Territory selectedTerritory;
    private transient Territory targetedTerritory;
    private transient Set<Territory> highlightedTerritories;
    private transient boolean selectionChange;
    private transient boolean targetChange;
    private transient boolean highlightChange;
    private transient boolean ownershipChange;
    private transient float[] ownershipChangeTimes;
    private transient boolean isOwnershipChangeInProgress;
    private transient boolean armyChange;

    public World(Game game, long seed) {
        this.game = game;
        this.seed = seed;

        constructTransient(true);
    }

    // Called during deserialization
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        constructTransient(false);
    }

    private void constructTransient(boolean isNew) {
        Mesh sphereMesh = MeshMaker.makeSphereIndexed("World", TESSELLATION_DEGREE);
        terrain = new Terrain(this, sphereMesh, seed, MAX_N_TERRITORIES, MAX_N_CONTINENTS);
        if (isNew) {
            Pair<Territory[], Continent[]> pair = terrain.retrieveTerrsConts();
            territories = pair.first;
            continents = pair.second;
        }
        oceanRenderer = new OceanRenderer(sphereMesh);
        waterways = terrain.createWaterways(100, 0.025f);
        armyRenderer = new ArmyRenderer(this);
        arrowRenderer = new ArrowRenderer(this, 100);
        highlightedTerritories = new HashSet<>();
        selectionChange = true;
        targetChange = true;
        highlightChange = true;
        ownershipChange = true;
        ownershipChangeTimes = new float[64];
        Arrays.fill(ownershipChangeTimes, 1.0f);
        isOwnershipChangeInProgress = true;
        armyChange = true;
    }

    public boolean load() {
        unload();

        if (Util.isGLError()) {
            return false;
        }

        if (!terrain.load()) {
            Log.e("", "Failed to load terrain");
            return false;
        }
        if (!oceanRenderer.load()) {
            Log.e("", "Failed to load ocean renderer");
            return false;
        }
        if (!waterways.load()) {
            Log.e("", "Failed to load waterway renderer");
            return false;
        }
        if (!armyRenderer.load()) {
            Log.e("", "Failed to load army renderer");
            return false;
        }
        if (!arrowRenderer.load()) {
            Log.e("", "Failed to load arrow renderer");
            return false;
        }

        return true;
    }

    public synchronized void render(long t, float dt, Camera camera, Vec3 lightDir, int cubemapHandle) {
        terrain.render(t, camera, lightDir, selectionChange, targetChange, highlightChange, ownershipChange, isOwnershipChangeInProgress ? ownershipChangeTimes : null);
        oceanRenderer.render(camera, lightDir, cubemapHandle);
        waterways.render(t, camera, lightDir, selectionChange);
        armyRenderer.render(camera, lightDir, armyChange, ownershipChange);
        if (targetedTerritory != null && selectedTerritory != null) {
            if (selectionChange || targetChange) {
                arrowRenderer.set(selectedTerritory.getCenter(), targetedTerritory.getCenter());
            }
            arrowRenderer.render(t, camera);
        }

        selectionChange = false;
        targetChange = false;
        highlightChange = false;
        armyChange = false;
        ownershipChange = false;
        if (isOwnershipChangeInProgress) {
            isOwnershipChangeInProgress = false;
            for (int i = 0; i < ownershipChangeTimes.length; ++i) {
                ownershipChangeTimes[i] += dt;
                if (ownershipChangeTimes[i] < 1.0f) {
                    isOwnershipChangeInProgress = true;
                }
                else {
                    ownershipChangeTimes[i] = 1.0f;
                }
            }
        }
    }

    public void renderId(Camera camera) {
        terrain.renderId(camera);
    }

    public void unload() {
        terrain.unload();
        oceanRenderer.unload();
        waterways.unload();
        armyRenderer.unload();
    }

    public synchronized void selectTerritory(Territory territory) {
        if (selectedTerritory != territory) {
            selectedTerritory = territory;
            selectionChange = true;
        }
    }

    public synchronized void unselectTerritory() {
        if (selectedTerritory != null) {
            selectedTerritory = null;
            selectionChange = true;
        }
    }

    public synchronized void targetTerritory(Territory territory) {
        if (targetedTerritory != territory) {
            targetedTerritory = territory;
            targetChange = true;
        }
    }

    public synchronized void untargetTerritory() {
        if (targetedTerritory != null) {
            targetedTerritory = null;
            targetChange = true;
        }
    }

    public synchronized void highlightTerritory(Territory territory) {
        if (!highlightedTerritories.contains(territory)) {
            highlightedTerritories.add(territory);
            highlightChange = true;
        }
    }

    public synchronized void highlightTerritories(Set<Territory> territories) {
        if (territories != null) {
            for (Territory terr : territories) {
                highlightTerritory(terr);
            }
        }
    }

    public synchronized void unhighlightTerritories() {
        if (!highlightedTerritories.isEmpty()) {
            highlightedTerritories.clear();
            highlightChange = true;
        }
    }

    public List<Territory> getUnoccupiedTerritories(){
        List<Territory> unOccTerrs = new ArrayList<>();
        for(Territory terr : territories)
            if(terr.getOwner()==null)
                unOccTerrs.add(terr);

        return unOccTerrs;
    }

    public Territory getUnoccTerritory(int id){
        List<Territory> unOccTerrs = getUnoccupiedTerritories();
        if (id <= 0 || id > unOccTerrs.size()) {
            return null;
        }
        Territory terr = unOccTerrs.get(id-1);
        unOccTerrs.remove(id - 1);
        return terr;
    }

    public Territory getRandomUnoccTerritory(){
        List<Territory> unOccTerrs = getUnoccupiedTerritories();
        if (unOccTerrs.isEmpty()) {
            return null;
        }
        Random rand = new Random();
        int id = rand.nextInt(unOccTerrs.size());
        Territory terr = unOccTerrs.get(id);
        unOccTerrs.remove(id);
        return terr;
    }

    public Territory[] getTerritories() {
        return territories;
    }

    public Territory getTerritory(int id) {
        if (id <= 0 || id > territories.length) {
            return null;
        }
        return territories[id - 1];
    }

    public boolean allTerritoriesOccupied() {
        for (Territory terr : territories)
            if(terr.getOwner()==null)
                return false;
        return true;
    }

    public int getNTerritories() {
        return territories.length;
    }

    public Continent[] getContinents() {
        return continents;
    }

    public int getNContinents() {
        return continents.length;
    }

    public synchronized Territory getSelectedTerritory() {
        return selectedTerritory;
    }

    public synchronized Territory getTargetedTerritory() {
        return targetedTerritory;
    }

    public synchronized Set<Territory> getHighlightedTerritories() {
        return highlightedTerritories;
    }

    public int getTotalNArmies() {
        int nArmies = 0;
        for (Territory terr : territories) nArmies += terr.getNArmies();
        return nArmies;
    }

    public long getSeed() {
        return seed;
    }

    Terrain getTerrain() {
        return terrain;
    }

    synchronized void setOwnershipChange(Territory territory) {
        ownershipChange = true;
        ownershipChangeTimes[territory.getId()] = 0.0f;
        isOwnershipChangeInProgress = true;
    }

    synchronized void setArmyChange() {
        armyChange = true;
    }

}
