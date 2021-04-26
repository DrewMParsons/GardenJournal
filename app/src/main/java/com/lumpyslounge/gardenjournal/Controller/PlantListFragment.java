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

import com.lumpyslounge.gardenjournal.Model.Model.Plant;

import com.lumpyslounge.gardenjournal.R;
import com.lumpyslounge.gardenjournal.UI.PlantAdapter;
import com.lumpyslounge.gardenjournal.UI.PlantViewModel;
import com.lumpyslounge.gardenjournal.Util.JournalApi;


public class PlantListFragment extends Fragment
{
    Context context;
    private List<Plant> plantList;
    private RecyclerView recyclerView;
    private PlantAdapter plantAdapter;
    private FloatingActionButton fab;
    private PlantViewModel plantViewModel;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Plants");

    public PlantListFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        context = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);
        plantViewModel = ViewModelProviders.of((FragmentActivity) context).get(PlantViewModel.class);
        setHasOptionsMenu(true);
        fab = rootView.findViewById(R.id.fab_fragmentItemList);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Navigation.findNavController(getActivity(), R.id.nav_host).navigate(R.id.action_nav_plant_to_plantAddFragment);
            }
         
        });

        plantList = new ArrayList<>();

        recyclerView = rootView.findViewById(R.id.recyclerView_fragmentItemList);
        setUpRecyclerView();

        return rootView;
    }

    private void setUpRecyclerView()
    {
        Query query = collectionReference.whereEqualTo("userId", JournalApi.getInstance().getUserId())
                .orderBy("datePlanted", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Plant> options = new FirestoreRecyclerOptions.Builder<Plant>()
                .setQuery(query, Plant.class)
                .build();
        plantAdapter = new PlantAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(plantAdapter);

        plantAdapter.setOnItemClickListener(new PlantAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position)
            {
                Plant plant = documentSnapshot.toObject(Plant.class);
                plant.setId(documentSnapshot.getId());
                plantViewModel.setPlantMutableLiveData(plant);

                Navigation.findNavController(getActivity(), R.id.nav_host)
                        .navigate(R.id.action_nav_plant_to_plantDetailFragment);
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        plantAdapter.startListening();

    }

    @Override
    public void onStop()
    {
        super.onStop();
        plantAdapter.stopListening();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setQueryHint("Search by Variety");
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
                    .orderBy("datePlanted", Query.Direction.DESCENDING);
        }else {
            searchQuery = collectionReference.whereEqualTo("variety", query);
        }


        FirestoreRecyclerOptions<Plant> options = new FirestoreRecyclerOptions.Builder<Plant>()
                .setQuery(searchQuery, Plant.class)
                .build();
        PlantAdapter search = new PlantAdapter(options);

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
