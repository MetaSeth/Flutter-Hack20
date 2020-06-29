package com.example.cyber_scanner;


import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.flutter.plugin.common.MethodChannel;

public class CustomObjectDetector {

    FirebaseVisionObjectDetector detector;

    public CustomObjectDetector() {
        FirebaseVisionObjectDetectorOptions options = new FirebaseVisionObjectDetectorOptions.Builder()
                .setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE)
                .enableMultipleObjects()
                .enableClassification()
                .build();

      detector =   FirebaseVision.getInstance().getOnDeviceObjectDetector(options);

    }

    public void detect(final FirebaseVisionImage image,  MethodChannel.Result result) throws IOException {
        detector.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionObject>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionObject> firebaseVisionObjects) {
                        List<List<Integer>> ok = new ArrayList<>();

                        if (firebaseVisionObjects.size() > 0)
                        {
                            for (FirebaseVisionObject obj: firebaseVisionObjects){

                                Rect bounds = obj.getBoundingBox();

                                List<Integer> rect = new ArrayList<>();
                                rect.add( bounds.left);
                                rect.add( bounds.top);
                                rect.add( bounds.right);
                                rect.add(bounds.bottom);
                                ok.add(rect);
                            }
                        }
                        result.success(ok);
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
    }


}
