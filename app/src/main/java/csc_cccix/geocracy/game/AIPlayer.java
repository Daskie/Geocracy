package csc_cccix.geocracy.game;

import com.github.javafaker.Faker;

import glm_.vec3.Vec3;

public class AIPlayer extends Player {

    private static final long serialVersionUID = 0L; // INCREMENT IF INSTANCE VARIABLES ARE CHANGED
    private static Faker faker = new Faker();

    public AIPlayer(int id, Vec3 color) {
        super(id, color);
        super.name = "CPU: " + faker.name().lastName();
    }
}
