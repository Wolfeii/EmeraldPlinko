package com.wolfeiii.emeraldplinko.utils;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    private static final Random random = new Random();

    public static double generateRandomDouble(int min, int max, int decimalPoints) {
        double randomDouble = ThreadLocalRandom.current().nextDouble(min, max);
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(decimalPoints);
        return Double.parseDouble(decimalFormat.format(randomDouble));
    }


}
