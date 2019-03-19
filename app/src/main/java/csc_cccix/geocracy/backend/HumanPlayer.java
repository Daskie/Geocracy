package csc_cccix.geocracy.backend;

import glm_.vec3.Vec3;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, int id, Vec3 color) {
        super(id, color);
        super.name = name;
    }

}
