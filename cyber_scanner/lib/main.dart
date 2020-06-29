import 'dart:async';

import 'package:cyber_scanner/ObjectDetector.dart';
import 'package:cyber_scanner/scanner_utils.dart';
import 'package:flutter/material.dart';
import 'dart:math';
import 'camera_preview_scanner.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);
  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  List position = [];
  double ratio = 0;

  Future<void> detectObject(image, rotation) async {
    List positions = await detect(ScannerUtils.createImage(image, rotation));
    if (ratio == 0) {
      double r = await getRatio(context);
      setState(() {
        ratio = r;
      });
    }
    setState(() {
      position = positions;
    });
  }

  _buildObjectMarker() {
    List<Positioned> positioned = position
        .map((e) => Positioned(
              left: e[0] / ratio,
              top: e[1] / ratio,
              right: e[2] / ratio,
              bottom: e[3] / ratio,
              child: RotationImage(),
            ))
        .toList();

    return positioned;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Stack(
        children: <Widget>[
          CameraPreviewScanner(
            onStreaming: (image, imageRotation) async {
              detectObject(image, imageRotation);
            },
          ),
          ..._buildObjectMarker(),
          Container(
            height: double.infinity,
            width: double.infinity,
            decoration: BoxDecoration(
                image: DecorationImage(
                    image: AssetImage(
              "images/border.png",
            ))),
          )
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: null,
        tooltip: 'Increment',
        child: Icon(Icons.add),
      ),
    );
  }
}

class RotationImage extends StatefulWidget {
  @override
  _RotationImageState createState() => _RotationImageState();
}

class _RotationImageState extends State<RotationImage> {
  int angle = 0;
  Timer timer;
  setAngle() {
    setState(() {
      angle = DateTime.now().millisecond  % 360;
    });
  }

  @override
  void initState() {
    super.initState();
    setState(() {
      timer = Timer.periodic(Duration(microseconds: 10), (timer) {
        setAngle();
      });
    });

  }

  @override
  Widget build(BuildContext context) {
    return Transform.rotate(
      angle: (pi / 180) * angle,
      child: Image.asset('images/object.png'),
    );
  }

  @override
  void dispose() {
    timer.cancel();
    super.dispose();
  }
}
