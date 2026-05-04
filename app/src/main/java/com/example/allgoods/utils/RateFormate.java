package com.example.allgoods.utils;

import java.util.Locale;

public class RateFormate{

    public static String formateRating(double rating){
        if (rating == (long) rating){
            return String.format(Locale.ENGLISH, "%d", (long) rating);
        }else {
            return String.format(Locale.ENGLISH, "%.1f", rating);
        }
    }

}
