package com.lumpyslounge.gardenjournal.Controller;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.lumpyslounge.gardenjournal.Model.Model.Plant;
import com.lumpyslounge.gardenjournal.R;
import com.lumpyslounge.gardenjournal.Util.DateFormatter;
import com.lumpyslounge.gardenjournal.Util.JournalApi;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlantAddFragment extends Fragment implements View.OnClickListener
{
    private static final int PHOTO_CODE = 1;
    DatePickerDialog datePickerDialog;
    EditText editTextName;
    EditText editTextVariety;
    EditText editTextDate;
    EditText editTextNote;
    private ImageView imageViewCamera;
    private ImageView imageViewPhoto;
    ProgressBar progressBar;

    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Plants");
    private Uri imageUri;


    public PlantAddFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view =inflater.inflate(R.layout.fragment_plant_add, container, false);
        setHasOptionsMenu(true);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        editTextName = view.findViewById(R.id.editText_plant_add_name);
        editTextVariety = view.findViewById(R.id.editText_plant_add_variety);
        editTextDate = view.findViewById(R.id.editText_plant_add_date);
        editTextDate.setOnClickListener(this);

        editTextNote = view.findViewById(R.id.editText_plant_add_note);
        imageViewPhoto = view.findViewById(R.id.imageView_plant_add_photo);
        imageViewCamera = view.findViewById(R.id.imageView_plant_add_camera);
        imageViewCamera.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progressBar_plant_add);
        progressBar.setVisibility(View.INVISIBLE);

        authStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){

                }else{

                }
            }
        };

    }

    @Override
    public void onStart()
    {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
        currentUserName = JournalApi.getInstance().getUsername();
        currentUserId = firebaseUser.getUid();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_CODE && resultCode == getActivity().RESULT_OK) {
            if (data != null &&
                    data.getData() != null) {
                imageUri = data.getData();
                imageViewPhoto.setImageURI(imageUri);
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.imageView_plant_add_camera:
                Intent photoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent,PHOTO_CODE);
                break;
            case R.id.editText_plant_add_date:
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        editTextDate.setText(DateFormatter.getStringFromDate(year,month,dayOfMonth));

                    }
                },year,month,day);
                datePickerDialog.show();
                break;


        }


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()) {
            
            case R.id.save:
                try {
                    savePlant();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.delete:
                clearFields();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePlant() throws ParseException
    {
        progressBar.setVisibility(View.VISIBLE);
        final String name = editTextName.getText().toString().trim();
        final String variety = editTextVariety.getText().toString().trim();
        final String note = editTextNote.getText().toString().trim();
        final String date = editTextDate.getText().toString().trim();
        if(!TextUtils.isEmpty(date)) {
            final Timestamp timestamp = DateFormatter.getTimeStampFromString(date);

            if (!TextUtils.isEmpty(name)
                    && !TextUtils.isEmpty(variety)
                    && imageUri != null) {
                final StorageReference filepath = storageReference
                        .child("plant_images")
                        .child("image_" + Timestamp.now().getSeconds());
                filepath.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                        {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                            {
                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                {
                                    @Override
                                    public void onSuccess(Uri uri)
                                    {
                                        String imageUrl = uri.toString();

                                        Plant plant = new Plant();
                                        plant.setName(name);
                                        plant.setVariety(variety);
                                        plant.setNote(note);
                                        plant.setDatePlanted(timestamp);
                                        plant.setImageUrl(imageUrl);
                                        plant.setTimeCreated(new Timestamp(new Date()));
                                        plant.setUserName(currentUserName);
                                        plant.setUserId(currentUserId);


                                        collectionReference.add(plant)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                                                {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference)
                                                    {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Navigation.findNavController(getActivity(), R.id.nav_host)
                                                                .navigate(R.id.action_global_nav_plant);

                                                    }
                                                }).addOnFailureListener(new OnFailureListener()
                                        {
                                            @Override
                                            public void onFailure(@NonNull Exception e)
                                            {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(getContext(), "Data Upload Failure", Toast.LENGTH_SHORT).show();


                                            }
                                        });

                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getContext(), "Image Upload Failure", Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Name, Variety, and Photo must be Set", Toast.LENGTH_SHORT).show();
            }
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Date Planted Must Be Set", Toast.LENGTH_SHORT).show();
        }


    }

    private void clearFields()
    {
        editTextName.setText("");
        editTextVariety.setText("");
        editTextDate.setText("");
        editTextNote.setText("");
        imageViewPhoto.setImageResource(R.drawable.daffodils);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {

       
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem search = menu.findItem(R.id.search);
        search.setVisible(false);
        final MenuItem save = menu.findItem(R.id.save);
        save.setVisible(true);
        final MenuItem delete = menu.findItem(R.id.delete);
        delete.setVisible(true);


    }
}
