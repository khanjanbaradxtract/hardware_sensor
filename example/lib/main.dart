import 'package:flutter/material.dart';
import 'package:vector_math/vector_math_64.dart' hide Colors;
import 'package:sensor_flutter/sensor_flutter.dart';

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
  Vector3 _uncalibratedGyroscope = Vector3.zero();
  Vector3 _gravity = Vector3.zero();
  Vector3 _magnetometer = Vector3.zero();
  Vector3 _uncalibratedMagnetometer = Vector3.zero();
  double _pressure = 0.0;
  Vector3 _gameRotation = Vector3.zero();
  Vector3 _rotation = Vector3.zero();
  Vector3 _linearAcceleration = Vector3.zero();


  int? _groupValue = 0;

  @override
  void initState() {
    super.initState();
    hardwareSensors.gyroscope.listen((GyroscopeEvent event) {
      setState(() {
        _gyroscope.setValues(event.x, event.y, event.z);
      });
    });
    hardwareSensors.uncalibratedGyroscope.listen((UncalibratedGyroscopeEvent event) {
      setState(() {
        _uncalibratedGyroscope.setValues(event.x, event.y, event.z);
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
    hardwareSensors.magnetometer.listen((MagnetometerEvent event) {
      setState(() {
        _magnetometer.setValues(event.x, event.y, event.z);
      });
    });
    hardwareSensors.uncalibratedMagnetometer.listen((UncalibratedMagnetometerEvent event) {
      setState(() {
        _uncalibratedMagnetometer.setValues(event.x, event.y, event.z);
      });
    });
    hardwareSensors.pressure.listen((PressureEvent event) {
      setState(() {
        _pressure = event.x;
      });
    });
    hardwareSensors.isGameRotationAvailable().then((available) {
      if (available) {
        hardwareSensors.gameRotaion.listen((GameRotationEvent event) {
          setState(() {
            _gameRotation.setValues(event.yaw, event.pitch, event.roll);
          });
        });
      }
    });
    hardwareSensors.rotation.listen((RotationEvent event) {
      setState(() {
        _rotation.setValues(event.yaw, event.pitch, event.roll);
      });
    });
    hardwareSensors.linearAcceleration.listen((LinearAccelerationEvent event) {
      setState(() {
        _linearAcceleration.setValues(event.x, event.y, event.z);
      });
    });

  }



  void setUpdateInterval(int? groupValue, int interval) {
    hardwareSensors.accelerometerUpdateInterval = interval;
    hardwareSensors.gyroscopeUpdateInterval = interval;
    hardwareSensors.uncalibratedGyroscopeUpdateInterval = interval;
    hardwareSensors.gravityUpdateInterval = interval;
    hardwareSensors.magnetometerUpdateInterval = interval;
    hardwareSensors.uncalibratedMagnetometerUpdateInterval = interval;
    hardwareSensors.pressureUpdateInterval = interval;
    hardwareSensors.gameRotationUpdateInterval = interval;
    hardwareSensors.rotationUpdateInterval = interval;
    hardwareSensors.linearAccelerationUpdateInterval = interval;



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
              Text('Linear Acceleration'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text('${_linearAcceleration.x.toStringAsFixed(4)}'),
                  Text('${_linearAcceleration.y.toStringAsFixed(4)}'),
                  Text('${_linearAcceleration.z.toStringAsFixed(4)}'),
                ],
              ),
              Text('Game Rotaion'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text('${_gameRotation.x.toStringAsFixed(4)}'),
                  Text('${_gameRotation.y.toStringAsFixed(4)}'),
                  Text('${_gameRotation.z.toStringAsFixed(4)}'),
                ],
              ),
              Text('Rotation'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text('${_rotation.x.toStringAsFixed(4)}'),
                  Text('${_rotation.y.toStringAsFixed(4)}'),
                  Text('${_rotation.z.toStringAsFixed(4)}'),
                ],
              ),
              Text('Pressure'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text('${_pressure}')
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
              Text('Uncalibrated Gyroscope'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text('${_uncalibratedGyroscope.x.toStringAsFixed(4)}'),
                  Text('${_uncalibratedGyroscope.y.toStringAsFixed(4)}'),
                  Text('${_uncalibratedGyroscope.z.toStringAsFixed(4)}'),
                ],
              ),
              Text('Magnetometer'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text('${_magnetometer.x.toStringAsFixed(4)}'),
                  Text('${_magnetometer.y.toStringAsFixed(4)}'),
                  Text('${_magnetometer.z.toStringAsFixed(4)}'),
                ],
              ),
              Text('Uncalibrated Magnetometer'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text('${_uncalibratedMagnetometer.x.toStringAsFixed(4)}'),
                  Text('${_uncalibratedMagnetometer.y.toStringAsFixed(4)}'),
                  Text('${_uncalibratedMagnetometer.z.toStringAsFixed(4)}'),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}