package com.lumpyslounge.gardenjournal.Controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lumpyslounge.gardenjournal.R;
import com.lumpyslounge.gardenjournal.Util.MyReceiver;
import com.squareup.picasso.Picasso;

import com.lumpyslounge.gardenjournal.Model.Model.Event;
import com.lumpyslounge.gardenjournal.UI.EventViewModel;
import com.lumpyslounge.gardenjournal.Util.DateFormatter;

public class EventDetailFragment extends Fragment
{
    ImageView imageViewPhoto;
    TextView textViewTitle;
    TextView textViewType;
    TextView textViewDate;
    TextView textViewNote;
    FloatingActionButton fab;
    Event eventEdit;

    public EventDetailFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_event_detail,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        final EventViewModel eventViewModel = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
        eventViewModel.getEventMutableLiveData().observe(this, new Observer<Event>()
        {
            @Override
            public void onChanged(Event event)
            {
                eventEdit = event;
                displayReceivedData(eventEdit);
            }
        });
        setHasOptionsMenu(true);
        imageViewPhoto = view.findViewById(R.id.imageView_event_detail_photo);
        textViewTitle = view.findViewById(R.id.textView_event_detail_title);
        textViewType = view.findViewById(R.id.textView_event_detail_type);
        textViewDate = view.findViewById(R.id.textView_event_detail_date);
        textViewNote = view.findViewById(R.id.textView_event_detail_note);
        fab = view.findViewById(R.id.fab_fragmentEventDetail);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                eventViewModel.setEventMutableLiveData(eventEdit);
                Navigation.findNavController(getActivity(),R.id.nav_host)
                        .navigate(R.id.action_eventDetailFragment_to_eventEditFragment);
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
        MenuItem remind = menu.findItem(R.id.remind);
        remind.setVisible(true);
        MenuItem share = menu.findItem(R.id.share);
        share.setVisible(true);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {

            switch (item.getItemId()){
                case R.id.remind:
                    setReminder();
                    return true;
                case R.id.share:
                    share();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);

            }


    }

    private void share()
    {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, eventEdit.getTitle());
        sharingIntent.putExtra(Intent.EXTRA_TEXT, eventEdit.getNote());
        startActivity(Intent.createChooser(sharingIntent,"Share Event Note:"));
    }

    private void setReminder()
    {
        Intent alarmIntent = new Intent(getContext(), MyReceiver.class);
        alarmIntent.putExtra(Intent.EXTRA_TITLE,eventEdit.getTitle());
        alarmIntent.putExtra(Intent.EXTRA_SUBJECT,eventEdit.getType());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),0,alarmIntent,0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP,eventEdit.getDate().getSeconds(),pendingIntent);
        Toast.makeText(getContext(), "Alert set for Due Date: " +
                DateFormatter.getStringFromTimestamp(eventEdit.getDate()), Toast.LENGTH_SHORT).show();
    }

    protected void displayReceivedData(Event event)
    {


        textViewTitle.setText(event.getTitle());
        textViewNote.setText(event.getNote());
        textViewType.setText(event.getType());
        textViewDate.setText(DateFormatter.getStringFromTimestamp(event.getDate()));
        Picasso.get().load(event.getImageUrl()).placeholder(R.drawable.watering)
                .error(R.drawable.watering).
                fit().into(imageViewPhoto);

    }

}
