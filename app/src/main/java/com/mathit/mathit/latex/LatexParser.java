package com.mathit.mathit.latex;

import android.content.Context;
import java.io.File;

/**
 * Simple abstraction that provides helper methods to display latex from a string
 * @author Aaron Vontell
 * @version 7.27.2016
 */
public class LatexParser {

    /**
     * Escapes the received string for use in LaTeX
     * @param latexString The string to escape
     * @return latexString escaped and ready for MathJax
     */
    public static String doubleEscapeTeX(String latexString) {
        String result = "";
        for (int i=0; i < latexString.length(); i++) {
            if (latexString.charAt(i) == '\'') result += '\\';
            if (latexString.charAt(i) != '\n') result += latexString.charAt(i);
            if (latexString.charAt(i) == '\\') result += "\\";
        }
        return result;
    }

}
