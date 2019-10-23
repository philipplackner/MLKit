package com.androiddevs.mlkit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FaceRecognitionActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK = 0;

    private ImageButton btnPhoto;
    private ImageView ivPhoto;
    private ProgressBar progressBar;
    private RecyclerView rvContours;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        btnPhoto = findViewById(R.id.btnPhoto);
        ivPhoto = findViewById(R.id.ivPhoto);
        progressBar = findViewById(R.id.progressBar);
        rvContours = findViewById(R.id.rvContours);

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
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK) {
            Uri uri = data.getData();
            scanContours(uri);
        }
    }

    private void scanContours(final Uri uri) {
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .build();
        try {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(this, uri);
            FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
            detector.detectInImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                            Bitmap bmp = drawContoursOnImage(uri, firebaseVisionFaces);
                            if(bmp != null) {
                                ivPhoto.setImageBitmap(bmp);
                                Toast.makeText(FaceRecognitionActivity.this,
                                        "SUCCESS", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(FaceRecognitionActivity.this,
                                        "Faces couldn't be scanned", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FaceRecognitionActivity.this,
                                    e.toString(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*private void scanFaces(final Uri uri) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(this, uri);
            FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector();
            detector.detectInImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                            Bitmap bmp = drawRectsOnImage(uri, firebaseVisionFaces);
                            if (bmp != null) {
                                ivPhoto.setImageBitmap(bmp);
                            } else {
                                Toast.makeText(FaceRecognitionActivity.this, "Faces couldn't be scanned", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FaceRecognitionActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private Bitmap drawRectsOnImage(Uri image, List<FirebaseVisionFace> faces) {
        try {
            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), image);
            Bitmap bmp = ImageDecoder.decodeBitmap(source);
            bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);

            Canvas canvas = new Canvas(bmp);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            paint.setColor(Color.RED);
            paint.setAntiAlias(true);

            for (FirebaseVisionFace face : faces) {
                canvas.drawRect(face.getBoundingBox(), paint);
            }

            return bmp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap drawContoursOnImage(Uri image, List<FirebaseVisionFace> faces) {
        try {
            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), image);
            Bitmap bmp = ImageDecoder.decodeBitmap(source);
            bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);

            Canvas canvas = new Canvas(bmp);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3f);
            paint.setAntiAlias(true);

            ArrayList<Pair<Integer, String>> colorList = setupColorList();
            rvContours.setAdapter(new RecyclerViewAdapter(this, colorList));
            rvContours.setLayoutManager(new LinearLayoutManager(this));

            for (FirebaseVisionFace face : faces) {
                drawContoursOnCanvas(getAllContourLists(face), colorList, canvas, paint);
            }

            return bmp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<List<FirebaseVisionPoint>> getAllContourLists(FirebaseVisionFace face) {
        ArrayList<List<FirebaseVisionPoint>> result = new ArrayList<>();
        // the loop goes from 2 to 14 because Firebase provides 14 different enum values
        // for ContourTypes. However, we don't want the first value (ALL_CONTOURS) because
        // then the connections between the ContourTypes would look strange. For example,
        // then there would be a part of the mouth connected with the eyes. To prevent that,
        // we need to get all the single contours.
        for(int i = 2; i <= 14; i++) {
            result.add(face.getContour(i).getPoints());
        }

        return result;
    }

    private void drawContoursOnCanvas(List<List<FirebaseVisionPoint>> allContours,
                                      List<Pair<Integer, String>> colorList,
                                      Canvas canvas, Paint paint) {
        for(int i = 0; i < allContours.size(); i++) {
            List<FirebaseVisionPoint> points = allContours.get(i);
            int curColor = colorList.get(i).first;
            paint.setColor(curColor);

            for (int j = 0; j < points.size() - 1; j++) {
                FirebaseVisionPoint point1 = points.get(j);
                FirebaseVisionPoint point2 = points.get(j + 1);
                canvas.drawLine(point1.getX(), point1.getY(), point2.getX(), point2.getY(), paint);
            }
            if (!points.isEmpty()) {
                FirebaseVisionPoint firstPoint = points.get(0);
                FirebaseVisionPoint lastPoint = points.get(points.size() - 1);
                canvas.drawLine(lastPoint.getX(), lastPoint.getY(), firstPoint.getX(), firstPoint.getY(), paint);
            }
        }
    }

    private ArrayList<Pair<Integer, String>> setupColorList() {
        Random r = new Random();
        ArrayList<Pair<Integer, String>> colorList = new ArrayList<>();
        ArrayList<String> allContourTypes = new ArrayList(Arrays.asList("Face",
                "Left Eyebrow Top", "Left Eyebrow Bottom", "Right Eyebrow Top",
                "Right Eyebrow Bottom", "Left Eye", "Right Eye", "Upper Lip Top",
                "Upper Lip Bottom", "Lower Lip Top", "Lower Lip Bottom",
                "Nose Bridge", "Nose Bottom"));
        for(int i = 2; i <= 14; i++) {


            int color = Color.argb(255, r.nextInt(255),
                    r.nextInt(255), r.nextInt(255));
            colorList.add(new Pair<>(color, allContourTypes.get(i-2)));
        }
        return colorList;
    }
}
