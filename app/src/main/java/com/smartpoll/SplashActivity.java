package com.smartpoll;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class SplashActivity extends AppCompatActivity {

    Handler myHandler;
    LinearLayout refresh;
    LinearLayout networkcheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        myHandler = new Handler();
        refresh = (LinearLayout) findViewById(R.id.refresh);
        networkcheck = (LinearLayout) findViewById(R.id.networkcheck);
        checkNetwork();
    }
    public void openMainActivity() {
        Intent in=new Intent(this,MainActivity.class);
        finish();
        startActivity(in);
    }
    public void checkNetwork(){
        refresh.setVisibility(View.GONE);
        networkcheck.setVisibility(View.VISIBLE);
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //Internet Connection
                    openMainActivity();
                } else {
                    //No Internet Connection
                    refresh.setVisibility(View.VISIBLE);
                    networkcheck.setVisibility(View.GONE);
                }
            }
        }, 5000);
    }
    public void refresh(View v){
        checkNetwork();
    }
}