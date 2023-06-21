import 'dart:async';
import 'dart:math' as math;
import 'package:flutter/services.dart';
import 'package:vector_math/vector_math_64.dart';

final HardwareSensors hardwareSensors = HardwareSensors();
const MethodChannel _methodChannel = MethodChannel('hardware_sensors/method');
const EventChannel _accelerometerEventChannel = EventChannel('hardware_sensors/accelerometer');
const EventChannel _gyroscopeEventChannel = EventChannel('hardware_sensors/gyroscope');
const EventChannel _gravityEventChannel = EventChannel('hardware_sensors/gravity');

/// Discrete reading from an accelerometer. Accelerometers measure the velocity
/// of the device. Note that these readings include the effects of gravity. Put
/// simply, you can use accelerometer readings to tell if the device is moving in
/// a particular direction.
class AccelerometerEvent {
  /// Contructs an instance with the given [x], [y], and [z] values.
  AccelerometerEvent(this.x, this.y, this.z);
  AccelerometerEvent.fromList(List<double> list)
      : x = list[0],
        y = list[1],
        z = list[2];

  /// Acceleration force along the x axis (including gravity) measured in m/s^2.
  ///
  /// When the device is held upright facing the user, positive values mean the
  /// device is moving to the right and negative mean it is moving to the left.
  final double x;

  /// Acceleration force along the y axis (including gravity) measured in m/s^2.
  ///
  /// When the device is held upright facing the user, positive values mean the
  /// device is moving towards the sky and negative mean it is moving towards
  /// the ground.
  final double y;

  /// Acceleration force along the z axis (including gravity) measured in m/s^2.
  ///
  /// This uses a right-handed coordinate system. So when the device is held
  /// upright and facing the user, positive values mean the device is moving
  /// towards the user and negative mean it is moving away from them.
  final double z;

  @override
  String toString() => '[AccelerometerEvent (x: $x, y: $y, z: $z)]';
}

class GravityEvent {
  GravityEvent(this.x, this.y, this.z);
  GravityEvent.fromList(List<double> list)
      : x = list[0],
        y = list[1],
        z = list[2];

  final double x;
  final double y;
  final double z;
  @override
  String toString() => '[Gravity (x: $x, y: $y, z: $z)]';
}

/// Discrete reading from a gyroscope. Gyroscopes measure the rate or rotation of
/// the device in 3D space.
class GyroscopeEvent {
  /// Contructs an instance with the given [x], [y], and [z] values.
  GyroscopeEvent(this.x, this.y, this.z);
  GyroscopeEvent.fromList(List<double> list)
      : x = list[0],
        y = list[1],
        z = list[2];

  /// Rate of rotation around the x axis measured in rad/s.
  ///
  /// When the device is held upright, this can also be thought of as describing
  /// "pitch". The top of the device will tilt towards or away from the
  /// user as this value changes.
  final double x;

  /// Rate of rotation around the y axis measured in rad/s.
  ///
  /// When the device is held upright, this can also be thought of as describing
  /// "yaw". The lengthwise edge of the device will rotate towards or away from
  /// the user as this value changes.
  final double y;

  /// Rate of rotation around the z axis measured in rad/s.
  ///
  /// When the device is held upright, this can also be thought of as describing
  /// "roll". When this changes the face of the device should remain facing
  /// forward, but the orientation will change from portrait to landscape and so
  /// on.
  final double z;

  @override
  String toString() => '[GyroscopeEvent (x: $x, y: $y, z: $z)]';
}


class HardwareSensors {
  Stream<AccelerometerEvent>? _accelerometerEvents;
  Stream<GyroscopeEvent>? _gyroscopeEvents;
  Stream<GravityEvent>? _gravityEvents;

  static const int TYPE_ACCELEROMETER = 1;
  static const int TYPE_GRAVITY = 9;
  static const int TYPE_GYROSCOPE = 4;

  /// Determines whether sensor is available.
  Future<bool> isSensorAvailable(int sensorType) async {
    final available = await _methodChannel.invokeMethod('isSensorAvailable', sensorType);
    return available;
  }

  /// Determines whether accelerometer is available.
  Future<bool> isAccelerometerAvailable() => isSensorAvailable(TYPE_ACCELEROMETER);

  /// Determines whether magnetometer is available.
  Future<bool> isGravityAvailable() => isSensorAvailable(TYPE_GRAVITY);

  /// Determines whether gyroscope is available.
  Future<bool> isGyroscopeAvailable() => isSensorAvailable(TYPE_GYROSCOPE);

  /// Change the update interval of sensor. The units are in microseconds.
  Future setSensorUpdateInterval(int sensorType, int interval) async {
    await _methodChannel.invokeMethod('setSensorUpdateInterval', {"sensorType": sensorType, "interval": interval});
  }

  /// The update interval of accelerometer. The units are in microseconds.
  set accelerometerUpdateInterval(int interval) => setSensorUpdateInterval(TYPE_ACCELEROMETER, interval);

  /// The update interval of magnetometer. The units are in microseconds.
  set gravityUpdateInterval(int interval) => setSensorUpdateInterval(TYPE_GRAVITY, interval);

  /// The update interval of Gyroscope. The units are in microseconds.
  set gyroscopeUpdateInterval(int interval) => setSensorUpdateInterval(TYPE_GYROSCOPE, interval);


  /// A broadcast stream of events from the device accelerometer.
  Stream<AccelerometerEvent> get accelerometer {
    if (_accelerometerEvents == null) {
      _accelerometerEvents = _accelerometerEventChannel.receiveBroadcastStream().map((dynamic event) => AccelerometerEvent.fromList(event.cast<double>()));
    }
    return _accelerometerEvents!;
  }

  /// A broadcast stream of events from the device gyroscope.
  Stream<GyroscopeEvent> get gyroscope {
    if (_gyroscopeEvents == null) {
      _gyroscopeEvents = _gyroscopeEventChannel.receiveBroadcastStream().map((dynamic event) => GyroscopeEvent.fromList(event.cast<double>()));
    }
    return _gyroscopeEvents!;
  }


  /// A broadcast stream of events from the device magnetometer.
  Stream<GravityEvent> get gravity {
    if (_gravityEvents == null) {
      _gravityEvents = _gravityEventChannel.receiveBroadcastStream().map((dynamic event) => GravityEvent.fromList(event.cast<double>()));
    }
    return _gravityEvents!;
  }

}