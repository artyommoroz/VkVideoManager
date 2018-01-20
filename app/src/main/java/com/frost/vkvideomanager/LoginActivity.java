package com.frost.vkvideomanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.loginButton)
    Button loginButton;
    @BindView(R.id.videocamView)
    ImageView videocamView;

    private static final String[] myScope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.videocam_scale);
//        videocamView.startAnimation(scaleAnimation);

//        loginButton.setOnClickListener(v -> VKSdk.login(LoginActivity.this, "friends,video,wall,groups," + VKScope.NOHTTPS));
        loginButton.startAnimation(scaleAnimation);
        loginButton.setOnClickListener(v -> VKSdk.login(LoginActivity.this, myScope));

        if (VKSdk.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    static {
        myScope = new String[]{VKScope.VIDEO, VKScope.GROUPS, VKScope.FRIENDS,
                VKScope.WALL, VKScope.OFFLINE, VKScope.NOHTTPS, VKScope.DIRECT};
//        myScope = new String[]{VKScope.VIDEO, VKScope.GROUPS, VKScope.FRIENDS,
//                VKScope.WALL, VKScope.OFFLINE};
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override                   
            public void onError(VKError error) {
                Toast.makeText(LoginActivity.this, getString(R.string.login_error_toast), Toast.LENGTH_SHORT).show();
            }
        }))
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
