package com.smartpoll;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText edit_text;
    String key="";
    String data2="";
    Button b;
    private static final String REGISTRATION_URL = "https://smartpolling.herokuapp.com/app/app_key.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b=(Button) findViewById(R.id.poll_btn);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();
        b.setEnabled(true);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void checkKey(View v){                               // when button is pressed
        edit_text = (EditText) findViewById(R.id.keytext);      // getting text ie. id from edittext
        key = edit_text.getText().toString();
//        Log.d("key",key);
        if(key.equals(""))
        {
//            Log.d("empty","empty");
            Toast.makeText(getApplicationContext(),"Please Enter Key",Toast.LENGTH_LONG).show();
        }
        else {
//            Log.d("full","full");
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref",MODE_PRIVATE);
            if(pref.contains(key))
            {
                Toast.makeText(getApplicationContext(),"You have already voted",Toast.LENGTH_LONG).show();
            }
            else {
                b.setEnabled(false);
                getkeyresult(key);
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void getkeyresult(String key_val){
        final String URL_SUFFIX="?key="+key_val;
        final String URL_SUFFIX2="?request="+key_val;
        class key_check extends AsyncTask<String,Void,String>
        {

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                Toast.makeText(getApplicationContext(),"Please wait",Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(String... params)
            {
                BufferedReader buffer=null;
                StringBuffer result;
                String line = "";
                try
                {
                    URL url = new URL(REGISTRATION_URL+URL_SUFFIX);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
//                    Log.d("p1","http");
                    con.connect();
//                    Log.d("p2","connection");
                    buffer = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    result =  new StringBuffer();
//                    Log.d("p3","result");

                    while((line = buffer.readLine())!= null) {
                        result.append(line);
                    }

                    URL url2 = new URL(REGISTRATION_URL+URL_SUFFIX2);//  REGISTRATION_URL
                    HttpURLConnection httpURLConnection2 = (HttpURLConnection) url2.openConnection();
                    InputStream inputStream = httpURLConnection2.getInputStream();
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(inputStream));
                    String line2 = "";
                    while(line2 != null)
                    {
                        line2 = bufferedReader2.readLine();
                        data2 += line2;
                    }
//                    Log.d("r1",data2);

                    return result.toString();
                }
                catch (Exception e)
                {
                    return e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
//                Log.d("p4",s.toString());
                if(s.toString().equals("0")){

                    Toast.makeText(getApplicationContext(),"Give your voting",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this,PollingActivity.class);
                    i.putExtra("data",data2);
                    Log.d("Tet", data2);
                    i.putExtra("key",key);
                    startActivity(i);
                }
                else if(s.toString().equals("1")){
                    Toast.makeText(getApplicationContext(),"Polling is closed.",Toast.LENGTH_LONG).show();
                    b.setEnabled(true);
                }
                else if(s.toString().equals("2")){
                    Toast.makeText(getApplicationContext(),"Incorrect Code.",Toast.LENGTH_LONG).show();
                    b.setEnabled(true);
                }
                else if(s.toString().equals("3")){
                    Toast.makeText(getApplicationContext(),"You have already voted",Toast.LENGTH_LONG).show();
                    b.setEnabled(true);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Check Network.",Toast.LENGTH_LONG).show();
                    b.setEnabled(true);
                }

            }
        }
        key_check k = new key_check();
        k.execute(URL_SUFFIX);
    }
}
