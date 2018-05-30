package csc309.geocracy.game;

import com.github.javafaker.Faker;

import java.util.Random;

import glm_.vec3.Vec3;

public class AIPlayer extends Player {
    private static int fakerSeed = 1;
    private static Faker faker = new Faker(new Random(fakerSeed));

    public AIPlayer(int id, Vec3 color) {
        super(id, color);
        super.name = "Computer " + faker.name().lastName();

    }
}
