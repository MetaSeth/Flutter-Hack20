import 'package:camera/camera.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

class CameraPreviewScanner extends StatefulWidget {
  final Function onStreaming;

  const CameraPreviewScanner({Key key, this.onStreaming}) : super(key: key);
  @override
  State<StatefulWidget> createState() => _CameraPreviewScannerState();
}

class _CameraPreviewScannerState extends State<CameraPreviewScanner> {
  dynamic _scanResults;
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

      _isDetecting = true;
    });
  }

  Widget buildImage() {
    return Container(
      child: _camera == null
          ? const Center(
              child: Text(
                'Initializing Camera...',
                style: TextStyle(
                  color: Colors.green,
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
