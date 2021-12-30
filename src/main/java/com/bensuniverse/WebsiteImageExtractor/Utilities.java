package com.bensuniverse.WebsiteImageExtractor;

public class Utilities {

    public static int clamp(int var, int min, int max) {

        if (var < min)
            var = min;

        if (var > max)
            var = max;

        return var;

    }

    public static double clamp(double var, double min, double max) {

        if (var < min)
            var = min;

        if (var > max)
            var = max;

        return var;

    }
}
