package tn.bchat.aramex.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import tn.bchat.aramex.Models.Tarif;
import tn.bchat.aramex.R;
import tn.bchat.aramex.Utils.URLs;

public class TarifFragment extends Fragment {

    AutoCompleteTextView autoCompleteExpediteur, autoCompleteDestinateur;
    Spinner spinnerPoids;
    EditText unity;
    CircularProgressButton calculerBtn;
    CardView cardView2;
    TextView destinateurTv, expediteurTv, poidTv, unityTv;

    AwesomeValidation awesomeValidation;

    String[] countries = {"Tunis", "Beja", "Jendouba", "Kef", "Siliana", "Kasserine", "Gafsa",
            "Ariana", "Manouba", "Ben Arous", "Zaghouen", "Monastir", "Mahdia", "Nabeul", "Sousse",
            "Tozeur", "Gabes", "Kébili", "Tataouine", "Bizerte", "Kairaoun", "Sidi Bouzid", "Sfax", "Médenine"};
    String[] poidList = {"Kg", "Lb"};
    ArrayAdapter<String> adapterAutocomplete;
    ArrayAdapter<String> adapterPoid;
    List<String> countriesList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tarif, container, false);

        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //asign variable
        autoCompleteExpediteur = view.findViewById(R.id.auExpediteur);
        autoCompleteDestinateur = view.findViewById(R.id.auDestinateur);
        spinnerPoids = view.findViewById(R.id.spPoids);
        unity = view.findViewById(R.id.unityEt);
        calculerBtn = view.findViewById(R.id.calculerBtn);
        cardView2 = view.findViewById(R.id.cardView2);
        cardView2.setVisibility(View.GONE);

        destinateurTv = view.findViewById(R.id.destinateurTv);
        expediteurTv = view.findViewById(R.id.expediteurTv);
        poidTv = view.findViewById(R.id.poidTv);
        unityTv = view.findViewById(R.id.unityTv);

        //initialize validation style
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        AdapterAutocomplete();

        return view;
    }

    private void AdapterAutocomplete(){
        //list of countries
        countriesList = new ArrayList<>();
        countriesList.add("Tunis");
        countriesList.add("Beja");
        countriesList.add("Jendouba");
        countriesList.add("Kef");
        countriesList.add("Siliana");
        countriesList.add("Kasserine");
        countriesList.add("Gafsa");
        countriesList.add("Ariana");
        countriesList.add("Manouba");
        countriesList.add("Ben Arous");
        countriesList.add("Zaghouen");
        countriesList.add("Monastir");
        countriesList.add("Mahdia");
        countriesList.add("Nabeul");
        countriesList.add("Sousse");
        countriesList.add("Tozeur");
        countriesList.add("Gabes");
        countriesList.add("Kébili");
        countriesList.add("Tataouine");
        countriesList.add("Bizerte");
        countriesList.add("Kairaoun");
        countriesList.add("Sidi Bouzid");
        countriesList.add("Sfax");
        countriesList.add("Médenine");

       /* final ArrayList<String> nlist = new ArrayList<String>();
        String filterableString ;
        for (int i=0; i<countriesList.size(); i++){
            filterableString = countriesList.get(i);
            if(autoCompleteExpediteur.getText().toString().toLowerCase().equals(filterableString)){
                nlist.add(filterableString);
            }
            else {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }*/

        //initialize adapter
        adapterAutocomplete = new ArrayAdapter<>(getActivity()
                ,android.R.layout.simple_dropdown_item_1line, countriesList);
        adapterPoid = new ArrayAdapter<>(getActivity()
                ,android.R.layout.simple_dropdown_item_1line, poidList);

        //get suggestion after the number of word types
        autoCompleteExpediteur.setThreshold(1);
        autoCompleteDestinateur.setThreshold(1);
        //set adapter
        autoCompleteExpediteur.setAdapter(adapterAutocomplete);
        autoCompleteDestinateur.setAdapter(adapterAutocomplete);
        spinnerPoids.setAdapter(adapterPoid);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //add validation for search edit text
        awesomeValidation.addValidation(getActivity(), R.id.auExpediteur,
                RegexTemplate.NOT_EMPTY, R.string.invalid_expediteur);
        awesomeValidation.addValidation(getActivity(), R.id.auDestinateur,
                RegexTemplate.NOT_EMPTY, R.string.invalid_destinateur);
        awesomeValidation.addValidation(getActivity(), R.id.unityEt,
                RegexTemplate.NOT_EMPTY, R.string.invalid_unity);

        calculerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculTarif();
            }
        });
    }

    private void calculTarif(){
        String expediteur = autoCompleteExpediteur.getText().toString().trim();
        String destinateur = autoCompleteDestinateur.getText().toString().trim();
        String poid = spinnerPoids.getSelectedItem().toString().trim();
        String unite = unity.getText().toString().trim();

        if(!countriesList.contains(expediteur)){
            autoCompleteExpediteur.setError("Invalid Expediteur");
            autoCompleteExpediteur.setFocusable(true);
        }
        if(!countriesList.contains(destinateur)){
            autoCompleteDestinateur.setError("Invalid Destinateur");
            autoCompleteDestinateur.setFocusable(true);
        }
        //check validation
        if(awesomeValidation.validate() && countriesList.contains(expediteur) && countriesList.contains(destinateur)){

            cardView2.setVisibility(View.VISIBLE);
            expediteurTv.setText(expediteur);
            destinateurTv.setText(destinateur);
            poidTv.setText(poid);
            unityTv.setText(unite);

            sendObjectTarif(expediteur, destinateur, poid, unite);
        }
        else {
            Toast.makeText(getActivity(), "Validation failed", Toast.LENGTH_SHORT).show();
            cardView2.setVisibility(View.GONE);
        }
    }

    private void sendObjectTarif(String expediteur, String destinateur, String poid, String unite){
        final JSONObject json = new JSONObject();
        try {
            json.put("expediteur",expediteur);
            json.put("destinateur",destinateur);
            json.put("poid",poid);
            json.put("unite",unite);
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URLs.URL_COLLIS, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("success", response.toString());

                        try {
                            String exp = response.getString("expediteur");
                            String dest = response.getString("destinateur");
                            double distance = response.getDouble("poid");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", error.toString());
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
                };
    }

}