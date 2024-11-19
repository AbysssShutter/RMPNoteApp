package com.ikit.rmpnoteapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.protobuf.NullValue;
import com.ikit.rmpnoteapp.ui.AddNewNoteScreen;
import com.ikit.rmpnoteapp.ui.ChangeNote;
import com.ikit.rmpnoteapp.ui.Note;
import com.ikit.rmpnoteapp.ui.NoteAdapter;
import com.ikit.rmpnoteapp.ui.joke.Joke;
import com.ikit.rmpnoteapp.ui.joke.JokeApi;
import com.ikit.rmpnoteapp.ui.joke.JokesOnYou;
import com.yandex.mapkit.MapKitFactory;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    static int timer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MapKitFactory.setApiKey("7dff1683-11a0-4891-9fd7-445bf2669739");
        refreshNotes(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (id == R.id.nav_note) {
            refreshNotes(null);
            drawer.closeDrawers();
        } else if (id == R.id.nav_weather) {
            startActivity(new Intent(MainActivity.this, JokesOnYou.class));
            drawer.closeDrawers();
        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void refreshNotes(Note noteToEdit) {
        Realm.init(getApplicationContext());

        RealmConfiguration mRealmConfiguration = new RealmConfiguration.Builder()
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(mRealmConfiguration);

        Realm realm = Realm.getDefaultInstance();

        if (noteToEdit != null) {
            realm.copyToRealmOrUpdate(noteToEdit);
        }

        RealmResults<Note> notesList = realm.where(Note.class).findAll();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        NoteAdapter noteAdapter = new NoteAdapter(getApplicationContext(), notesList);
        recyclerView.setAdapter(noteAdapter);

        notesList.addChangeListener(new RealmChangeListener<RealmResults<Note>>() {
            @Override
            public void onChange(@NonNull RealmResults<Note> notes) {
                noteAdapter.notifyDataSetChanged();
            }
        });
    }

    public void openNewNoteScreen(View v) {
        startActivity(new Intent(MainActivity.this, AddNewNoteScreen.class));
    }
}