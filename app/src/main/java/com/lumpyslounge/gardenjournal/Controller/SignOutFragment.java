package com.lumpyslounge.gardenjournal.Controller;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.lumpyslounge.gardenjournal.R;


public class SignOutFragment extends Fragment
{


    private Button buttonSignOut;
    private FirebaseAuth firebaseAuth;


    public SignOutFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_out, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        buttonSignOut = view.findViewById(R.id.button_sign_out);
        buttonSignOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(),LoginActivity.class));

            }
        });
    }







}
