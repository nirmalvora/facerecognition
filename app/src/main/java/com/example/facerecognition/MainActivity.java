package com.example.facerecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.facerecognition.data_base.SimpleDBHelper;
import com.example.facerecognition.databinding.ActivityAddFaceBinding;
import com.example.facerecognition.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        try {
            SimpleDBHelper dbHandler = new SimpleDBHelper(MainActivity.this);
            dbHandler.addNewCourse("Nirmal","Vora");
        }catch (Exception e){}
        binding.addFace.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), AddFaceActivity.class);
            startActivity(i);
        });
        binding.recognizeFace.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), RecognitionActivity.class);
            startActivity(i);
        });

    }
}