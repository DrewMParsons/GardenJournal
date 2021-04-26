package com.lumpyslounge.gardenjournal.Controller;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.lumpyslounge.gardenjournal.R;
import com.lumpyslounge.gardenjournal.Util.JournalApi;
import com.lumpyslounge.gardenjournal.Util.Validation;

public class RegisterActivity extends AppCompatActivity
{
    EditText editTextUsername;
    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonRegister;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null){
                    //user is already logged in
                }else{
                    //no user yet

            }
            }
        };

        editTextUsername = findViewById(R.id.editText_register_username);
        editTextEmail = findViewById(R.id.editText_register_email);
        editTextPassword = findViewById(R.id.editText_register_password);
        buttonRegister = findViewById(R.id.button_register_register);

        buttonRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!TextUtils.isEmpty(editTextUsername.getText().toString())
                && !TextUtils.isEmpty(editTextEmail.getText().toString())
                &&!TextUtils.isEmpty(editTextPassword.getText().toString())){

                    String username = editTextUsername.getText().toString().trim();
                    String email = editTextEmail.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();

                    if(Validation.isEmailValid(email)) {
                        createUserEmailAccount(email, password, username);
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Email entered is not a valid address", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(RegisterActivity.this, "Empty Fields Not Allowed", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void createUserEmailAccount(String email, String password, final String username){
        if(!TextUtils.isEmpty(email)
        && !TextUtils.isEmpty(password)
        && !TextUtils.isEmpty(username)){

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful()){

                                currentUser = firebaseAuth.getCurrentUser();
                                assert currentUser != null;
                                final String currentUserId = currentUser.getUid();

                                Map<String,String> userObj = new HashMap<>();
                                userObj.put("userId",currentUserId);
                                userObj.put("username",username);

                                //save to Firestore DB
                                collectionReference.add(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                                        {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference)
                                            {
                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                                                        {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                                            {
                                                                if(Objects.requireNonNull(task.getResult()).exists()){
                                                                    String name = task.getResult().getString("username");
                                                                    JournalApi journalApi = JournalApi.getInstance();
                                                                    journalApi.setUserId(currentUserId);
                                                                    journalApi.setUsername(name);
                                                                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                                                    intent.putExtra("username",name);
                                                                    intent.putExtra("userId",currentUserId);
                                                                    startActivity(intent);

                                                                }else{
                                                                    Toast.makeText(RegisterActivity.this, "DocRef no Exist", Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener()
                                        {
                                            @Override
                                            public void onFailure(@NonNull Exception e)
                                            {
                                                Toast.makeText(RegisterActivity.this, "Saving to COllection Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }else{
                                Toast.makeText(RegisterActivity.this, "Task didnt complete", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }else{
            //else for if(textutil)
            Toast.makeText(this, "TextUtil is Empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
