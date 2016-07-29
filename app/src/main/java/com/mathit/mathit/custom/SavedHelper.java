package com.mathit.mathit.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for retrieving and saving custom equations for easy input
 * @author Aaron Vontell
 * @version 0.1
 */
public class SavedHelper {

    private static final String PREFS_NAME = "com.mathit.mathit.PREFS_STORED_KEY";
    private static final String COUNT_KEY = "com.mathit.mathit.EQUATION_SAVED_COUNTER";
    private static final String EQUATION_TEXT_KEY = "com.mathit.mathit.EQUATION_TEXT-%d";
    private static final String EQUATION_IMAGE_KEY = "com.mathit.mathit.EQUATION_IMAGE-%d";
    private static final String EQUATION_SHOULD_SKIP = "com.mathit.mathit.SKIP_EQUATION-%d";

    private SharedPreferences.Editor editor;
    private SharedPreferences keyVal;

    /**
     * Creates the SavedHelper that will save and retrieve LaTeX inputs
     * @param context The calling activity
     */
    public SavedHelper(Context context) {

        keyVal = context.getSharedPreferences(PREFS_NAME, 0);
        editor = keyVal.edit();
    }

    /**
     * Saves the given equation to SharedPreferences
     * @param image The image of the LaTeX
     * @param latex The text input of the LaTeX string
     */
    public void saveEquation(Bitmap image, String latex) {

        // First, grab the count of equations and increment it
        int count = keyVal.getInt(COUNT_KEY, 0);

        // Create the formatted string keys to use
        String bitmapEncoding = encodeToBase64(image);
        String textKey = String.format(EQUATION_TEXT_KEY, count);
        String imageKey = String.format(EQUATION_IMAGE_KEY, count);
        String shouldSkip = String.format(EQUATION_SHOULD_SKIP, count);

        editor.putString(textKey, latex);
        editor.putString(imageKey, bitmapEncoding);
        editor.putBoolean(shouldSkip, false);

        count++;
        editor.putInt(COUNT_KEY, count);

        editor.apply();

    }

    /**
     * Delete a saved LaTeX equation with the given indexKey
     * @param indexKey the index of the equation to delete. Note that this is not the same
     *                 id as the saved equation in the layout it is presented in
     */
    public void deleteEquation(int indexKey) {

        // Create the keys to access equation info
        String textKey = String.format(EQUATION_TEXT_KEY, indexKey);
        String imageKey = String.format(EQUATION_IMAGE_KEY, indexKey);
        String shouldSkip = String.format(EQUATION_SHOULD_SKIP, indexKey);

        // Remove the information and mark for skipping
        editor.remove(textKey);
        editor.remove(imageKey);
        editor.putBoolean(shouldSkip, true);
        editor.apply();

    }

    /**
     * Gets the list of saved equations within shared preferences
     * @return the list of saved equations
     */
    public List<SavedLatexEquation> getSavedEquations() {

        List<SavedLatexEquation> equations = new ArrayList<>();

        // Get the count of equations (including deleted equations)
        int count = keyVal.getInt(COUNT_KEY, 0);

        // Iterate through the equations
        for(int i = 0; i < count; i++) {

            // If skip, then skip it!
            String skipKey = String.format(EQUATION_SHOULD_SKIP, i);
            boolean skip = keyVal.getBoolean(skipKey, true);
            if(!skip) {
                String textKey = String.format(EQUATION_TEXT_KEY, i);
                String imageKey = String.format(EQUATION_IMAGE_KEY, i);

                String latexInput = keyVal.getString(textKey, "Equation\\ Error");
                Bitmap latexImage = decodeBase64(keyVal.getString(imageKey, ""));

                SavedLatexEquation equation = new SavedLatexEquation(i, latexInput, latexImage);
                equations.add(equation);

            }

        }

        return equations;

    }

    private String encodeToBase64(Bitmap image) {

        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);

    }

    private Bitmap decodeBase64(String input) {

        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

    }

}
