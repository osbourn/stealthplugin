package me.osbourn.stealthplugin.util;

import java.util.StringJoiner;

public class MaterialsUtil {
    private MaterialsUtil() {
    }

    public static String prettyMaterialName(String materialName) {
        String[] words = materialName.split("_");
        StringJoiner result = new StringJoiner(" ");
        for (String word : words) {
            if (word.length() > 1) {
                result.add(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }
}
