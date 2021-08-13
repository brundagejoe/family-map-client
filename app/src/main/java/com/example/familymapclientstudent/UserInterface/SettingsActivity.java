package com.example.familymapclientstudent.UserInterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.familymapclientstudent.Proxies.DataCache;
import com.example.familymapclientstudent.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DataCache dataCache = DataCache.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        Switch lifeStoryLineSwitch = findViewById(R.id.lifeStoryLineSwitch);
        Switch familyTreeLineSwitch = findViewById(R.id.familyTreeLineSwitch);
        Switch spouseLineSwitch = findViewById(R.id.spouseLineSwitch);
        Switch fathersSideFilterSwitch = findViewById(R.id.fatherSideFilterSwitch);
        Switch mothersSideFilterSwitch = findViewById(R.id.mothersSideFilterSwitch);
        Switch maleEventsFilterSwitch = findViewById(R.id.maleEventsFilterSwitch);
        Switch femaleEventsFilterSwitch = findViewById(R.id.femaleEventsFilterSwitch);

        lifeStoryLineSwitch.setChecked(dataCache.isShowLifeStoryLines());
        familyTreeLineSwitch.setChecked(dataCache.isShowFamilyTreeLines());
        spouseLineSwitch.setChecked(dataCache.isShowSpouseLines());
        fathersSideFilterSwitch.setChecked(dataCache.isShowFatherSide());
        mothersSideFilterSwitch.setChecked(dataCache.isShowMotherSide());
        maleEventsFilterSwitch.setChecked(dataCache.isShowMales());
        femaleEventsFilterSwitch.setChecked(dataCache.isShowFemales());

        lifeStoryLineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setShowLifeStoryLines(isChecked);
            }
        });

        familyTreeLineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setShowFamilyTreeLines(isChecked);
            }
        });

        spouseLineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setShowSpouseLines(isChecked);
            }
        });

        fathersSideFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setShowFatherSide(isChecked);
            }
        });

        mothersSideFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setShowMotherSide(isChecked);
            }
        });

        maleEventsFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setShowMales(isChecked);
            }
        });

        femaleEventsFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setShowFemales(isChecked);
            }
        });

        LinearLayout logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCache.getInstance().clearData();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                          Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        return true;
    }
}