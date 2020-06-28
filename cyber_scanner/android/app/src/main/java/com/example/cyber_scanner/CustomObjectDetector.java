package com.example.cyber_scanner;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import java.io.IOException;
import java.util.List;

import io.flutter.plugin.common.MethodChannel;

public class CustomObjectDetector {

    ObjectDetector detector;

    public CustomObjectDetector() {
        ObjectDetectorOptions options = new ObjectDetectorOptions.Builder()
                .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
                .enableClassification()  // Optional
                .build();

      detector = ObjectDetection.getClient(options);
    }

    public void detect(byte[] path,  MethodChannel.Result result) throws IOException {

        InputImage image = InputImage.fromByteArray(path, 450, 800, 90, InputImage.IMAGE_FORMAT_NV21);
        detector.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<DetectedObject>>() {
                            @Override
                            public void onSuccess(List<DetectedObject> detectedObjects) {
                                result.success(detectedObjects);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                               result.error("200", e.getMessage(), e);
                            }
                        });
    }


}
