package com.datechnologies.androidtest.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.datechnologies.androidtest.MainActivity;
import com.datechnologies.androidtest.R;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * A screen that displays a login prompt, allowing the user to login to the D & A Technologies Web Server.
 *
 */
public class LoginActivity extends AppCompatActivity {

    private final int SUCCESS = 200;
    private String email;
    private String password;
    private final String URL_STRING = "http://dev.rapptrlabs.com/Tests/scripts/login.php";
    private String jsonTxt;
    private long startTime;
    long elapsedTime;
    Object lock = new Object();
    protected HttpURLConnection conn = null;
    private BufferedReader reader = null;
    private int responseCode;

    //==============================================================================================
    // Static Class Methods
    //==============================================================================================

    public static void start(Context context)
    {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);

    }

    //==============================================================================================
    // Lifecycle Methods
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // The only valid login credentials are:
        // email: info@rapptrlabs.com
        // password: Test123
        // so please use those to test the login.
    }

    public void onLogInClicked(View view) {

        EditText userObj = (EditText)findViewById(R.id.userField);
        email = userObj.getText().toString();
        EditText passwordObj = (EditText)findViewById(R.id.passField);
        password = passwordObj.getText().toString();

        sendParams();


    }

    //==============================================================================================
    // Send Credentials To Server
    //==============================================================================================

    private void sendParams() {
        String data = "email=" + email + "&password=" + password;

        Runnable runnable =
                () -> {

            try {
                byte[] postData = data.getBytes(StandardCharsets.UTF_8);
                int postDataLength = postData.length;
                URL url = new URL(URL_STRING);
                conn = (HttpURLConnection) url.openConnection();

                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                conn.setUseCaches(true);
                //Send Info to Server
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                startTime = System.currentTimeMillis();
                    wr.write(postData);
                    wr.flush();
                    wr.close();

                //Process Response
                responseCode = conn.getResponseCode();
                if(responseCode != SUCCESS){
                    InputStream error = conn.getErrorStream();
                    reader = new BufferedReader(new InputStreamReader(error));
                }else {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                }

                StringBuffer buffer = new StringBuffer();
                String line = "";


                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);
                }
                elapsedTime = System.currentTimeMillis() - startTime;

                jsonTxt = buffer.toString();

                messageReturned(conn);
            }catch (Exception e){
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    //==============================================================================================
    // Process Response From Server
    //==============================================================================================

    protected void messageReturned(HttpURLConnection conn) {
        try {

            if(jsonTxt != null) {
                JSONObject obj = new JSONObject(jsonTxt);
                String code = obj.getString("code");
                String message = obj.getString("message");

                LoginActivity.this.runOnUiThread(new Runnable() {
                    public void run() {

                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                alert.setTitle(code);
                alert.setMessage(message + "\n(" + elapsedTime + "ms)");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(responseCode == SUCCESS) {
                            finish();
                        }
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();
                    }
                });
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
