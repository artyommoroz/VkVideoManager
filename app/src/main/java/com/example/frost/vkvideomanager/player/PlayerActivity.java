package com.example.frost.vkvideomanager.player;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = "PlayerActivity";
    HTML5WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String videoUri = getIntent().getStringExtra("videoUri");
        Log.d(TAG, "onCreate: videoURL " + videoUri);
        webView = new HTML5WebView(this);

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.loadUrl(videoUri);
        }

        setContentView(webView.getLayout());
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        webView.stopLoading();
    }

    @Override
    public void onBackPressed() {
        if (webView.inCustomView()) {
            webView.hideCustomView();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }
}
