package org.example;

import org.example.generator.MapGenerator;
import org.example.generator.MapGeneratorImpl;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        MapGenerator generator = new MapGeneratorImpl(
                64,
                64,
                15,
                5,
                10,
                false,
                1,
                3
        );
        generator.genLevel();
        generator.genTilesLevel();
    }
}