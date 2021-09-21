package tn.bchat.aramex.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import tn.bchat.aramex.Models.User;
import tn.bchat.aramex.R;
import tn.bchat.aramex.Utils.SharedPrefManager;
import tn.bchat.aramex.Utils.URLs;
import tn.bchat.aramex.Utils.VolleySingleton;

public class RegisterActivity extends AppCompatActivity {

    //views
    EditText mEmailEt, mPasswordEt, mFirstNameEt, mLastName, mPhoneEt, mUserNameEt, idCardEt, idCityEt;
    SwitchCompat activeSC;
    CircularProgressButton mRegisterBtn;
    ProgressBar loading;
    //progressdialog display while registering user
    ProgressDialog progressDialog;
    private int mStatusCode;
    private static boolean active = false;

    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        changeStatusBarColor();

        //if the user is already logged in we will directly start the profile activity
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, DashboardActivity.class));
            return;
        }

        //init
        mFirstNameEt = findViewById(R.id.firstNameEt);
        mLastName = findViewById(R.id.lastNameEt);
        mUserNameEt = findViewById(R.id.userNameEt);
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mPhoneEt = findViewById(R.id.phoneEt);
        idCardEt = findViewById(R.id.idCardEt);
        idCityEt = findViewById(R.id.idCityEt);
        activeSC = findViewById(R.id.activeSC);
        mRegisterBtn = findViewById(R.id.registerBtn);
        loading = findViewById(R.id.loading);

        progressDialog = new ProgressDialog(this);


        activeSC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked == true){
                    active = true;
                    Toast.makeText(RegisterActivity.this, "Active "+active, Toast.LENGTH_SHORT).show();
                }
                else {
                    active = false;
                    Toast.makeText(RegisterActivity.this, "Active "+active, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //initialize validation style
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        //add validation for search edit text
        awesomeValidation.addValidation(this, R.id.firstNameEt,
                RegexTemplate.NOT_EMPTY, R.string.invalid_name);
        awesomeValidation.addValidation(this, R.id.lastNameEt,
                RegexTemplate.NOT_EMPTY, R.string.invalid_lat_name);
        awesomeValidation.addValidation(this, R.id.userNameEt,
                RegexTemplate.NOT_EMPTY, R.string.invalid_user_name);
        awesomeValidation.addValidation(this, R.id.emailEt,
                Patterns.EMAIL_ADDRESS, R.string.invalid_email);
        awesomeValidation.addValidation(this, R.id.phoneEt,
                RegexTemplate.NOT_EMPTY, R.string.invalid_phone);
        awesomeValidation.addValidation(this, R.id.passwordEt,
                RegexTemplate.NOT_EMPTY, R.string.invalid_password);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register(){
        //check validation
        if(awesomeValidation.validate()){
            //Toast.makeText(getApplicationContext(), "Validation successfully", Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.VISIBLE);
            mRegisterBtn.setVisibility(View.GONE);

            final String firstName = mFirstNameEt.getText().toString().trim();
            final String lastName = mLastName.getText().toString().trim();
            final String userName = mUserNameEt.getText().toString().trim();
            final String email = mEmailEt.getText().toString().trim();
            final String phone = mPhoneEt.getText().toString().trim();
            final String password = mPasswordEt.getText().toString().trim();
            final String idCard = idCardEt.getText().toString().trim();
            final String city = idCityEt.getText().toString().trim();

            SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            String createdDate = formatter.format(date);

            final JSONObject json = new JSONObject();
            try {
                json.put("first_name",firstName);
                json.put("last_name",lastName);
                json.put("username",userName);
                json.put("email",email);
                json.put("password",password);
                json.put("phone",phone);
                json.put("id_card",idCard);
                json.put("city_id",city);
                json.put("active",active);
                json.put("created_at",createdDate);
            }catch (JSONException e){
                e.printStackTrace();
            }
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_REGISTER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                           try {
                               //converting response to json object
                               JSONObject obj = new JSONObject(response);

                               //if no error in response
                               //if (!obj.getBoolean("error")) {
                                 if(mStatusCode == 200) {
                                   Toast.makeText(getApplicationContext(), obj.getString("Success"), Toast.LENGTH_SHORT).show();

                                   //getting the user from the response
                                   JSONObject userJson = obj.getJSONObject("user");

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

                                   //starting the profile activity
                                   finish();
                                   startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                               }
                                 else if(mStatusCode == 400) {
                                   Toast.makeText(getApplicationContext(), obj.getString("Bad Request"), Toast.LENGTH_SHORT).show();
                                   loading.setVisibility(View.GONE);
                                   mRegisterBtn.setVisibility(View.VISIBLE);
                               }
                                 else if(mStatusCode == 401) {
                                     Toast.makeText(getApplicationContext(), obj.getString("Unauthorized"), Toast.LENGTH_SHORT).show();
                                     loading.setVisibility(View.GONE);
                                     mRegisterBtn.setVisibility(View.VISIBLE);
                                 }
                                 else if(mStatusCode == 404) {
                                     Toast.makeText(getApplicationContext(), obj.getString("Not Found"), Toast.LENGTH_SHORT).show();
                                     loading.setVisibility(View.GONE);
                                     mRegisterBtn.setVisibility(View.VISIBLE);
                                 }
                                 else {
                                     Toast.makeText(getApplicationContext(), obj.getString("nknown Error"), Toast.LENGTH_SHORT).show();
                                     loading.setVisibility(View.GONE);
                                     mRegisterBtn.setVisibility(View.VISIBLE);
                                 }
                           } catch (JSONException e) {
                               e.printStackTrace();
                               Toast.makeText(RegisterActivity.this, "Register failed "+e.toString(), Toast.LENGTH_SHORT).show();
                               loading.setVisibility(View.GONE);
                               mRegisterBtn.setVisibility(View.VISIBLE);
                           }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(RegisterActivity.this, "Register failed \n"+error.toString(), Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            mRegisterBtn.setVisibility(View.VISIBLE);
                        }
                    })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Accept", "application/json");
                    return params;
                }
                @Override
                public byte[] getBody() throws AuthFailureError {
                    return json.toString().getBytes();
                }
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    mStatusCode = response.statusCode;
                    return super.parseNetworkResponse(response);
                }
            };
            //RequestQueue requestQueue = Volley.newRequestQueue(this);
            //requestQueue.add(stringRequest);
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }
        else {
            loading.setVisibility(View.GONE);
            mRegisterBtn.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Validation failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void onLoginClick(View view){
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);

    }
}