package com.example.locationbasednotesapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_main_notes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_main_notes extends Fragment implements RecyclerViewInterface{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static RecyclerView recycleView;
    private LinearLayoutManager layoutManager;

    public List<Note> dataSet2;


    private static NoteAdapter adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Frag_main_notes() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_main_notes.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_main_notes newInstance(String param1, String param2) {
        Frag_main_notes fragment = new Frag_main_notes();
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
        View view = inflater.inflate(R.layout.fragment_frag_main_notes, container, false);

        Bundle bundle = this.getArguments();

        dataSet2 = new ArrayList<>();

        recycleView=(RecyclerView)view.findViewById(R.id.recyclerView);

        layoutManager=new LinearLayoutManager(getContext());

        recycleView.setLayoutManager(layoutManager);

        recycleView.setItemAnimator(new DefaultItemAnimator());

        TextView messageTextView = view.findViewById(R.id.messageTextView);

        readNote(messageTextView);

        adapter= new NoteAdapter(getParentFragment().getContext() ,dataSet2, this);

        recycleView.setAdapter(adapter);


        Button btnMapMode = view.findViewById(R.id.btnMapMode);
        Button btnNewNote = view.findViewById(R.id.btnNewNote);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        TextView welcomeUser = view.findViewById(R.id.welcomeUser);


        // Set the text dynamically
        welcomeUser.setText("WELCOME "+ MainActivity.userName.toUpperCase() + "!");


        btnLogout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                com.example.locationbasednotesapp.MainActivity mainActivity=(com.example.locationbasednotesapp.MainActivity) getActivity();
                mainActivity.logout();
                Navigation.findNavController(getView()).navigate((R.id.action_frag_main_notes_to_frag_login));
            }
        });

        btnMapMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.example.locationbasednotesapp.MainActivity mainActivity=(com.example.locationbasednotesapp.MainActivity) getActivity();
                // Convert ArrayList<Note> to array of Parcelable
                Note[] noteArray = dataSet2.toArray(new Note[dataSet2.size()]);

                Bundle bundle = new Bundle();
                bundle.putParcelableArray("dataSet2", noteArray);
                Navigation.findNavController(getView()).navigate(R.id.action_frag_main_notes_to_frag_main_maps, bundle);
            }
        });

        btnNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.example.locationbasednotesapp.MainActivity mainActivity=(com.example.locationbasednotesapp.MainActivity) getActivity();

                Navigation.findNavController(getView()).navigate((R.id.action_frag_main_notes_to_frag_note_screen1));
            }
        });

        return view;
    }

    public void readNote(TextView messageTextView) {

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://location-based-notes-60b9d-default-rtdb.firebaseio.com/");
        DatabaseReference usersRef = database.getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    User user = userSnapshot.getValue(User.class);

                    // Now 'user' contains the data for each user
                    if (user != null) {
                        String email = user.getEmail();
                        if(email.equals(MainActivity.emailLogin)){

                            DatabaseReference userRef = usersRef.child(userId); // Use the user's ID
                            DatabaseReference notesRef = userRef.child("noteList");

                            // Retrieve notes from Firebase Realtime Database
                            notesRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dataSet2.clear();// Clear existing data

                                    for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                                        Note note = noteSnapshot.getValue(Note.class);
                                        dataSet2.add(note);
                                    }

                                    if (dataSet2.isEmpty()) {
                                        // The user doesn't have any notes yet
                                        messageTextView.setText("Don't have notes yet, create one!");
                                        messageTextView.setVisibility(View.VISIBLE); // Make the message visible
                                    } else {
                                        // The user has notes, hide the message
                                        messageTextView.setVisibility(View.GONE); // Hide the message
                                    }

                                    Collections.sort(dataSet2, new NoteDateComparator());

                                    adapter.setOnNoteClickListener(new NoteAdapter.OnNoteClickListener() {
                                        @Override
                                        public void onNoteClick(Note note) {
                                            // Handle the click event, e.g., open a new fragment with note details
                                            openNoteDetailsFragment(note);
                                            //Navigation.findNavController(getView()).navigate((R.id.action_frag_main_notes_to_frag_note_screen1));
                                        }

                                    });


                                    adapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle error
                                }
                            });

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void openNoteDetailsFragment(Note note) {
        // Create a new instance of NoteDetailsFragment
        Frag_note_screen1 noteDetailsFragment = new Frag_note_screen1();

        // Pass note details to the fragment using a Bundle
        Bundle bundle = new Bundle();
        bundle.putString("title", note.getTitle());
        bundle.putString("body", note.getBody());
        bundle.putString("date", note.getDate());
        noteDetailsFragment.setArguments(bundle);


        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, noteDetailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onItemClock(int position) {
        Navigation.findNavController(getView()).navigate((R.id.action_frag_main_notes_to_frag_note_screen1));

        Frag_note_screen1 noteDetailsFragment = new Frag_note_screen1();

        // Pass note details to the fragment using a Bundle
        Bundle bundle = new Bundle();
        bundle.putString("title", dataSet2.get(position).getTitle());
        bundle.putString("body", dataSet2.get(position).getBody());
        bundle.putString("date", dataSet2.get(position).getDate());
        noteDetailsFragment.setArguments(bundle);

    }
}