package com.app.wallafy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by hitasoft on 24/5/16.
 **/

public class WelcomeActivity extends Activity implements View.OnClickListener {

    TextView login,signup,skip;
    public static boolean fromSignout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcomelay);
        login= (TextView)findViewById(R.id.login);
        signup = (TextView)findViewById(R.id.signup);
        skip = (TextView)findViewById(R.id.skip);

        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        skip.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // For Internet checking disconnect
        wallafyApplication.unregisterReceiver(WelcomeActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // For Internet checking
        wallafyApplication.registerReceiver(WelcomeActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fromSignout){
            fromSignout = false;
            finish();
            Intent y = new Intent(WelcomeActivity.this, FragmentMainActivity.class);
            startActivity(y);
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                break;
            case R.id.signup:
                Intent e = new Intent(WelcomeActivity.this, RegisterActivity.class);
                e.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(e);
                break;
            case R.id.skip:
                if (fromSignout){
                    fromSignout = false;
                    finish();
                    Intent y = new Intent(WelcomeActivity.this, FragmentMainActivity.class);
                    startActivity(y);
                } else {
                    finish();
                }
                break;
        }

    }
}
