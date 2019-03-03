package csc_cccix.geocracy.backend.game;

import csc_cccix.geocracy.backend.game.Player;
import glm_.vec3.Vec3;

public class HumanPlayer extends Player {

    private static final long serialVersionUID = 0L; // INCREMENT IF INSTANCE VARIABLES ARE CHANGED

    public HumanPlayer(String name, int id, Vec3 color) {
        super(id, color);
        super.name = name;
    }

}
