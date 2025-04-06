
package com.example.appli20240829;

import java.util.HashMap;

public class DonneesPartagees {
    private static String URLConnexion = "";
    public static int customerId = -1;

    // Nouveau panier avec FilmPanierItem
    public static HashMap<FilmPanierItem, Integer> panierGlobal = new HashMap<>();

    public static String getURLConnexion() {
        return URLConnexion;
    }

    public static void setURLConnexion(String URLConnexion) {
        DonneesPartagees.URLConnexion = URLConnexion;
    }

    public static void setCustomerId(int id) {
        DonneesPartagees.customerId = id;
    }

    public static int getCustomerId() {
        return DonneesPartagees.customerId;
    }
}
