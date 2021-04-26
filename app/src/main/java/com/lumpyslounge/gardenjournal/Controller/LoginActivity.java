package com.lumpyslounge.gardenjournal.Controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.lumpyslounge.gardenjournal.R;
import com.lumpyslounge.gardenjournal.Util.JournalApi;
import com.lumpyslounge.gardenjournal.Util.Validation;

public class LoginActivity extends AppCompatActivity
{
    private EditText editTextEmail;
    private EditText editTextPassword;

    private Button buttonLogin;
    private Button buttonRegister;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        firebaseAuth = firebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editText_login_email);
        editTextPassword = findViewById(R.id.editText_login_password);
        buttonLogin = findViewById(R.id.button_login_login);
        buttonRegister = findViewById(R.id.button_login_register);

        authStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser !=null){
                    currentUser = firebaseAuth.getCurrentUser();
                    String currentUserId = currentUser.getUid();

                    collectionReference.whereEqualTo("userId",currentUserId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>()
                            {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                                {
                                    if(e != null){
                                        return;
                                    }

                                    if(!queryDocumentSnapshots.isEmpty()){
                                        for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                                            JournalApi journalApi = JournalApi.getInstance();
                                            journalApi.setUserId(snapshot.getString("userId"));
                                            journalApi.setUsername(snapshot.getString("username"));

                                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                            finish();
                                        }
                                    }


                                }
                            });



                }else{
                }

            }
        };

        buttonRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                loginUser(editTextEmail.getText().toString(),editTextPassword.getText().toString());
                //finish();
                    
            }


        });
    }

    private void loginUser(String email, String password)
    {
        if(Validation.isInputValid(email)
        && Validation.isInputValid(password)){
            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if(user !=null){
                            final String currentUserId = user.getUid();
                            collectionReference.whereEqualTo("userId",currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>()
                                    {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                                        {
                                            if(e !=null){
                                                return;
                                            }
                                            for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {
                                                JournalApi journalApi = JournalApi.getInstance();
                                                journalApi.setUsername(snapshot.getString("username"));
                                                journalApi.setUserId(currentUserId);
                                            }
                                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                            finish();
                                        }
                                    });}
                            else {
                                Toast.makeText(LoginActivity.this, "Password or Email incorrect", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(LoginActivity.this, "Password or Email incorrect", Toast.LENGTH_SHORT).show();

                }
            });
            
        }else{
            Toast.makeText(this, "Password and Email must be Entered", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        currentUser =firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(firebaseAuth !=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
