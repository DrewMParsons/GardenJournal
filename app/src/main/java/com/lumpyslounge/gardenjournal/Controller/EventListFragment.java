package com.lumpyslounge.gardenjournal.Controller;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import com.lumpyslounge.gardenjournal.Model.Model.Event;

import com.lumpyslounge.gardenjournal.R;
import com.lumpyslounge.gardenjournal.UI.EventAdapter;
import com.lumpyslounge.gardenjournal.UI.EventViewModel;
import com.lumpyslounge.gardenjournal.Util.JournalApi;


public class EventListFragment extends Fragment
{
    Context context;
    private List<Event> eventList;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private FloatingActionButton fab;
    private EventViewModel eventViewModel;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Events");

    public EventListFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        context = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_item_list,container,false);
        eventViewModel = ViewModelProviders.of((FragmentActivity) context).get(EventViewModel.class);

        setHasOptionsMenu(true);
        eventList = new ArrayList<>();

        recyclerView = rootView.findViewById(R.id.recyclerView_fragmentItemList);
        setUpRecyclerView();
        fab = rootView.findViewById(R.id.fab_fragmentItemList);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Navigation.findNavController(getActivity(), R.id.nav_host).navigate(R.id.action_nav_event_to_eventAddFragment);
            }
        });
        return rootView;
    }

    private void setUpRecyclerView()
    {
        Query query = collectionReference.whereEqualTo("userId", JournalApi.getInstance().getUserId())
                .orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query,Event.class)
                .build();
        eventAdapter = new EventAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(eventAdapter);

        eventAdapter.setOnItemClickListener(new EventAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position)
            {
                Event event = documentSnapshot.toObject(Event.class);
                event.setId(documentSnapshot.getId());
                eventViewModel.setEventMutableLiveData(event);

                Navigation.findNavController(getActivity(),R.id.nav_host)
                        .navigate(R.id.action_nav_event_to_eventDetailFragment);
            }
        });
    }


    @Override
    public void onStart()
    {
        super.onStart();
        eventAdapter.startListening();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        eventAdapter.stopListening();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setQueryHint("Search by Type");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                searchData(newText);
                return true;
            }
        });
    }

    private void searchData(String query)
    {
        Query searchQuery;

        if(query.isEmpty()){
            searchQuery = collectionReference.whereEqualTo("userId", JournalApi.getInstance().getUserId())
                    .orderBy("date", Query.Direction.DESCENDING);
        }else {
            searchQuery = collectionReference.whereEqualTo("type", query);
        }

        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(searchQuery, Event.class)
                .build();
        EventAdapter search = new EventAdapter(options);

        recyclerView.swapAdapter(search,true);
        search.notifyDataSetChanged();
        search.startListening();




    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }
}
