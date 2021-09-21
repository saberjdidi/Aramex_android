package tn.bchat.aramex.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import tn.bchat.aramex.R;

public class TrackMapFragment extends Fragment {

      EditText etSource, etDestination;
      Button btTrack;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_track_map, container, false);

        etSource = view.findViewById(R.id.et_source);
        etDestination = view.findViewById(R.id.et_destination);
        btTrack = view.findViewById(R.id.bt_track);

        btTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get value from edittext
                String sSource = etSource.getText().toString().trim();
                String sDestination = etDestination.getText().toString().trim();

                //check condition
                if(sSource.equals("") && sDestination.equals("")){
                    Toast.makeText(getContext(), "Enter both location", Toast.LENGTH_SHORT).show();
                }
                else  {
                    DisplayTrack(sSource, sDestination);
                }
            }
        });

        return view;
    }

    private void DisplayTrack(String sSource, String sDestination) {
        //if device does not maps installed, redirect to playstore
        try {
            //when google maps is installed
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + sSource + "/" + sDestination);
            //initialize intent with action view
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //set package
            intent.setPackage("com.google.android.apps.maps");
            //set flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e){
            //when google maps is not installed
            //initialize uri
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            //initialize intent with action view
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //set flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}