import 'dart:ui';

import 'package:flutter/material.dart';

class ObjectDetectorPainter extends CustomPainter {
  ObjectDetectorPainter(this.absoluteImageSize, this.objectLocations);

  final Size absoluteImageSize;
  final List<Rect> objectLocations;

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint();
    paint.style=PaintingStyle.stroke;
    for (var i = 0; i < objectLocations.length; i++) {
      canvas.drawRect(objectLocations[i],paint
          ); // TODO: custom paint
    }
  }

  @override
  bool shouldRepaint(ObjectDetectorPainter oldDelegate) =>
      objectLocations != oldDelegate.objectLocations;
}
