package com.lonelyship.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GooglePlusLoginFragment.GOOGLE_PLUS_SIGN_IN) {
            Log.w(TAG, "[LoginActivity]onActivityResult For Google");
            getSupportFragmentManager().findFragmentById(R.id.google_plus_login_fragment).onActivityResult(requestCode, resultCode, data);
        } else {
            Log.w(TAG, "[LoginActivity]onActivityResult For FB");
            getSupportFragmentManager().findFragmentById(R.id.facebook_login_fragment).onActivityResult(requestCode, resultCode, data);
        }
    }
}
