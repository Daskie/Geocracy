package csc_cccix.geocracy.game;

import com.github.javafaker.Faker;

import java.util.Random;

import glm_.vec3.Vec3;

public class HumanPlayer extends Player {

    private static final long serialVersionUID = 0L; // INCREMENT IF INSTANCE VARIABLES ARE CHANGED

    private static int fakerSeed = 1;

    public HumanPlayer(String name, int id, Vec3 color) {
        super(id, color);
        super.name = name;
    }

}
