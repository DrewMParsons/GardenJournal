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

import com.lumpyslounge.gardenjournal.Model.Model.Journal;
import com.lumpyslounge.gardenjournal.R;
import com.lumpyslounge.gardenjournal.UI.JournalAdapter;
import com.lumpyslounge.gardenjournal.UI.JournalViewModel;
import com.lumpyslounge.gardenjournal.Util.JournalApi;



public class JournalListFragment extends Fragment
{
    Context context;

    private JournalViewModel journalViewModel;

    public JournalListFragment(){

    }


    private RecyclerView recyclerView;
    private JournalAdapter journalAdapter;
    private FloatingActionButton fab;


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Journals");


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        context = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);
        journalViewModel = ViewModelProviders.of((FragmentActivity) context).get(JournalViewModel.class);
        setHasOptionsMenu(true);

        fab = rootView.findViewById(R.id.fab_fragmentItemList);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Navigation.findNavController(getActivity(),R.id.nav_host).navigate(R.id.action_nav_home_to_nav_journal);

            }
        });

        recyclerView = rootView.findViewById(R.id.recyclerView_fragmentItemList);
        setUpRecyclerView();
        return rootView;


    }


    private void setUpRecyclerView()
    {
        Query query = collectionReference.whereEqualTo("userId", JournalApi.getInstance().getUserId())
                .orderBy("timeCreated", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Journal> options = new FirestoreRecyclerOptions.Builder<Journal>()
                .setQuery(query, Journal.class)
                .build();
        journalAdapter = new JournalAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(journalAdapter);

        journalAdapter.setOnItemClickListener(new JournalAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position)
            {
                Journal journal = documentSnapshot.toObject(Journal.class);
                journal.setId(documentSnapshot.getId());
                journalViewModel.setJournalMutableLiveData(journal);

                Navigation.findNavController(getActivity(),R.id.nav_host).navigate(R.id.action_nav_home_to_journalDetailFragment);


            }
        });

    }


    @Override
    public void onStart()
    {
        super.onStart();
        journalAdapter.startListening();

    }

    @Override
    public void onStop()
    {
        super.onStop();
        journalAdapter.stopListening();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setQueryHint("Search by Title");
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
                    .orderBy("timeCreated", Query.Direction.DESCENDING);
        }else {
            searchQuery = collectionReference.whereEqualTo("title", query);
        }



        FirestoreRecyclerOptions<Journal> options = new FirestoreRecyclerOptions.Builder<Journal>()
                .setQuery(searchQuery, Journal.class)
                .build();
        JournalAdapter search = new JournalAdapter(options);
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


