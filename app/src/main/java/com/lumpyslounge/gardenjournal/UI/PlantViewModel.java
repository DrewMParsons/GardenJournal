package com.lumpyslounge.gardenjournal.UI;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lumpyslounge.gardenjournal.Model.Model.Plant;

public class PlantViewModel extends ViewModel
{
    private final MutableLiveData<Plant> plantMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<Plant> getPlantMutableLiveData()
    {
        return plantMutableLiveData;
    }

    public void setPlantMutableLiveData(Plant plant){
        plantMutableLiveData.setValue(plant);
    }
}
