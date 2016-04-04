package com.example.frost.vkvideomanager.player;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


public class UrlHelper {

    private static final String PACKAGE_YOUTUBE = "com.google.android.youtube";

    public static void playVideo(Context context, String videoUri) {
        if (isAppInstalled(context, PACKAGE_YOUTUBE) && videoUri.contains("youtube")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUri));
            intent.setPackage(PACKAGE_YOUTUBE);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("videoUri", videoUri);
            context.startActivity(intent);
        }
    }

    private static boolean isAppInstalled(Context context, String packageName) {
        Intent mIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        return mIntent != null;
    }
}
