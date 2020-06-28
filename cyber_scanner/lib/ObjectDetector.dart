import 'package:firebase_ml_vision/firebase_ml_vision.dart';
import 'package:flutter/services.dart';

final channel = const MethodChannel("com.example.cyber_scanner");

Future<dynamic> detect(FirebaseVisionImage image){
  return channel.invokeListMethod('ObjectDetector', [image]);
}