package com.mathit.mathit;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.MessengerThreadParams;
import com.facebook.messenger.ShareToMessengerParams;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MainActivity that shows an interface to create LaTeX images
 * @author Aaron Vontell
 * @version 7.27.2016
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /*
    private static final int REQUEST_CODE_SHARE_TO_MESSENGER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.mathit.mathit",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        View fbButton = findViewById(R.id.button_layout);
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendImage();
            }
        });

    }
    */

    /*
     * Sends the created latex image to FB Messenger
     *
    private void sendImage() {

        String mimeType = "image/jpeg";
        Uri contentUri = Uri.parse("android.resource://com.mathit.mathit/drawable/astro");

        // contentUri points to the content being shared to Messenger
        ShareToMessengerParams shareToMessengerParams =
                ShareToMessengerParams.newBuilder(contentUri, mimeType)
                        .build();

        // Sharing from an Activity
        MessengerUtils.shareToMessenger(
                this,
                REQUEST_CODE_SHARE_TO_MESSENGER,
                shareToMessengerParams);

    }*/

    private String doubleEscapeTeX(String s) {
        String t="";
        for (int i=0; i < s.length(); i++) {
            if (s.charAt(i) == '\'') t += '\\';
            if (s.charAt(i) != '\n') t += s.charAt(i);
            if (s.charAt(i) == '\\') t += "\\";
        }
        return t;
    }

    public void onClick(View v) {
        if (v == findViewById(R.id.button2)) {
            WebView w = (WebView) findViewById(R.id.webView);
            EditText e = (EditText) findViewById(R.id.edit);
            w.loadUrl("javascript:document.getElementById('math').innerHTML='\\\\["
                    +doubleEscapeTeX(e.getText().toString())+"\\\\]';");
            w.loadUrl("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);");
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView w = (WebView) findViewById(R.id.webView);
        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setBuiltInZoomControls(false);
        w.loadDataWithBaseURL("http://bar", "<script type='text/x-mathjax-config'>"
                +"MathJax.Hub.Config({ "
                +"showMathMenu: false, "
                +"jax: ['input/TeX','output/HTML-CSS'], "
                +"extensions: ['tex2jax.js'], "
                +"TeX: { extensions: ['AMSmath.js','AMSsymbols.js',"
                +"'noErrors.js','noUndefined.js'] } "
                +"});</script>"
                +"<script type='text/javascript' "
                +"src='file:///android_asset/MathJax/MathJax.js'"
                +"></script><span id='math'></span>","text/html","utf-8","");
        /*String newurl = "http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML";
        w.loadDataWithBaseURL("http://bar","<script type='text/javascript' "
                +"src='"+newurl+"'"
                +"></script><span id='math'></span>","text/html","utf-8","");*/
        EditText e = (EditText) findViewById(R.id.edit);
        e.setBackgroundColor(Color.LTGRAY);
        e.setTextColor(Color.BLACK);
        e.setText("");
        Button b = (Button) findViewById(R.id.button2);
        b.setOnClickListener(this);

    }

}
