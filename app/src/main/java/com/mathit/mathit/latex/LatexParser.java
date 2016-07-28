package com.mathit.mathit.latex;

import android.content.Context;
import java.io.File;

/**
 * Simple abstraction that provides methods to display latex from a string
 * @author Aaron Vontell
 * @version 7.27.2016
 */
public class LatexParser {

    /**
     * Create a test image with Latex symbols
     * @param context The calling activity
     * @return the File containing the image
     */
    public File createTestImage(Context context) {

        String testString = "\\frac{1}{\\sqrt{2}}(|0\\rangle + |1\\rangle)";
        return null;

    }

}
