import 'dart:typed_data';

import 'package:firebase_ml_vision/firebase_ml_vision.dart';
import 'package:flutter/services.dart';

final channel = const MethodChannel("com.example.cyber_scanner");

Future<dynamic> detect(Uint8List imagePath) async {
    return  await channel.invokeListMethod(
      'ObjectDetector',
      <String, dynamic>{
        'byte': imagePath,
      });
}

