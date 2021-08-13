package com.example.familymapclientstudent.UserInterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.familymapclientstudent.R;

public class EventActivity extends AppCompatActivity {

    String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = new MapFragment();


        Bundle bundle = new Bundle();
        bundle.putString("eventID", eventID);
        fragment.setArguments(bundle);

        fragmentManager.beginTransaction().replace(R.id.eventFrame, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                          Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        return true;
    }
}