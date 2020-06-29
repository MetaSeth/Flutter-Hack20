import 'dart:typed_data';
import 'dart:ui';

import 'package:firebase_ml_vision/firebase_ml_vision.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

final channel = const MethodChannel("com.example.cyber_scanner");

Future<dynamic> detect(FirebaseVisionImage image) async {
    return  await channel.invokeListMethod(
      'ObjectDetector',
      <String, dynamic>{
      }..addAll(image.serialize()));
}

Future<double> getRatio(BuildContext context) async {
  Size flutterScreenSize = MediaQuery.of(context).size;
  final drawingScreenSize = await channel.invokeMethod('drawingSize');


  double ratio = drawingScreenSize['width'] / flutterScreenSize.width;

  return ratio;
}
