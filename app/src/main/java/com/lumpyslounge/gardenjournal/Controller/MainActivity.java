package com.lumpyslounge.gardenjournal.Controller;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.navigation.NavController;

import androidx.navigation.Navigation;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.lumpyslounge.gardenjournal.R;


public class MainActivity extends AppCompatActivity
{

    AppBarConfiguration appBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.nav_home,R.id.nav_event,R.id.nav_plant)
                        .setDrawerLayout(drawer).build();


        navigationView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this,R.id.nav_host);

        NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration);

        NavigationUI.setupWithNavController(navigationView,navController);



    }

    @Override
    public boolean onSupportNavigateUp()
    {
        NavController navController = Navigation.findNavController(this, R.id.nav_host);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items,menu);
        MenuItem delete = menu.findItem(R.id.delete);
        delete.setVisible(false);
        MenuItem save = menu.findItem(R.id.save);
        save.setVisible(false);
        MenuItem remind = menu.findItem(R.id.remind);
        remind.setVisible(false);
        MenuItem share = menu.findItem(R.id.share);
        share.setVisible(false);

        return true;
    }


}





