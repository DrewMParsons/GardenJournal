package com.lumpyslounge.gardenjournal.UI;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lumpyslounge.gardenjournal.Model.Model.Journal;

public class JournalViewModel extends ViewModel
{
    private final MutableLiveData<Journal> journalMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<Journal> getJournalMutableLiveData()
    {
        return journalMutableLiveData;
    }

    public void setJournalMutableLiveData(Journal journal){
        journalMutableLiveData.setValue(journal);
    }
}
