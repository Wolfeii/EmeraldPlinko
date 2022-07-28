package com.wolfeiii.emeraldplinko.utils;

import java.text.DecimalFormat;

public class NumberUtils {

    public static boolean isClose(double integerOne, double integerTwo, double scale) {
        return Math.max(integerOne, integerTwo) - Math.min(integerOne, integerTwo) <= scale;
    }

    public static double roundTo(double number, int decimalPoints) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(decimalPoints);
        return Double.parseDouble(decimalFormat.format(number));
    }
}
