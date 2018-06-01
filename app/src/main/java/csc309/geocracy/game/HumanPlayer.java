package csc309.geocracy.game;

import com.github.javafaker.Faker;

import java.util.Random;

import glm_.vec3.Vec3;

public class HumanPlayer extends Player{

    private static int fakerSeed = 1;
    private static Faker faker = new Faker(new Random(fakerSeed));

    public HumanPlayer(int id, Vec3 color) {
        super(id, color);

        super.name = faker.name().fullName();

    }

}
