package csc_cccix.geocracy;

import org.junit.Test;

import csc_cccix.geocracy.backend.game.Player;
import glm_.vec3.Vec3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlayerTest {

    @Test
    public void constructor() {
        int id = 2;
        Vec3 color = new Vec3(0.0f, 0.0f, 0.0f);
        Player player = new Player(id, color);
        assertEquals(id, player.getId());
        assertEquals(color, player.getColor());
        assertEquals(0, player.getNArmies());
        assertTrue(player.getOwnedTerritories().isEmpty());
    }

    @Test
    public void armyPool() {
        int id = 2;
        Vec3 color = new Vec3(0.0f, 0.0f, 0.0f);
        Player player = new Player(id, color);

        player.setArmyPool(10);
        assertEquals(10, player.getArmyPool());

        player.addOrRemoveNArmiesToPool(-5);
        assertEquals(5, player.getArmyPool());

        player.addOrRemoveNArmiesToPool(0);
        assertEquals(5, player.getArmyPool());

        player.addOrRemoveNArmiesToPool(3);
        assertEquals(8, player.getArmyPool());
    }


}