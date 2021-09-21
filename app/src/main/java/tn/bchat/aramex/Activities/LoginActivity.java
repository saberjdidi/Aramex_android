package tn.bchat.aramex.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import tn.bchat.aramex.Models.User;
import tn.bchat.aramex.R;
import tn.bchat.aramex.Utils.ConnectionReceiver;
import tn.bchat.aramex.Utils.SharedPrefManager;
import tn.bchat.aramex.Utils.URLs;
import tn.bchat.aramex.Utils.VolleySingleton;

public class LoginActivity extends AppCompatActivity {

    //check internet connection
    BroadcastReceiver broadcastReceiver;
    //views
    EditText mEmailEt, mPasswordEt;
    CircularProgressButton mLoginBtn, sendEmail;
    ProgressBar loading;
    FloatingActionButton fab;
    //progressdialog display while registering user
    ProgressDialog progressDialog;
    TextView forgetPassword;
    Dialog dialogPassword;

    private static final String ACCESS_TOKEN = null;
    private String localeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);

        if (!isConnected(this)) {
            showInternetDialog();
        }

      /*  if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, DashboardActivity.class));
        } */

        InitComponent();

        //internet connection
        //broadcastReceiver = new ConnectionReceiver();
        //registerNetworkBroadcast();

    }

    private void InitComponent()
    {
        try {
            //init
            mEmailEt = findViewById(R.id.emailEt);
            mPasswordEt = findViewById(R.id.passwordEt);
            mLoginBtn = findViewById(R.id.loginBtn);
            forgetPassword = findViewById(R.id.forgetPassword);
            loading = findViewById(R.id.loading);
            progressDialog = new ProgressDialog(this);

            fab = findViewById(R.id.fab);
            setFlag();
            Locale locale = getResources().getConfiguration().locale;
            localeString = locale.toString().substring(0, 2);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (localeString.equals("en")) {
                        setLocale("fr");
                    }
                    else {
                        setLocale("en");
                    }

                }
            });

            //login button click
            mLoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isConnected(LoginActivity.this)) {
                        showInternetDialog();
                    }
                    //input email, password
                    String email = mEmailEt.getText().toString().trim();
                    String password = mPasswordEt.getText().toString().trim();
                    //validate
                    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        // set error and focus to email edittext
                        mEmailEt.setError("Invalid Email");
                        mEmailEt.setFocusable(true);
                    }
                    else if (email.isEmpty() || password.isEmpty()){
                        showMessage("Please verify your fields");
                    }
                    else {
                        loginUser(email, password);
                    }
                }
            });

            //forget password
            dialogPassword = new Dialog(this);
            forgetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialogForgetPassword();
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(LoginActivity.this, "error\n"+e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void loginUser(String email, String password) {
        //progressDialog.setMessage("Logging in...");
        //progressDialog.show();
        loading.setVisibility(View.VISIBLE);
        mLoginBtn.setVisibility(View.GONE);
        updateUI();

        final JSONObject json = new JSONObject();
        try {
            json.put("email",email);
            json.put("password",password);
        }catch (JSONException e){
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Toast.makeText(getApplicationContext(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if ((obj.getString("code")).equals(200)) {
                                Toast.makeText(getApplicationContext(), obj.getString("user"), Toast.LENGTH_SHORT).show();
                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("user");
                                //getting the token from response
                                String token = obj.getString("jwt");
                                Log.d("jwt", token);

                                //creating a new user object
                                User user = new User(
                                        userJson.getString("id"),
                                        userJson.getString("email"),
                                        userJson.getString("username"),
                                        userJson.getString("first_name"),
                                        userJson.getString("last_name"),
                                        userJson.getString("phone"),
                                        userJson.getString("id_card"),
                                        userJson.getString("city_id"),
                                        userJson.getString("created_at"),
                                        userJson.getBoolean("active")
                                );

                                //storing the user in shared preferences
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                //storing token in shared preferences
                                SharedPrefManager.getInstance(getApplicationContext()).storeToken(token);

                                //starting the dashboard activity
                                runOnUiThread(LoginActivity.this::updateUI);
                            } else {
                                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                                mLoginBtn.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Login failed "+e.toString(), Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            mLoginBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Login error "+error.getMessage(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                loading.setVisibility(View.GONE);
                mLoginBtn.setVisibility(View.VISIBLE);
            }
        })
        {
         //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");  //use for object json
                params.put("Accept", "application/json");  //use for object json
                //params.put("Content-Type","application/x-www-form-urlencoded"); //use for params
                //params.put("token", ACCESS_TOKEN); //pass token in header
                return params;
            }
            // mettre jsonObject dans la methode et ajouter methode pour retourne application/json
            @Override
            public byte[] getBody() throws AuthFailureError {
                return json.toString().getBytes();
            }
            @Override
            public String getBodyContentType() {
                return "application/json";
                //return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        //Pass Your Parameters here
        /* @Override
         protected Map<String, String> getParams() throws AuthFailureError {
             Map<String, String> params = new HashMap<>();
             params.put("email", email);
             params.put("password", password);
             return params;
         } */

         //status code
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int statusCode=response.statusCode;
                switch (statusCode) {
                    case HttpURLConnection.HTTP_OK:
                        Toast.makeText(LoginActivity.this, "Successfully ", Toast.LENGTH_SHORT).show();
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST :
                        Toast.makeText(LoginActivity.this, "Bad Request ", Toast.LENGTH_SHORT).show();
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        Toast.makeText(LoginActivity.this, "Service not found ", Toast.LENGTH_SHORT).show();
                        break;
                    case HttpURLConnection.HTTP_INTERNAL_ERROR:
                        Toast.makeText(LoginActivity.this, "Error in server ", Toast.LENGTH_SHORT).show();
                        break;
                    case HttpURLConnection.HTTP_UNAUTHORIZED :
                        Toast.makeText(LoginActivity.this, "Unauthorized ", Toast.LENGTH_SHORT).show();
                        break;
                    case HttpURLConnection.HTTP_FORBIDDEN :
                        Toast.makeText(LoginActivity.this, "Forbidden ", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(LoginActivity.this, "Unknown error ", Toast.LENGTH_SHORT).show();
                        break;
                }
                return super.parseNetworkResponse(response);
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

    private void updateUI() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginClick(View View){
        startActivity(new Intent(this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);

    }

    private void openDialogForgetPassword(){
        dialogPassword.setContentView(R.layout.forgot_password_popup);
        dialogPassword.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        sendEmail = dialogPassword.findViewById(R.id.sendEmail);
        sendEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogPassword.dismiss();
                }
            });
        dialogPassword.show();
    }

    //connection internet
    private void showInternetDialog() {

        Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        View view = LayoutInflater.from(this).inflate(R.layout.no_internet_dialog, findViewById(R.id.no_internet_layout));
        view.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected(LoginActivity.this)) {
                    showInternetDialog();
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        });

        dialog.setContentView(view);
        dialog.show();

    }

    private boolean isConnected(LoginActivity loginActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) loginActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }

    //multi langage
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        RestartActivity();
    }

    private void setFlag() {
        Locale locale = getResources().getConfiguration().locale;
        String lang = locale.toString().substring(0, 2);
        if (lang.equals("en")) {
            lang = "fr";
        } else if (lang.equals("fr")) {
            lang = "en";
        }
        String iconId = "ic_flag_" + lang;
        fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), getResources().getIdentifier(iconId, "drawable", getPackageName())));

    }
    private void RestartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    //check internet connection
  /*  protected void registerNetworkBroadcast(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }
    protected void unregisterNetwork(){
        try {
          unregisterReceiver(broadcastReceiver);
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetwork();
    } */
}