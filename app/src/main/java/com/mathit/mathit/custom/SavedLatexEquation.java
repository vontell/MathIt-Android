package com.mathit.mathit.custom;

import android.graphics.Bitmap;

/**
 * Represents a saved Latex object
 * @author Aaron Vontell
 * @version 0.1
 */
public class SavedLatexEquation {

    private int index;
    private String latexInput;
    private Bitmap image;

    /**
     * Creates a new latex image that was saved to the device
     * @param index The index of the equation within the shared preferences
     * @param latexInput The string of LaTeX code that will generate the image
     * @param image The image representation of the LateX code
     */
    public SavedLatexEquation(int index, String latexInput, Bitmap image) {
        this.index = index;
        this.latexInput = latexInput;
        this.image = image;
    }

    /**
     * Get the index of the equation within shared preferences
     * @return The index of the equation within shared preferences
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the string of LaTeX code that generates the image
     * @return The string of LaTeX code that generates the image
     */
    public String getLatexInput() {
        return latexInput;
    }

    /**
     * Get the image that is generated when parsing latexInput
     * @return the image displaying the parsed LaTeX code
     */
    public Bitmap getImage() {
        return image;
    }
}
