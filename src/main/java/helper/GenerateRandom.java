package helper;

import java.util.Random;

public class GenerateRandom {

    public static int rndInt(int max) {
        Random rnd = new Random();
        return rnd.nextInt(max);
    }

}
