import 'package:camera/camera.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import 'detector_painters.dart';

class CameraPreviewScanner extends StatefulWidget {
  final Function onStreaming;

  const CameraPreviewScanner({Key key, this.onStreaming}) : super(key: key);
  @override
  State<StatefulWidget> createState() => _CameraPreviewScannerState();
}

class _CameraPreviewScannerState extends State<CameraPreviewScanner> {
  //dynamic _scanResults;
  CameraController _camera;

  bool _isDetecting = false;
  CameraLensDirection _direction = CameraLensDirection.back;

  @override
  void initState() {
    super.initState();
    _initializeCamera();
  }

  void _initializeCamera() async {
    final CameraDescription description = await availableCameras().then(
      (List<CameraDescription> cameras) => cameras.firstWhere(
        (CameraDescription camera) => camera.lensDirection == _direction,
      ),
    );

    _camera = CameraController(
      description,
      defaultTargetPlatform == TargetPlatform.android
          ? ResolutionPreset.high
          : ResolutionPreset.low,
    );
    await _camera.initialize();

    _camera.startImageStream((CameraImage image) {
      widget.onStreaming(image,description.sensorOrientation);
      if (_isDetecting) return;

      setState(() {
        _isDetecting = true;
      });
    });
  }

  Widget _buildResults() {
    const Text noResultsText = Text('No results!');
    if (/*_scanResults == null ||*/
        _camera == null || !_camera.value.isInitialized) {
      return noResultsText;
    }

    final Size imageSize = Size(
      _camera.value.previewSize.height,
      _camera.value.previewSize.width,
    );

    if (testlistObjectLocations.length > 0) {
      return CustomPaint(
        painter: ObjectDetectorPainter(
            imageSize, buildObjectLocations(listObjectLocations)),
      );
    } else
      return noResultsText;
  }

  // Instanciate Rect for each list of 4 coordonates
  List<Rect> buildObjectLocations(List<List<double>> listCoords) {
    for (var i = 0; i < listCoords.length; i++) {
      setState(() {
        listObjectLocations.add(new Rect.fromLTRB(listCoords[i][0],
            listCoords[i][1], listCoords[i][2], listCoords[i][3]));
      });
    }
    return listObjectLocations;
  }

  Widget buildImage() {
    return Container(
      child: _camera == null
          ? const Center(
              child: Text(
                'Cyber connection',
                style: TextStyle(
                  color: Color(0x0492D5),
                  fontSize: 30.0,
                ),
              ),
            )
          : Stack(
              fit: StackFit.expand,
              children: <Widget>[
                CameraPreview(_camera),
                // _buildResults(),
              ],
            ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return buildImage();
  }
}
