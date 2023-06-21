import 'package:flutter/material.dart';
import 'package:vector_math/vector_math_64.dart' hide Colors;
import 'package:sensor_flutter/hardware_sensors.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  Vector3 _accelerometer = Vector3.zero();
  Vector3 _gyroscope = Vector3.zero();
  Vector3 _gravity = Vector3.zero();

  int? _groupValue = 0;

  @override
  void initState() {
    super.initState();
    hardwareSensors.gyroscope.listen((GyroscopeEvent event) {
      setState(() {
        _gyroscope.setValues(event.x, event.y, event.z);
      });
    });
    hardwareSensors.accelerometer.listen((AccelerometerEvent event) {
      setState(() {
        _accelerometer.setValues(event.x, event.y, event.z);
      });
    });
    hardwareSensors.gravity.listen((GravityEvent event) {
      setState(() {
        _gravity.setValues(event.x, event.y, event.z);
      });
    });

  }

  void setUpdateInterval(int? groupValue, int interval) {
    hardwareSensors.accelerometerUpdateInterval = interval;
    hardwareSensors.gyroscopeUpdateInterval = interval;
    hardwareSensors.gravityUpdateInterval = interval;
    setState(() {
      _groupValue = groupValue;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Motion Sensors'),
        ),
        body: SingleChildScrollView(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Text('Update Interval'),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Radio(
                    value: 1,
                    groupValue: _groupValue,
                    onChanged: (dynamic value) => setUpdateInterval(value, Duration.microsecondsPerSecond ~/ 1),
                  ),
                  Text("1 FPS"),
                  Radio(
                    value: 2,
                    groupValue: _groupValue,
                    onChanged: (dynamic value) => setUpdateInterval(value, Duration.microsecondsPerSecond ~/ 30),
                  ),
                  Text("30 FPS"),
                  Radio(
                    value: 3,
                    groupValue: _groupValue,
                    onChanged: (dynamic value) => setUpdateInterval(value, Duration.microsecondsPerSecond ~/ 60),
                  ),
                  Text("60 FPS"),
                ],
              ),
              Text('Accelerometer'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text('${_accelerometer.x.toStringAsFixed(4)}'),
                  Text('${_accelerometer.y.toStringAsFixed(4)}'),
                  Text('${_accelerometer.z.toStringAsFixed(4)}'),
                ],
              ),
              Text('Gravity'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text('${_gravity.x.toStringAsFixed(4)}'),
                  Text('${_gravity.y.toStringAsFixed(4)}'),
                  Text('${_gravity.z.toStringAsFixed(4)}'),
                ],
              ),
              Text('Gyroscope'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text('${_gyroscope.x.toStringAsFixed(4)}'),
                  Text('${_gyroscope.y.toStringAsFixed(4)}'),
                  Text('${_gyroscope.z.toStringAsFixed(4)}'),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}