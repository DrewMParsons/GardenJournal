package com.lumpyslounge.gardenjournal.Controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lumpyslounge.gardenjournal.R;
import com.squareup.picasso.Picasso;

import com.lumpyslounge.gardenjournal.Model.Model.Plant;
import com.lumpyslounge.gardenjournal.UI.PlantViewModel;
import com.lumpyslounge.gardenjournal.Util.DateFormatter;

public class PlantDetailFragment extends Fragment
{
    ImageView imageViewPhoto;
    TextView textViewName;
    TextView textViewVariety;
    TextView textViewDate;
    TextView textViewNote;
    FloatingActionButton fab;
    Plant plantEdit;

    public PlantDetailFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_plant_detail,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        final PlantViewModel plantViewModel = ViewModelProviders.of(getActivity()).get(PlantViewModel.class);
        plantViewModel.getPlantMutableLiveData().observe(this, new Observer<Plant>()
        {
            @Override
            public void onChanged(Plant plant)
            {
                plantEdit = plant;
                displayReceivedData(plantEdit);
            }
        });
        setHasOptionsMenu(true);
        imageViewPhoto = view.findViewById(R.id.imageView_plant_detail_photo);
        textViewName = view.findViewById(R.id.textView_plant_detail_name);
        textViewVariety = view.findViewById(R.id.textView_plant_detail_variety);
        textViewDate = view.findViewById(R.id.textView_plant_detail_date);
        textViewNote = view.findViewById(R.id.textView_plant_detail_note);
        fab = view.findViewById(R.id.fab_fragmentPlantDetail);

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                plantViewModel.setPlantMutableLiveData(plantEdit);
                Navigation.findNavController(getActivity(),R.id.nav_host)
                        .navigate(R.id.action_plantDetailFragment_to_plantEditFragment);
            }
        });


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {

        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem search = menu.findItem(R.id.search);
        search.setVisible(false);
        final MenuItem save = menu.findItem(R.id.save);
        save.setVisible(false);
        final MenuItem delete = menu.findItem(R.id.delete);
        delete.setVisible(false);

    }



    protected void displayReceivedData(Plant plant)
    {


        textViewName.setText(plant.getName());
        textViewNote.setText(plant.getNote());
        textViewVariety.setText(plant.getVariety());
        textViewDate.setText(DateFormatter.getStringFromTimestamp(plant.getDatePlanted()));
        Picasso.get().load(plant.getImageUrl()).placeholder(R.drawable.daffodils).
                fit().into(imageViewPhoto);

    }
}
