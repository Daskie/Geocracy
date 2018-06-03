package csc_cccix.geocracy;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class GameSaves {

    // Save Game Save
    public static void saveObjectToSharedPreference(Context context, String preferenceFileName, String serializedObjectKey, Object object) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        final Gson gson = new Gson();
        String serializedObject = gson.toJson(object);
        sharedPreferencesEditor.putString(serializedObjectKey, serializedObject);
        sharedPreferencesEditor.apply();
    }

    // Load Game Save
    public static <T> T getSavedObjectFromPreference(Context context, String preferenceFileName, String preferenceKey, Class<T> classType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        if (sharedPreferences.contains(preferenceKey)) {
            final Gson gson = new Gson();
            return gson.fromJson(sharedPreferences.getString(preferenceKey, ""), classType);
        }
        return null;
    }

    // Delete Game Save
    public static void deleteObjectFromSharedPreference(Context context, String preferenceFileName, String serializedObjectKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.remove(serializedObjectKey);
        sharedPreferencesEditor.apply();
    }

}
