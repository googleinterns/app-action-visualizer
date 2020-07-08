package com.example.appactionvisualizer.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.appactionvisualizer.R;

public class ParameterActivity extends AppCompatActivity {


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameter);
        initView();
    }
}