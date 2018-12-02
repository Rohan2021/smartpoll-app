package com.smartpoll;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PollingActivity extends AppCompatActivity {
    String key="";
    String option="";
    Button b;
    RadioGroup rg;
    private static final String REGISTRATION_URL = "https://smartpolling.herokuapp.com/app/app_key.php";
    JSONArray JA;
    String data="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling);

        rg = (RadioGroup) findViewById(R.id.rgroup);
        rg.setPadding(50,0,0,0);

        b= (Button) findViewById(R.id.bt_sub);
        key = getIntent().getStringExtra("data");
        option  = getIntent().getStringExtra("key");

        try {
            JA = new JSONArray(key);

            Log.d("test", String.valueOf(JA));

            for(int i =0 ;i <JA.length(); i++)
            {
                JSONObject jo = (JSONObject) JA.get(i);
                RadioButton rb = new RadioButton(this);

                if (i == 0)
                {
                    TextView tv = (TextView) findViewById(R.id.quest);
                    tv.setText("" + jo.get("question"));
                }
                else
                {
                    rb.setText("" + jo.get("options"));
                    String id=""+jo.get("oID");
                    rb.setId(Integer.parseInt(id));
                    rb.setTextColor(getResources().getColor(R.color.white));
                    rb.setTextSize(25);
                    rb.setBackgroundResource(R.drawable.rounded_rectangle_shape);
                    rb.setPadding(50,0,50,0);
                    rg.addView(rb);

                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void render(){

    }

    public void send_result(View v) {
        int id = rg.getCheckedRadioButtonId();
        if(id == -1)
        {
            Toast.makeText(getApplicationContext(),"Please select an option.",Toast.LENGTH_SHORT).show();
        }
        else {
            b.setEnabled(false);
            send_result2(id);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void send_result2(final int id) {
        final String URL_SUFFIX = "?set_result=" + id;
//         Log.d("p10",URL_SUFFIX);
        class key_check extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(String... params) {
                BufferedReader buffer = null;
                StringBuffer result;
                String line = "";
                try {

                    URL url = new URL(REGISTRATION_URL + URL_SUFFIX);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.connect();
                    buffer = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    result = new StringBuffer();

                    while ((line = buffer.readLine()) != null) {
                        result.append(line);
                    }
                    return result.toString();
                } catch (Exception e) {
                    return e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                 Log.d("test1","inPost");
//                 Log.d("test2",s.toString());
                if (s.toString().equals("true")) {
                    Intent in = new Intent(PollingActivity.this, ResultActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref",MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(String.valueOf(option), true);
                    editor.commit();
                    finish();
                    startActivity(in);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Connection Error ", Toast.LENGTH_SHORT).show();
                    b.setEnabled(true);
                }

            }
        }

        key_check k = new key_check();
        k.execute(URL_SUFFIX);
    }
}

