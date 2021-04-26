package com.lumpyslounge.gardenjournal.Controller;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

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

import com.lumpyslounge.gardenjournal.Model.Model.Event;
import com.lumpyslounge.gardenjournal.R;
import com.lumpyslounge.gardenjournal.UI.EventViewModel;
import com.lumpyslounge.gardenjournal.Util.DateFormatter;
import com.lumpyslounge.gardenjournal.Util.JournalApi;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventAddFragment extends Fragment
{

    private static final int PHOTO_CODE = 1;
    ImageView imageViewPhoto;
    ImageView imageViewCamera;
    EditText editTextTitle;
    EditText editTextType;
    EditText editTextDate;
    EditText editTextNote;
    ProgressBar progressBar;
    


    private Uri imageUri;
    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Events");
    private DatePickerDialog datePickerDialog;


    public EventAddFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {


        View view = inflater.inflate(
                R.layout.fragment_event_add, container, false);
        setHasOptionsMenu(true);



        final EventViewModel model = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
        imageViewPhoto = view.findViewById(R.id.imageView_event_add_photo);
        imageViewCamera = view.findViewById(R.id.imageView_event_add_camera);
        editTextTitle = view.findViewById(R.id.editText_event_add_title);
        editTextDate = view.findViewById(R.id.editText_event_add_date);
        editTextType = view.findViewById(R.id.editText_event_add_type);
        editTextNote = view.findViewById(R.id.editText_event_add_note);

        progressBar = view.findViewById(R.id.progressBar_event_add);
        progressBar.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        authStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {

                } else {

                }
            }
        };

        imageViewCamera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent photoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent, PHOTO_CODE);

            }
        });
        editTextDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
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
            }
        });

        return view;
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()) {

            case R.id.save:
                try {
                    saveEvent();
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

    private void clearFields()
    {
        editTextTitle.setText("");
        editTextType.setText("");
        editTextDate.setText("");
        editTextNote.setText("");
        imageViewPhoto.setImageResource(R.drawable.watering);
    }


    private void saveEvent() throws ParseException
    {
        progressBar.setVisibility(View.VISIBLE);
        final String title = editTextTitle.getText().toString().trim();
        final String type = editTextType.getText().toString().trim();
        final String note = editTextNote.getText().toString().trim();
        final String date = editTextDate.getText().toString().trim();
        if(!TextUtils.isEmpty(date)) {
            final Timestamp timestamp = DateFormatter.getTimeStampFromString(date);
            if (!TextUtils.isEmpty(title)
                    && imageUri != null) {
                final StorageReference filepath = storageReference
                        .child("event_images")
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

                                        Event event = new Event();
                                        event.setTitle(title);
                                        event.setType(type);
                                        event.setDate(timestamp);
                                        event.setNote(note);
                                        event.setImageUrl(imageUrl);
                                        event.setTimeCreated(new Timestamp(new Date()));
                                        event.setUserName(currentUserName);
                                        event.setUserId(currentUserId);

                                        collectionReference.add(event)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                                                {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference)
                                                    {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), "Event Updated", Toast.LENGTH_SHORT).show();
                                                        Navigation.findNavController(getActivity(), R.id.nav_host).navigate(R.id.action_global_nav_event);

                                                    }
                                                }).addOnFailureListener(new OnFailureListener()
                                        {
                                            @Override
                                            public void onFailure(@NonNull Exception e)
                                            {

                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(getContext(), "DATA Upload Failure", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Title and Image must be Set", Toast.LENGTH_SHORT).show();
            }
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Date must be Set", Toast.LENGTH_SHORT).show();
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
    public void onStop()
    {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}


