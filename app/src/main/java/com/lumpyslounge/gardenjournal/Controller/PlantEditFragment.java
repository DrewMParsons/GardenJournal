package com.lumpyslounge.gardenjournal.Controller;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lumpyslounge.gardenjournal.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.lumpyslounge.gardenjournal.Model.Model.Plant;
import com.lumpyslounge.gardenjournal.UI.PlantViewModel;
import com.lumpyslounge.gardenjournal.Util.DateFormatter;
import com.lumpyslounge.gardenjournal.Util.JournalApi;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlantEditFragment extends Fragment implements View.OnClickListener
{
    private static final int PHOTO_CODE = 1;
    ImageView imageViewPhoto;
    ImageView imageViewCamera;
    EditText editTextName;
    EditText editTextVariety;
    EditText editTextDate;
    EditText editTextNote;
    ProgressBar progressBar;
    Plant plantEdit;
    DatePickerDialog datePickerDialog;

    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Plants");
    private Uri imageUri;


    public PlantEditFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        setHasOptionsMenu(true);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        return inflater.inflate(R.layout.fragment_plant_edit, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        editTextName = view.findViewById(R.id.editText_plant_edit_name);
        editTextVariety = view.findViewById(R.id.editText_plant_edit_variety);
        editTextDate = view.findViewById(R.id.editText_plant_edit_date);
        editTextDate.setOnClickListener(this);

        editTextNote = view.findViewById(R.id.editText_plant_edit_note);
        imageViewPhoto = view.findViewById(R.id.imageView_plant_edit_photo);
        imageViewCamera = view.findViewById(R.id.imageView_plant_edit_camera);
        imageViewCamera.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progressBar_plant_edit);
        progressBar.setVisibility(View.INVISIBLE);

        final PlantViewModel model = ViewModelProviders.of(getActivity()).get(PlantViewModel.class);
        model.getPlantMutableLiveData().observe(this, new Observer<Plant>()
        {
            @Override
            public void onChanged(Plant plant)
            {
                plantEdit = plant;
                displayReceivedData(plantEdit);
            }
        });

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

    protected void displayReceivedData(Plant plant)
    {


        editTextName.setText(plant.getName());
        editTextNote.setText(plant.getNote());
        editTextVariety.setText(plant.getVariety());
        editTextDate.setText(DateFormatter.getStringFromTimestamp(plant.getDatePlanted()));
        Picasso.get().load(plant.getImageUrl()).placeholder(R.drawable.daffodils).
                fit().into(imageViewPhoto);

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
            case R.id.imageView_plant_edit_camera:
                Intent photoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent,PHOTO_CODE);
                break;
            case R.id.editText_plant_edit_date:
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
                    updatePlant();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.delete:
                deletePlant();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deletePlant()
    {
        collectionReference.document(plantEdit.getId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        progressBar.setVisibility(View.INVISIBLE);
                        Navigation.findNavController(getActivity(),R.id.nav_host)
                                .navigate(R.id.action_global_nav_plant);
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(getActivity(), "Delete Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updatePlant() throws ParseException
    {
        progressBar.setVisibility(View.VISIBLE);
        final String name = editTextName.getText().toString().trim();
        final String variety = editTextVariety.getText().toString().trim();
        final String note = editTextNote.getText().toString().trim();
        final String date = editTextDate.getText().toString().trim();
        final Timestamp timestamp = DateFormatter.getTimeStampFromString(date);

        if(!TextUtils.isEmpty(name)
                && !TextUtils.isEmpty(variety)
                && !TextUtils.isEmpty(date)){
            if(imageUri != null) {
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
                                        plantEdit.setImageUrl(imageUrl);
                                        plantEdit.setName(name);
                                        plantEdit.setVariety(variety);
                                        plantEdit.setNote(note);
                                        plantEdit.setDatePlanted(timestamp);
                                        plantEdit.setTimeCreated(new Timestamp(new Date()));
                                        plantEdit.setUserName(currentUserName);
                                        plantEdit.setUserId(currentUserId);


                                        collectionReference.document(plantEdit.getId()).set(plantEdit)
                                                .addOnSuccessListener(new OnSuccessListener<Void>()
                                                {
                                                    @Override
                                                    public void onSuccess(Void aVoid)
                                                    {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(),
                                                                "Plant " + plantEdit.getName() + " Updated", Toast.LENGTH_SHORT).show();
                                                        Navigation.findNavController(getActivity(), R.id.nav_host)
                                                                .navigate(R.id.action_global_nav_plant);

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener()
                                                {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e)
                                                    {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), "Plant Update Failed", Toast.LENGTH_SHORT).show();
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
            }else //Update Plant without changing and uploading a photo
                {
                    plantEdit.setName(name);
                    plantEdit.setVariety(variety);
                    plantEdit.setNote(note);
                    plantEdit.setDatePlanted(timestamp);
                    plantEdit.setTimeCreated(new Timestamp(new Date()));
                    plantEdit.setUserName(currentUserName);
                    plantEdit.setUserId(currentUserId);


                    collectionReference.document(plantEdit.getId()).set(plantEdit)
                            .addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getContext(),
                                            "Plant " + plantEdit.getName() + " Updated", Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(getActivity(), R.id.nav_host)
                                            .navigate(R.id.action_global_nav_plant);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getContext(), "Plant Update Failed", Toast.LENGTH_SHORT).show();

                                }
                            });
            }

        }else{
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Name, Variety, and Date must be Set", Toast.LENGTH_SHORT).show();
        }
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
