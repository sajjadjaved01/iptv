package com.muparse;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    static final File dir = new File(DEFA.getPath() + "/Netuptv");
    static final File filepath = new File(dir.getPath() + "/iptv_data.m3u");
    FirebaseAnalytics firebaseAnalytics;
    ConnectivityManager cm;
    NetworkInfo activeNetwork;
    SharedPreferences.Editor editor;
    Intent intent;
    private EditText mEmailView;
    private EditText mPasswordView;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setBackgroundDrawable(null);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(password);
        spinner = (ProgressBar) findViewById(R.id.login_progress);
        final Button mEmailSignIn = (Button) findViewById(R.id.email_sign_in_button);
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
    }

    public void checkNet() {
        if (activeNetwork != null && activeNetwork.isConnected()) {
            attemptLogin();
            // connected to WiFI / Network
//            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
//               attemptLogin();
//            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
//               attemptLogin();
//            }
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
                    Toast.makeText(this, "Permission denied ☻", Toast.LENGTH_SHORT).show();
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
        } ///sdcard/Netuptv/iptv_data.m3u
//        http://portal.onlineiptv.net:5210/get.php?username=fNOaPbcqCB&password=yttnwNGpCR&type=m3u&output=ts
        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to perform the user login attempt.
            File fCheck = new File(filepath.getPath());
            if (fCheck.canRead()) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Toast.makeText(this, "FileFound", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            } else {
            }
            //String state = Environment.getExternalStorageState();
            // if (state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
//            new _Pingpong().execute("https://www.google.com.pk");
            new _checkNetworkAvailable().execute("http://portal.onlineiptv.net:5210/get.php?username=" + mEmailView.getText() + "&password=" + mPasswordView.getText() + "&type=m3u&output=ts");
//            }
//            }else {
//                Toast.makeText(this, "Unable to  get Storage", Toast.LENGTH_SHORT).show();
//            }
//            }{{
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    void activateWIFI() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setTitle("Wi-Fi not available");
        localBuilder.setMessage("It is recommended to enable  Wi-Fi network.");
        localBuilder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        localBuilder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
            }
        });
        localBuilder.setCancelable(false);
        localBuilder.create().show();
    }

    protected void onResume() {
        super.onResume();
        boolean isAccess = isLoggedIn();
        if (isAccess) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return prefs.getBoolean("isLogged", false);
    }

    private boolean isNetworkAvailable() {
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        } else {
            activateWIFI();
            return false;
        }
        //return (activeNetwork != null) && (activeNetwork.isConnected());
    }

    void gfgf(String ff) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(" ").append("");
            builder.setSpan(new ImageSpan(Login.this, R.drawable.ic_info_black_24dp), builder.length() - 1, builder.length(), 0);
            builder.append(" ").append(ff);
            Snackbar.make(findViewById(R.id.activity_login), builder, Snackbar.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, ff, Toast.LENGTH_SHORT).show();
        }
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
                con.setConnectTimeout(3000);
                con.setReadTimeout(3000);
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setDoInput(true);
                con.connect();
                return con.getResponseCode() == HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                Log.i("Google", e.toString());
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
                new DwnloadFileFromUrl().execute("http://portal.onlineiptv.net:5210/get.php?username=fNOaPbcqCB&password=yttnwNGpCR&type=m3u&output=ts");//"http://portal.onlineiptv.net:5210/get.php?username=" + mEmailView.getText() + "&password=" + mPasswordView.getText() + "&type=m3u&output=ts");
            } else {
                spinner.setVisibility(View.GONE);
                gfgf("Account not found. Contact admin ☺");
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

                OutputStreamWriter myOutWriter = new FileWriter(dir.getPath() + "/iptv_data.m3u");
                while ((inputLine = in.readLine()) != null) {
                    myOutWriter.write(inputLine + "\n");
                }
                myOutWriter.flush();
                myOutWriter.close();
                in.close();
                Log.e("Google", "File done");
            } catch (Exception e) {
                Log.d("Google", "Error:g " + e.getMessage());
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
        }

        protected void onPostExecute(String file_url) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            spinner.setVisibility(View.GONE);
            startActivity(intent);
        }
    }
}
