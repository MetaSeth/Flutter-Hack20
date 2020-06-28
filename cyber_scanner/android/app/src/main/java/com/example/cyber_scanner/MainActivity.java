package com.example.cyber_scanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
    private String CHANNEL = "com.example.cyber_scanner";
    CustomObjectDetector customObjectDetector =  new CustomObjectDetector();

    private FirebaseVisionImage dataToVisionImage(Map<String, Object> imageData) throws IOException {
        String imageType = (String) imageData.get("type");
        assert imageType != null;

        switch (imageType) {
            case "file":
                final String imageFilePath = (String) imageData.get("path");
                final int rotation = getImageExifOrientation(imageFilePath);

                if (rotation == 0) {
                    File file = new File(imageFilePath);
                    return FirebaseVisionImage.fromFilePath(getApplicationContext(), Uri.fromFile(file));
                }

                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);

                final Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                final Bitmap rotatedBitmap =
                        Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                return FirebaseVisionImage.fromBitmap(rotatedBitmap);
            case "bytes":
                @SuppressWarnings("unchecked")
                Map<String, Object> metadataData = (Map<String, Object>) imageData.get("metadata");

                FirebaseVisionImageMetadata metadata =
                        new FirebaseVisionImageMetadata.Builder()
                                .setWidth((int) (double) metadataData.get("width"))
                                .setHeight((int) (double) metadataData.get("height"))
                                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                                .setRotation(getRotation((int) metadataData.get("rotation")))
                                .build();

                byte[] bytes = (byte[]) imageData.get("bytes");
                assert bytes != null;

                return FirebaseVisionImage.fromByteArray(bytes, metadata);
            default:
                throw new IllegalArgumentException(String.format("No image type for: %s", imageType));
        }
    }

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
                            Map<String, Object> options = call.argument("options");

                            FirebaseVisionImage image;
                            Map<String, Object> imageData = call.arguments();
                            try {
                                image = dataToVisionImage(imageData);
                            } catch (IOException exception) {
                                result.error("MLVisionDetectorIOError", exception.getLocalizedMessage(), null);
                                return;
                            }
                            customObjectDetector.detect(image, result);
                            break;

                    }
                });
    }
}
