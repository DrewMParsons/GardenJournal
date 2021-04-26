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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

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

import com.lumpyslounge.gardenjournal.Model.Model.Event;

import com.lumpyslounge.gardenjournal.UI.EventViewModel;
import com.lumpyslounge.gardenjournal.Util.DateFormatter;
import com.lumpyslounge.gardenjournal.Util.JournalApi;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventEditFragment extends Fragment implements View.OnClickListener
{
    private static final int PHOTO_CODE = 1;
    ImageView imageViewPhoto;
    ImageView imageViewCamera;
    EditText editTextTitle;
    EditText editTextType;
    EditText editTextDate;
    EditText editTextNote;
    ProgressBar progressBar;
    Event eventEdit;
    DatePickerDialog datePickerDialog;

    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Events");
    private Uri imageUri;


    public EventEditFragment()
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
        return inflater.inflate(R.layout.fragment_event_edit, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);



        editTextTitle = view.findViewById(R.id.editText_event_edit_title);
        editTextType = view.findViewById(R.id.editText_event_edit_type);
        editTextDate = view.findViewById(R.id.editText_event_edit_date);
        editTextDate.setOnClickListener(this);

        editTextNote = view.findViewById(R.id.editText_event_edit_note);
        imageViewPhoto = view.findViewById(R.id.imageView_event_edit_photo);
        imageViewCamera = view.findViewById(R.id.imageView_event_edit_camera);
        imageViewCamera.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progressBar_event_edit);
        progressBar.setVisibility(View.INVISIBLE);

        final EventViewModel model = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
        model.getEventMutableLiveData().observe(getActivity(), new Observer<Event>()
        {
            @Override
            public void onChanged(Event event)
            {
                eventEdit = event;
                displayReceivedData(eventEdit);

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



    protected void displayReceivedData(Event event)
    {


        editTextTitle.setText(event.getTitle());
        editTextNote.setText(event.getNote());
        editTextType.setText(event.getType());
        editTextDate.setText(DateFormatter.getStringFromTimestamp(event.getDate()));
        Picasso.get().load(event.getImageUrl()).placeholder(R.drawable.watering).
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
            case R.id.imageView_event_edit_camera:
                Intent photoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent,PHOTO_CODE);
                break;
            case R.id.editText_event_edit_date:
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
                    updateEvent();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.delete:
                deleteEvent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteEvent()
    {
        collectionReference.document(eventEdit.getId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        progressBar.setVisibility(View.INVISIBLE);
                        Navigation.findNavController(getActivity(),R.id.nav_host)
                                .navigate(R.id.action_global_nav_event);
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

    private void updateEvent() throws ParseException
    {
        progressBar.setVisibility(View.VISIBLE);
        final String title = editTextTitle.getText().toString().trim();
        final String type = editTextType.getText().toString().trim();
        final String note = editTextNote.getText().toString().trim();
        final String date = editTextDate.getText().toString().trim();
        final Timestamp timestamp = DateFormatter.getTimeStampFromString(date);

        if(!TextUtils.isEmpty(title)
                && !TextUtils.isEmpty(type)
                && !TextUtils.isEmpty(date)){
            if(imageUri != null) {
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
                                        eventEdit.setImageUrl(imageUrl);
                                        eventEdit.setTitle(title);
                                        eventEdit.setType(type);
                                        eventEdit.setNote(note);
                                        eventEdit.setDate(timestamp);
                                        eventEdit.setTimeCreated(new Timestamp(new Date()));
                                        eventEdit.setUserName(currentUserName);
                                        eventEdit.setUserId(currentUserId);


                                        collectionReference.document(eventEdit.getId()).set(eventEdit)
                                                .addOnSuccessListener(new OnSuccessListener<Void>()
                                                {
                                                    @Override
                                                    public void onSuccess(Void aVoid)
                                                    {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(),
                                                                "Event " + eventEdit.getTitle() + " Updated", Toast.LENGTH_SHORT).show();
                                                        Navigation.findNavController(getActivity(), R.id.nav_host)
                                                                .navigate(R.id.action_global_nav_event);

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener()
                                                {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e)
                                                    {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), "Event Update Failed", Toast.LENGTH_SHORT).show();
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
            }else //Update Event without changing and uploading a photo
                {
                    eventEdit.setTitle(title);
                    eventEdit.setType(type);
                    eventEdit.setNote(note);
                    eventEdit.setDate(timestamp);
                    eventEdit.setTimeCreated(new Timestamp(new Date()));
                    eventEdit.setUserName(currentUserName);
                    eventEdit.setUserId(currentUserId);


                    collectionReference.document(eventEdit.getId()).set(eventEdit)
                            .addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getContext(),
                                            "Event " + eventEdit.getTitle() + " Updated", Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(getActivity(), R.id.nav_host)
                                            .navigate(R.id.action_global_nav_event);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getContext(), "Event Update Failed", Toast.LENGTH_SHORT).show();

                                }
                            });
            }

        }else{
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Title, Type, and Date must be Set", Toast.LENGTH_SHORT).show();
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
