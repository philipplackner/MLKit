package com.androiddevs.mlkit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnTextRecognition;
    private Button btnFaceRecognition;
    private Button btnImageLabeling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTextRecognition = findViewById(R.id.btnTextRecognition);
        btnFaceRecognition = findViewById(R.id.btnFaceRecognition);
        btnImageLabeling = findViewById(R.id.btnImageLabeling);

        btnTextRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TextRecognitionActivity.class));
            }
        });

        btnFaceRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FaceRecognitionActivity.class));
            }
        });

        btnImageLabeling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ImageLabelingActivity.class));
            }
        });
    }
}
