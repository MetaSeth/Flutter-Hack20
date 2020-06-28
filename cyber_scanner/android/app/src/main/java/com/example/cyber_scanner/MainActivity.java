package com.example.cyber_scanner;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;

import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.io.IOException;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
    private String CHANNEL = "com.example.cyber_scanner";
    CustomObjectDetector customObjectDetector =  new CustomObjectDetector();


    private int getImageExifOrientation(String imageFilePath) throws IOException {
        ExifInterface exif = new ExifInterface(imageFilePath);
        int orientation =
                exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }

    private int getRotation(int rotation) {
        switch (rotation) {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException(String.format("No rotation for: %d", rotation));
        }
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler((call, result) -> {
                    switch (call.method){
                        case "ObjectDetector":

                            try {
                                customObjectDetector.detect(call.argument("byte"), result);
                            } catch (IOException e) {
                                result.error("error", e.getMessage(), e);
                                e.printStackTrace();
                            }
                            break;

                    }
                });
    }
}
