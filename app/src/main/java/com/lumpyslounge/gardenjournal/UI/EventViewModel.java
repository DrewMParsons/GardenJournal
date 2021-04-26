package com.lumpyslounge.gardenjournal.UI;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lumpyslounge.gardenjournal.Model.Model.Event;

public class EventViewModel extends ViewModel
{
    private final MutableLiveData<Event> eventMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<Event> getEventMutableLiveData()
    {
        return eventMutableLiveData;
    }

    public void setEventMutableLiveData(Event event){
        eventMutableLiveData.setValue(event);
    }
}
