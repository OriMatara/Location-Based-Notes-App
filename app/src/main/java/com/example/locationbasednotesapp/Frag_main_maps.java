package com.example.locationbasednotesapp;



import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_main_maps#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_main_maps extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private MapView mapView;

    public List<Note> dataSet2;

    double latitude;
    double longitude;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Frag_main_maps() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_main_maps.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_main_maps newInstance(String param1, String param2) {
        Frag_main_maps fragment = new Frag_main_maps();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_main_maps, container, false);


        if (getArguments() != null) {
            Parcelable[] parcelableArray = getArguments().getParcelableArray("dataSet2");

            if (parcelableArray != null) {
                Note[] noteArray = Arrays.copyOf(parcelableArray, parcelableArray.length, Note[].class);

                // Convert array to ArrayList
                dataSet2 = new ArrayList<>(Arrays.asList(noteArray));

                Log.d("dataSet2", "onCreateView: " + dataSet2.get(0).toString());
            }
        }

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        Button btnListMode2 = view.findViewById(R.id.btnListMode2);
        Button btnNewNote2 = view.findViewById(R.id.btnNewNote2);
        Button btnLogout2 = view.findViewById(R.id.btnLogout2);

        TextView welcomeUser2 = view.findViewById(R.id.welcomeUser2);

        welcomeUser2.setText("WELCOME "+ MainActivity.userName.toUpperCase() + "!");

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                for (Note note : dataSet2) {
                    latitude = note.getLatitude();
                    longitude = note.getLongitude();

                    Log.d("NoteLocationinveiw999", "Latitude: " + latitude + ", Longitude: " + longitude);
                    // Now you can add a marker to the map
                    LatLng location = new LatLng(latitude, longitude);
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(location).title(note.getTitle()));
                    // Set the note as the tag for the marker
                    marker.setTag(note);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                }
                // Set a marker click listener
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        // Retrieve the note associated with the clicked marker
                        Note clickedNote = (Note) marker.getTag();
                        if (clickedNote != null) {
                            // Navigate to Frag_note_screen1 and pass the note details
                            Bundle bundle = new Bundle();
                            bundle.putString("title", clickedNote.getTitle());
                            bundle.putString("body", clickedNote.getBody());
                            bundle.putString("date", clickedNote.getDate());

                            bundle.putDouble("latitude", clickedNote.getLatitude());
                            bundle.putDouble("longitude", clickedNote.getLongitude());

                            Navigation.findNavController(getView()).navigate(R.id.action_frag_main_maps_to_frag_note_screen1, bundle);
                        }
                        return true;
                    }
                });
            }
        });


        btnLogout2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                com.example.locationbasednotesapp.MainActivity mainActivity=(com.example.locationbasednotesapp.MainActivity) getActivity();
                mainActivity.logout();
                Navigation.findNavController(getView()).navigate((R.id.action_frag_main_maps_to_frag_login));
            }
        });

        btnListMode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.example.locationbasednotesapp.MainActivity mainActivity=(com.example.locationbasednotesapp.MainActivity) getActivity();

                Navigation.findNavController(getView()).navigate((R.id.action_frag_main_maps_to_frag_main_notes));
            }
        });

        btnNewNote2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.example.locationbasednotesapp.MainActivity mainActivity=(com.example.locationbasednotesapp.MainActivity) getActivity();

                Navigation.findNavController(getView()).navigate((R.id.action_frag_main_maps_to_frag_note_screen1));
            }
        });


        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private FragmentManager getSupportFragmentManager() {
        return getChildFragmentManager();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}