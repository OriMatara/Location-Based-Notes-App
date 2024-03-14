package com.example.locationbasednotesapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
// Add this at the top of your file
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://location-based-notes-60b9d-default-rtdb.firebaseio.com/");
    DatabaseReference usersRef = database.getReference("users");
    public static String emailLogin;

    private String date;
    private String title;
    private String body;
    public static String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(this);

    }

    public void logout(){
        // Log out the current user
        mAuth.signOut();
    }

    public void writeNoteToUser(double latitude, double longitude, String originalTitle){

        date = ((TextView)findViewById(R.id.etDate)).getText().toString();
        title = ((EditText)findViewById(R.id.etTitle)).getText().toString();
        body = ((EditText)findViewById(R.id.etBody)).getText().toString();

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    User user = userSnapshot.getValue(User.class);
                    Log.d("userkaka","the id is: "+userId);
                    Log.d("userbobo","user datafack: "+user.toString());
                    // Now 'user' contains the data for each user
                    if (user != null) {
                        String email = user.getEmail();
                        if(email.equals(emailLogin)){
                            DatabaseReference userRef = usersRef.child(userId); // Use the user's ID
                            DatabaseReference notesRef = userRef.child("noteList");

                            Note newNote = new Note(date, title,body, latitude, longitude );

                            // Push the note to the user's noteList
                            if((originalTitle!=null && (!(originalTitle.equals(newNote.getTitle()))))){
                                notesRef.child(originalTitle).removeValue();
                            }

                            notesRef.child(newNote.getTitle()).setValue(newNote);

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

    public void deleteNote(String noteDelete) {

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

                                    for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                                        String noteId = noteSnapshot.getKey();
                                        Note note = noteSnapshot.getValue(Note.class);
                                        if(noteId.equals(noteDelete)){
                                            notesRef.child(noteId).removeValue();
                                        }
                                    }
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

    public void loginFunc(View view) {

        emailLogin = ((EditText)findViewById(R.id.email)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();

        mAuth.signInWithEmailAndPassword(emailLogin,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "login Ok", Toast.LENGTH_LONG).show();

                            Navigation.findNavController(view).navigate((R.id.action_frag_login_to_frag_main_notes));
                        } else {
                            Toast.makeText(MainActivity.this, "login Fail", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        usersRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    User user = userSnapshot.getValue(User.class);
                    // Now 'user' contains the data for each user
                    if (user != null) {
                        String email = user.getEmail();
                        if(email.equals(emailLogin)){
                            userName = user.getName();
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

    public void RegisterFunc() {

        String email = ((EditText)findViewById(R.id.regEmail)).getText().toString();
        String password = ((EditText)findViewById(R.id.regPassword)).getText().toString();
        String userName = ((EditText)findViewById(R.id.regName)).getText().toString();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String userId = usersRef.push().getKey();
                            // Create a new user
                            User newUser = new User(userId, userName, email);

                            // Push the user to the database
                            usersRef.child(newUser.getId()).setValue(newUser);

                        } else {
                            Toast.makeText(MainActivity.this, "Register Fail", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}