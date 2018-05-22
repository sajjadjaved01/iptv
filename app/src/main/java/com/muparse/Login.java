package com.muparse;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.text.TextUtils.isEmpty;
import static com.muparse.R.id.password;

public class Login extends AppCompatActivity {

    static final File DEFA = Environment.getExternalStorageDirectory();
    public static final File dir = new File(DEFA.getPath() + "/Netuptv");
    static final File filepath = new File(dir.getPath() + "/data.m3u");
    private static Login instance = null;
    public final String urlLink = " http://portal.simiptv.com:8001/get.php?username=LrWQISrOq0&password=7i5Qdtmunn&type=m3u&output=ts";
    public final String domain = " http://portal.simiptv.com:8001";
    FirebaseAnalytics firebaseAnalytics;
    SharedPreferences.Editor editor;
    private EditText mEmailView;
    private EditText mPasswordView;
    private ProgressBar spinner;

    public static Login getInstance() {
        if (instance == null) {
            instance = new Login();
        }
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setBackgroundDrawable(null);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(password);
        spinner = findViewById(R.id.login_progress);
        final Button mEmailSignIn = findViewById(R.id.email_sign_in_button);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                checkNet();
                return true;
            }
        });

        mEmailSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNet();
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "LoginActivity");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT,bundle);
    }

    public void checkNet() {
        if (Utils.getInstance().isNetworkAvailable(Login.this)) {
            attemptLogin();
        } else {
            attemptLogin();
        }
    }

    @SuppressWarnings("All")
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                } else {
                    Utils.getInstance().Snack(this, "Permission denied", findViewById(R.id.activity_login));
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
                return;
            }
            // other 'case' lines to check for other permissions this app might request
        }
    }

    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            new _checkNetworkAvailable().execute(domain + "/get.php?username=" + mEmailView.getText() + "&password=" + mPasswordView.getText() + "&type=m3u&output=ts");
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    protected void onResume() {
        super.onResume();
        boolean isAccess = PreferencesManager.getBoolean(this, "isLogged", false);
        boolean isAva = Utils.getInstance().isNetworkAvailable(Login.this);
        if (isAccess) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        //if (!isAva){activateWIFI();}
    }

    private class _checkNetworkAvailable extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL myUrl = new URL(params[0]); //Arrays.toString(params)
                HttpURLConnection con = (HttpURLConnection) myUrl.openConnection();
                con.setInstanceFollowRedirects(true);
                con.setConnectTimeout(2000);
                con.setReadTimeout(2000);
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setDoInput(true);
                con.connect();
                boolean check = con.getResponseCode() == HttpURLConnection.HTTP_OK;
                Log.e("Google", String.valueOf(check));
                return check;
            } catch (Exception e) {
                Log.e("Google", e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            boolean bRes = result;
            if (bRes) {
                editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                editor.putString("name", "admin");
                editor.putString("id", mEmailView.getText().toString());
                editor.putBoolean("isLogged", true);
                editor.apply();
                new DwnloadFileFromUrl().execute(urlLink);
                Utils.getInstance().Snack(Login.this, "Loading channels...", findViewById(R.id.activity_login));
            } else {
                spinner.setVisibility(View.GONE);
                Utils.getInstance().Snack(getApplicationContext(), "Account not found.", findViewById(R.id.activity_login));
            }
        }
    }

    private class DwnloadFileFromUrl extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... f_url) {
            try {
                URL yahoo = new URL(f_url[0]);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(yahoo.openStream()));
                String inputLine;
                OutputStreamWriter myOutWriter = new FileWriter(dir.getPath()+"/"+"data.m3u");
                while ((inputLine = in.readLine()) != null) {
                    myOutWriter.write(inputLine + "\n");
                }
                myOutWriter.flush();
                myOutWriter.close();
                in.close();
                Log.e("Google", "File done");
            } catch (Exception e) {
                Log.d("Google", "DownloadFileFromUrl " + e.getMessage());
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
        }

        protected void onPostExecute(String file_url) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            spinner.setVisibility(View.GONE);
            startActivity(intent);
            finish();
        }
    }
}
