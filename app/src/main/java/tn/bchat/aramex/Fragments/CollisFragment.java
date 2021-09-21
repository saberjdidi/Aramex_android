package tn.bchat.aramex.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import tn.bchat.aramex.Activities.LoginActivity;
import tn.bchat.aramex.Adapters.AdapterCollis;
import tn.bchat.aramex.Models.PackageCollis;
import tn.bchat.aramex.R;
import tn.bchat.aramex.Utils.URLs;
import tn.bchat.aramex.Utils.VolleySingleton;

public class CollisFragment extends Fragment {

    EditText searchEt;
    CircularProgressButton searchBtn;
    Button suiviBtn;
    TextView emptyTv;

    RecyclerView recyclerView;
    AdapterCollis adapterCollis;
    List<PackageCollis> packageCollisList;
    PackageCollis packageCollis;

    AwesomeValidation awesomeValidation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collis, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        searchEt = view.findViewById(R.id.searchEt);
        //searchBtn = view.findViewById(R.id.searchBtn);
        suiviBtn = view.findViewById(R.id.suiviBtn);
        emptyTv = view.findViewById(R.id.emptyTv);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //LinearLayoutManager manager = new LinearLayoutManager(getActivity().getApplicationContext(), RecyclerView.HORIZONTAL, false);

        //initialize validation style
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        //readPackageCollis();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //add validation for search edit text
        awesomeValidation.addValidation(getActivity(), R.id.searchEt,
                RegexTemplate.NOT_EMPTY, R.string.invalid_search);
        //awsome for mobile number
        // awesomeValidation.addValidation(getActivity(), R.id.searchEt,
        //       "[5-9]{1}[0-9]", R.string.invalid_search);

        suiviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check validation
                if(awesomeValidation.validate()){
                    Toast.makeText(getActivity(), "Validation successfully", Toast.LENGTH_SHORT).show();
                    readPackageCollis();
                }
                else {
                    Toast.makeText(getActivity(), "Validation failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchPackageCollis() {

        final String search = searchEt.getText().toString();

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("search",search);

        }catch (JSONException e){
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_COLLIS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //converting response to json object
                        try {
                            JSONObject obj = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                error.printStackTrace();

            }
        })
                //6: metter jsonObject dans method et ajouter methode pour retourne application/json
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonObject.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        //add requestQueue
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void readPackageCollis(){
        packageCollisList = new ArrayList<>();

        //static list
        packageCollisList.add(new PackageCollis("firstname1", "lastname1", "email1", "phone1", "address1"));
        packageCollisList.add(new PackageCollis("firstname2", "lastname2", "email2", "phone2", "address2"));
        packageCollisList.add(new PackageCollis("firstname3", "lastname3", "email3", "phone3", "address3"));
        adapterCollis = new AdapterCollis(getActivity(), packageCollisList);
        recyclerView.setAdapter(adapterCollis);

        //dynamic list
        final String search = searchEt.getText().toString();
        final JSONObject json = new JSONObject();
        try {
            json.put("search",search);
        }catch (JSONException e){
            e.printStackTrace();
        }

       JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLs.URL_COLLIS, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
              JSONObject jsonObject = null;
              for (int i = 0; i < response.length(); i++ ){
                  try {
                    jsonObject = response.getJSONObject(i);
                      packageCollis = new PackageCollis();
                      packageCollis.setFirstName(jsonObject.getString("firstName"));
                      packageCollis.setLastName(jsonObject.getString("lastName"));
                      packageCollis.setEmail(jsonObject.getString("email"));
                      packageCollis.setAddress(jsonObject.getString("address"));
                      packageCollis.setPhone(jsonObject.getString("phone"));

                      packageCollisList.add(packageCollis);
                  }
                  catch (JSONException e){
                      e.printStackTrace();
                      Toast.makeText(getActivity(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                  }
              }
                adapterCollis = new AdapterCollis(getActivity(), packageCollisList);
                recyclerView.setAdapter(adapterCollis);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        })
       {
           @Override
           public Map<String, String> getHeaders() throws AuthFailureError {
               Map<String, String> params = new HashMap<String, String>();
               params.put("Content-Type", "application/json");
               params.put("Accept", "application/json");
               return params;
           }
           @Override
           public byte[] getBody() {
               return json.toString().getBytes();
           }
       };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);

        if(packageCollisList.size() == 0){
            recyclerView.setVisibility(View.GONE);
            emptyTv.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTv.setVisibility(View.GONE);
        }
    }
}