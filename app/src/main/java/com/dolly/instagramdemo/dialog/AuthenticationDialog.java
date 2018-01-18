package com.dolly.instagramdemo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.dolly.instagramdemo.R;
import com.dolly.instagramdemo.interfaces.AuthenticationListener;
import com.dolly.instagramdemo.utils.InstagramConstants;
import com.dolly.instagramdemo.utils.SharedPreferencesUtils;

// This is a dialog box with web view to authenticate the user
// after authentication we will get the access_token
public class AuthenticationDialog extends Dialog {

    private final AuthenticationListener listener;

    // authentication URL
    // https://www.instagram.com/developer/authentication/
    private final String url = InstagramConstants.BASE_URL
            + "oauth/authorize/?client_id="
            + InstagramConstants.CLIENT_ID
            + "&redirect_uri="
            + InstagramConstants.REDIRECT_URI
            + "&response_type=token"
            + "&display=touch";
    private Context context;
    private WebView webView;

    public AuthenticationDialog(@NonNull Context context, AuthenticationListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.auth_dialog);
        initializeWebView();
    }

    private void initializeWebView() {
        webView = findViewById(R.id.web_view);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (url.contains("#access_token=")) {
                    Uri uri = Uri.parse(url);
                    String access_token = uri.getEncodedFragment();
                    // get the whole token after the '=' sign
                    access_token = access_token.substring(access_token.lastIndexOf("=") + 1);
                    System.out.println("access_token = " + access_token);
                    // saving the token in SharedPreferences
                    SharedPreferencesUtils.saveDataInSharedPreferences(getContext(), access_token);
                    listener.onCodeReceived();
                    dismiss();

                } else if (url.contains("?error")) {
                    // here we are just showing the error occurred
                    Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });
    }


}
