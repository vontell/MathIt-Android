package com.mathit.mathit;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.MessengerThreadParams;
import com.facebook.messenger.ShareToMessengerParams;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

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

    /**
     * Sends the created latex image to FB Messenger
     */
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

    }

}
