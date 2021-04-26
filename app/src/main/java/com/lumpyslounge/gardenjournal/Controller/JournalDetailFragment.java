package com.lumpyslounge.gardenjournal.Controller;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lumpyslounge.gardenjournal.R;
import com.squareup.picasso.Picasso;

import com.lumpyslounge.gardenjournal.Model.Model.Journal;
import com.lumpyslounge.gardenjournal.UI.JournalViewModel;
import com.lumpyslounge.gardenjournal.Util.DateFormatter;



/**
 * A simple {@link Fragment} subclass.
 */
public class JournalDetailFragment extends Fragment
{
    ImageView imageViewPhoto;
    TextView textViewTitle;
    TextView textViewTemperature;
    TextView textViewDate;
    TextView textViewNote;
    Journal journalEdit;
    FloatingActionButton fab;



    public JournalDetailFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {


        View view=  inflater.inflate(
                R.layout.fragment_journal_detail, container, false);
        final JournalViewModel model = ViewModelProviders.of(getActivity()).get(JournalViewModel.class);
        imageViewPhoto = view.findViewById(R.id.imageView_journalDetail_photo);
        textViewTitle = view.findViewById(R.id.textView_journalDetail_title);
        textViewDate = view.findViewById(R.id.textView_journalDetail_date);
        textViewTemperature = view.findViewById(R.id.textView_journalDetail_temperature);
        textViewNote = view.findViewById(R.id.textView_journalDetail_note);
        fab = view.findViewById(R.id.fab_fragmentJournalDetail);
        setHasOptionsMenu(true);


        model.getJournalMutableLiveData().observe(this, new Observer<Journal>()
        {
            @Override
            public void onChanged(Journal journal)
            {
                journalEdit = journal;
                displayReceivedData(journal);
            }
        });

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               model.setJournalMutableLiveData(journalEdit);
               Navigation.findNavController(getActivity(),R.id.nav_host).navigate(R.id.action_journalDetailFragment_to_journalEditFragment);

            }
        });
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageViewPhoto = view.findViewById(R.id.imageView_journalDetail_photo);
        textViewTitle = view.findViewById(R.id.textView_journalDetail_title);
        textViewDate = view.findViewById(R.id.textView_journalDetail_date);
        textViewTemperature = view.findViewById(R.id.textView_journalDetail_temperature);
        textViewNote = view.findViewById(R.id.textView_journalDetail_note);



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





    protected void displayReceivedData(Journal journal)
    {


        textViewTitle.setText(journal.getTitle());
        textViewNote.setText(journal.getNote());
        textViewTemperature.setText(journal.getTemperature());
        textViewDate.setText(DateFormatter.getStringFromTimestamp(journal.getDate()));
        Picasso.get().load(journal.getImageUrl()).placeholder(R.drawable.garden01).
                fit().into(imageViewPhoto);

    }


}


