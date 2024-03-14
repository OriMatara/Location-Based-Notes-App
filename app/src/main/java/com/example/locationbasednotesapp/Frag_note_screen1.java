package com.example.locationbasednotesapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_note_screen1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_note_screen1 extends Fragment implements LocationListener {

    private double latitude;
    private double longitude;

    EditText titleET;
    EditText bodyET;
    TextView dateET;
    String title = null;
    String date;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Frag_note_screen1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_note_screen1.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_note_screen1 newInstance(String param1, String param2) {
        Frag_note_screen1 fragment = new Frag_note_screen1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // Initialize location services
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        // Check if location permissions are granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, this);
        } else {
            // Handle the case where location permissions are not granted
            Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        // Update latitude and longitude when the location changes
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.d("latitude", "latitude: " + latitude);
        Log.d("longitude", "longitude: " + longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Handle status changes if needed
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Handle provider enabled if needed
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Handle provider disabled if needed
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_note_screen1, container, false);

        title = null;
        date = getCurrentFormattedDate();
        dateET = view.findViewById(R.id.etDate);
        dateET.setText(date);

        // Retrieve note details from arguments bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString("title");
            String body = bundle.getString("body");
            date = bundle.getString("date");
            latitude = bundle.getDouble("latitude");
            longitude = bundle.getDouble("longitude");

            // Now you have the note details, use them as needed
            titleET = view.findViewById(R.id.etTitle);
            bodyET = view.findViewById(R.id.etBody);
            dateET = view.findViewById(R.id.etDate);

            titleET.setText(title);
            bodyET.setText(body);
            dateET.setText(date);
        }


        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnDelete = view.findViewById(R.id.btnDelete);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.example.locationbasednotesapp.MainActivity mainActivity=(com.example.locationbasednotesapp.MainActivity) getActivity();
                mainActivity.writeNoteToUser(latitude, longitude, title);
                Navigation.findNavController(getView()).navigate((R.id.action_frag_note_screen1_to_frag_main_notes));
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.example.locationbasednotesapp.MainActivity mainActivity=(com.example.locationbasednotesapp.MainActivity) getActivity();
                mainActivity.deleteNote(title);
                Navigation.findNavController(getView()).navigate((R.id.action_frag_note_screen1_to_frag_main_notes));
            }
        });

        return view;
    }

    private String getCurrentFormattedDate() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(currentDate);
    }

}