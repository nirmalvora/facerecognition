package com.example.facerecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.facerecognition.data_base.SimpleDBHelper;
import com.example.facerecognition.databinding.ActivityAddFaceBinding;
import com.example.facerecognition.databinding.ActivityMainBinding;
import com.example.facerecognition.usb_serial_read.UsbSerialActivity;
import com.google.gson.Gson;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        float[] data = new float[4];
       data[0] = 10.10f;
       data[1] = 30.3f;
       data[2] = 40.60f;
       data[3] = 77.50f;

        try {
            SimpleDBHelper dbHandler = new SimpleDBHelper(MainActivity.this);
            dbHandler.addNewCourse("Nirmal",data);
            dbHandler.getData();
        }catch (Exception e){
            Log.e("GetDataFromDB", "onCreate: "+e.toString() );
        }
        binding.addFace.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), AddFaceActivity.class);
            startActivity(i);
        });
        binding.recognizeFace.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), RecognitionActivity.class);
            startActivity(i);
        });   binding.usbSerialData.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), UsbSerialActivity.class);
            startActivity(i);
        });

    }
}