package com.androiddevs.mlkit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;


import java.io.IOException;
import java.util.List;

public class ImageLabelingActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK = 0;

    private ImageButton btnPhoto;
    private ImageView ivPhoto;
    private ProgressBar progressBar;
    private TextView tvLabels;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_labeling);

        btnPhoto = findViewById(R.id.btnPhoto);
        ivPhoto = findViewById(R.id.ivPhoto);
        progressBar = findViewById(R.id.progressBar);
        tvLabels = findViewById(R.id.tvLabels);

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_PICK);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK) {
            Uri uri = data.getData();
            labelImage(uri);
        }
    }

    private void labelImage(final Uri uri) {
        try {
            FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(this, uri);
            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();
            progressBar.setVisibility(View.VISIBLE);

            labeler.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                            if(firebaseVisionImageLabels.isEmpty()) {
                                tvLabels.setText("No labels detected");
                            } else {
                                StringBuilder sb = new StringBuilder("Recognized labels: " + "\n");
                                for(int i = 1; i <= firebaseVisionImageLabels.size(); i++) {
                                    sb.append(i + ". " + firebaseVisionImageLabels.get(i-1).getText() + "\n");
                                }
                                tvLabels.setText(sb.toString());
                                progressBar.setVisibility(View.GONE);
                                ivPhoto.setImageURI(uri);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ImageLabelingActivity.this,
                                    "Oops, that didn't work!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
