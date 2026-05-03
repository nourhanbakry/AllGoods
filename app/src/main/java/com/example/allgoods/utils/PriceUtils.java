package com.example.allgoods.utils;

import java.util.Locale;

public class PriceUtils {
    public static String formatPrice(double price) {
        if (price == (long) price) {
            return String.format(Locale.ENGLISH, "%d", (long) price);
        } else {
            return String.format(Locale.ENGLISH, "%.2f", price);
        }
    }
}
