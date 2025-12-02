package com.example.biblioteca_digital;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class FavoritesManager {

    private static final String PREF_NAME = "favorites_prefs";
    private static final String KEY_FAVORITES = "favorites_list";

    public static void addFavorite(Context context, String recipeId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> favs = prefs.getStringSet(KEY_FAVORITES, new HashSet<>());
        favs.add(recipeId);
        prefs.edit().putStringSet(KEY_FAVORITES, favs).apply();
    }

    public static void removeFavorite(Context context, String recipeId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> favs = prefs.getStringSet(KEY_FAVORITES, new HashSet<>());
        favs.remove(recipeId);
        prefs.edit().putStringSet(KEY_FAVORITES, favs).apply();
    }

    public static boolean isFavorite(Context context, String recipeId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> favs = prefs.getStringSet(KEY_FAVORITES, new HashSet<>());
        return favs.contains(recipeId);
    }

    public static Set<String> getFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getStringSet(KEY_FAVORITES, new HashSet<>());
    }
}
