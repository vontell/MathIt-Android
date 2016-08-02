package com.mathit.mathit;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
import com.mathit.mathit.custom.SavedHelper;
import com.mathit.mathit.custom.SavedLatexEquation;
import com.mathit.mathit.latex.LatexParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * MainActivity that shows an interface to create LaTeX images
 * @author Aaron Vontell
 * @version 7.27.2016
 */
public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_SHARE_TO_MESSENGER = 1;
    private static final String mathJaxURL = "http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML";
    private static final String DEFAULT_PATH = "lateximage.png";
    private static final String MIME_TYPE = "image/png";

    private WebView latexDisplay;
    private EditText inputEditText;
    private View fbButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instantiateViews();

        parseLatex();

    }

    @Override
    protected void onResume() {
        super.onResume();
        parseLatex();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // For situations such as a screen rotation
        parseLatex();

    }


    /**
     * Sends the created latex image to FB Messenger
     */
    private void sendImage() {


        Bitmap latexImage = getDisplayImage();
        File imageFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), DEFAULT_PATH);
        Uri contentUri = saveFile(latexImage, imageFile);

        if(contentUri == null) {
            // TODO: Display error dialog
        }

        // contentUri points to the content being shared to Messenger
        ShareToMessengerParams shareToMessengerParams =
                ShareToMessengerParams.newBuilder(contentUri, MIME_TYPE)
                        .build();

        // Sharing from an Activity
        MessengerUtils.shareToMessenger(
                this,
                REQUEST_CODE_SHARE_TO_MESSENGER,
                shareToMessengerParams);

    }

    /**
     * Grabs and loads the views within the main activity of this application
     */
    private void instantiateViews() {

        latexDisplay = (WebView) findViewById(R.id.latex_display);
        inputEditText = (EditText) findViewById(R.id.latex_input);
        fbButton = findViewById(R.id.fb_button);

        ImageView saveButton = (ImageView) findViewById(R.id.save_image);
        ImageView loadButton = (ImageView) findViewById(R.id.load_image);
        ImageView deleteButton = (ImageView) findViewById(R.id.delete_text);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEquation();
            }
        });
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySavedDialog();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputEditText.setText("");
            }
        });

        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                parseLatex();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendImage();
            }
        });

        latexDisplay.getSettings().setJavaScriptEnabled(true);
        latexDisplay.getSettings().setBuiltInZoomControls(false);
        latexDisplay.loadDataWithBaseURL("http://bar","<script type='text/javascript' "
                + "src='" + mathJaxURL + "'"
                + "></script><span id='math'></span>", "text/html", "utf-8", "");

    }

    /**
     * Creates a bitmap from the Latex Display
     * @return
     */
    private Bitmap getDisplayImage() {

        // Create the image from the Latex Display
        Bitmap bitmap = Bitmap.createBitmap(
                latexDisplay.getWidth(),
                latexDisplay.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        latexDisplay.draw(c);

        return bitmap;

    }

    /**
     * Saves the bitmap as a png to internal storage, and returns the URI to the file
     * @param bmp The image to save as a png
     * @param file The image file
     * @return
     */
    private Uri saveFile(Bitmap bmp, File file) {

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            Uri location = Uri.fromFile(file);
            Log.d("SEND", "Uri: " + location.toString());
            return location;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;

    }

    /**
     * Parses the latex from EditText and displays inside the web view
     */
    private void parseLatex() {

        String toParse = LatexParser.doubleEscapeTeX(inputEditText.getText().toString());
        if(toParse.equals("")) {
            toParse = "No\\\\ content";
        }

        // Load JS depending on the Android version, for compatibility
        // Then display the LaTeX within the WebView
        if (android.os.Build.VERSION.SDK_INT < 19) {
            latexDisplay.loadUrl("javascript:document.getElementById('math').innerHTML='\\\\["
                    + toParse + "\\\\]';", null);
            latexDisplay.loadUrl("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);", null);
        } else {

            latexDisplay.evaluateJavascript("javascript:document.getElementById('math').innerHTML='\\\\["
                    + toParse + "\\\\]';", null);
            latexDisplay.evaluateJavascript("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);", null);

        }

    }

    /**
     * Displays a dialog of equations that the user has saved, which
     * they can select as input. The selected equation gets inserted
     * into the current input at the cursor.
     */
    private void displaySavedDialog() {

        List<SavedLatexEquation> savedEquations = new SavedHelper(this).getSavedEquations();
        LayoutInflater inflater = LayoutInflater.from(this);

        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.saved_equations_title)
                .customView(R.layout.image_list, wrapInScrollView)
                .negativeText(R.string.saved_equations_cancel)
                .show();

        final ViewGroup imageList = (ViewGroup) dialog.getCustomView();

        for(SavedLatexEquation equation : savedEquations) {

            final View equationView = inflater.inflate(R.layout.saved_latex_view, null);
            ImageButton equationButton = (ImageButton) equationView.findViewById(R.id.equation_button);
            ImageView deleteButton = (ImageView) equationView.findViewById(R.id.delete_eq_button);
            equationButton.setImageBitmap(equation.getImage());

            final String input = equation.getLatexInput();
            final int index = equation.getIndex();
            equationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    insertEquationFrom(input);
                    dialog.cancel();
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new SavedHelper(view.getContext()).deleteEquation(index);
                    imageList.removeView(equationView);
                    imageList.invalidate();
                }
            });

            imageList.addView(equationView);

        }

    }

    /**
     * Insert the equation text into the current cursor pointer or selection
     * @param input The new latex input
     */
    private void insertEquationFrom(String input) {

        int start = Math.max(inputEditText.getSelectionStart(), 0);
        int end = Math.max(inputEditText.getSelectionEnd(), 0);
        inputEditText.getText().replace(Math.min(start, end), Math.max(start, end),
                input, 0, input.length());

    }

    /**
     * Saves the current equation to shared preferences
     */
    private void saveEquation() {

        SavedHelper saver = new SavedHelper(this);
        saver.saveEquation(getDisplayImage(), inputEditText.getEditableText().toString());
        new MaterialDialog.Builder(this)
                .title(R.string.save_confirm_title)
                .positiveText(R.string.save_confirm_accept)
                .content(R.string.save_confirm_content)
                .build().show();

    }

}
