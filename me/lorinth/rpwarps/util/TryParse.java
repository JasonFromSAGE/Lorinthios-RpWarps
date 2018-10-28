package me.lorinth.rpwarps.util;

import org.bukkit.Material;

public class TryParse {

    public static boolean parseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean parseDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean parseFloat(String value) {
        try {
            Float.parseFloat(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean parseBoolean(String value){
        try {
            Boolean.parseBoolean(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean parseMaterial(String value){
        try {
            Material mat = Material.valueOf(value);
            return mat != null;
        } catch (Exception e) {
            return false;
        }
    }

}
