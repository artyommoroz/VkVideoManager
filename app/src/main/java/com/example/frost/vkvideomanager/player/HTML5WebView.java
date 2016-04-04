package com.example.frost.vkvideomanager.player;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.example.frost.vkvideomanager.R;


public class HTML5WebView extends android.webkit.WebView {
    private Context context;
    private MyWebChromeClient webChromeClient;
    private View customView;
    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;

    private FrameLayout layout;

    public HTML5WebView(Context context) {
        super(context);
        init(context);
    }

    public HTML5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HTML5WebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        Activity activity = (Activity) this.context;

        layout = new FrameLayout(context);

        FrameLayout browserFrameLayout = (FrameLayout) LayoutInflater.from(activity).inflate(R.layout.custom_screen, null);
        FrameLayout contentView = (FrameLayout) browserFrameLayout.findViewById(R.id.main_content);
        customViewContainer = (FrameLayout) browserFrameLayout.findViewById(R.id.fullscreen_custom_content);

        layout.addView(browserFrameLayout, COVER_SCREEN_PARAMS);

        // Configure the webview
        WebSettings webSettings = getSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptEnabled(true);
        webChromeClient = new MyWebChromeClient();
        setWebChromeClient(webChromeClient);

        setWebViewClient(new WebViewClient());

        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webSettings.setDomStorageEnabled(true);

        contentView.addView(this);
    }

    public FrameLayout getLayout() {
        return layout;
    }

    public boolean inCustomView() {
        return (customView != null);
    }

    public void hideCustomView() {
        webChromeClient.onHideCustomView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((customView == null) && canGoBack()){
                goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private class MyWebChromeClient extends WebChromeClient {
        private View videoProgressView;

        @Override
        public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
            HTML5WebView.this.setVisibility(View.GONE);

            // if a view already exists then immediately terminate the new one
            if (customView != null) {
                callback.onCustomViewHidden();
                return;
            }

            customViewContainer.addView(view);
            customView = view;
            customViewCallback = callback;
            customViewContainer.setVisibility(View.VISIBLE);
        }

        @Override
        public void onHideCustomView() {
            if (customView == null)
                return;

            // Hide the custom view.
            customView.setVisibility(View.GONE);

            // Remove the custom view from its container.
            customViewContainer.removeView(customView);
            customView = null;
            customViewContainer.setVisibility(View.GONE);
            customViewCallback.onCustomViewHidden();

            HTML5WebView.this.setVisibility(View.VISIBLE);
            HTML5WebView.this.goBack();
        }


        @Override
        public View getVideoLoadingProgressView() {
            if (videoProgressView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                videoProgressView = inflater.inflate(R.layout.video_loading_progress, null);
            }
            return videoProgressView;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            ((Activity) context).setTitle(title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            ((Activity) context).getWindow().setFeatureInt(Window.FEATURE_PROGRESS, newProgress*100);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }
    }

    static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS =
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
}
