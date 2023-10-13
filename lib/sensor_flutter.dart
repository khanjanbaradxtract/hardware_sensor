import 'dart:async';
import 'dart:math' as math;
import 'package:flutter/services.dart';

final HardwareSensors hardwareSensors = HardwareSensors();

const MethodChannel _methodChannel = MethodChannel('hardware_sensors/method');
const EventChannel _accelerometerEventChannel =
    EventChannel('hardware_sensors/accelerometer');
const EventChannel _gyroscopeEventChannel =
    EventChannel('hardware_sensors/gyroscope');
const EventChannel _uncalibratedGyroscopeEventChannel =
    EventChannel('hardware_sensors/uncalibrated_gyroscope');
const EventChannel _gravityEventChannel =
    EventChannel('hardware_sensors/gravity');
const EventChannel _magnetometerEventChannel =
    EventChannel('motion_sensors/magnetometer');
const EventChannel _uncalibratedMagnetometerEventChannel =
    EventChannel('motion_sensors/uncalibrated_magnetometer');
const EventChannel _pressureEventChannel =
    EventChannel('motion_sensors/pressure');
const EventChannel _gameRotationEventChannel =
    EventChannel('motion_sensors/game_rotation');
const EventChannel _rotationEventChannel =
    EventChannel('motion_sensors/rotation');
const EventChannel _linearAccelerationEventChannel =
    EventChannel('hardware_sensors/linear_acceleration');
const EventChannel _locationEventChannel =
    EventChannel('hardware_sensors/location');

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

class LinearAccelerationEvent {
  /// Contructs an instance with the given [x], [y], and [z] values.
  LinearAccelerationEvent(this.x, this.y, this.z);
  LinearAccelerationEvent.fromList(List<double> list)
      : x = list[0],
        y = list[1],
        z = list[2];

  final double x;
  final double y;
  final double z;

  @override
  String toString() => '[LinearAccelerationEvent (x: $x, y: $y, z: $z)]';
}

class MagnetometerEvent {
  MagnetometerEvent(this.x, this.y, this.z);
  MagnetometerEvent.fromList(List<double> list)
      : x = list[0],
        y = list[1],
        z = list[2];

  final double x;
  final double y;
  final double z;
  @override
  String toString() => '[Magnetometer (x: $x, y: $y, z: $z)]';
}

class UncalibratedMagnetometerEvent {
  UncalibratedMagnetometerEvent(this.x, this.y, this.z);
  UncalibratedMagnetometerEvent.fromList(List<double> list)
      : x = list[0],
        y = list[1],
        z = list[2];

  final double x;
  final double y;
  final double z;
  @override
  String toString() => '[UncalibratedMagnetometerEvent (x: $x, y: $y, z: $z)]';
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

class UncalibratedGyroscopeEvent {
  /// Contructs an instance with the given [x], [y], and [z] values.
  UncalibratedGyroscopeEvent(this.x, this.y, this.z);
  UncalibratedGyroscopeEvent.fromList(List<double> list)
      : x = list[0],
        y = list[1],
        z = list[2];

  final double x;

  final double y;

  final double z;

  @override
  String toString() => '[UncalibratedGyroscopeEvent (x: $x, y: $y, z: $z)]';
}

class GameRotationEvent {
  GameRotationEvent(this.yaw, this.pitch, this.roll);
  GameRotationEvent.fromList(List<double> list)
      : yaw = list[0],
        pitch = list[1],
        roll = list[2];

  /// The yaw of the device in radians.
  final double yaw;

  /// The pitch of the device in radians.
  final double pitch;

  /// The roll of the device in radians.
  final double roll;
  @override
  String toString() =>
      '[GameRotationEvent (yaw: $yaw, pitch: $pitch, roll: $roll)]';
}

class RotationEvent {
  RotationEvent(this.yaw, this.pitch, this.roll);
  RotationEvent.fromList(List<double> list)
      : yaw = list[0],
        pitch = list[1],
        roll = list[2];

  /// The yaw of the device in radians.
  final double yaw;

  /// The pitch of the device in radians.
  final double pitch;

  /// The roll of the device in radians.
  final double roll;
  @override
  String toString() =>
      '[RotationEvent (yaw: $yaw, pitch: $pitch, roll: $roll)]';
}

class LocationEvent {
  LocationEvent(this.lat, this.long, this.elev, this.acc);

  LocationEvent.fromList(List<double> list)
      : lat = list[0],
        long = list[1],
        elev = list[2],
        acc = list[3];

  /// The lat of the location in degrees.
  final double lat;

  /// The long of the location in degrees.
  final double long;

  /// The elev of the location in meters.
  final double elev;

  /// The horz accuracy of the location in meters.
  final double acc;

  @override
  String toString() =>
      '[LocationEvent (lat: $lat, long: $long, elev: $elev, acc: $acc)]';
}

class PressureEvent {
  PressureEvent(this.x);
  PressureEvent.fromList(List<double> list) : x = list[0];

  final double x;

  @override
  String toString() => '[PressureEvent (x: $x)]';
}

class HardwareSensors {
  Stream<AccelerometerEvent>? _accelerometerEvents;
  Stream<GyroscopeEvent>? _gyroscopeEvents;
  Stream<UncalibratedGyroscopeEvent>? _uncalibratedGyroscopeEvents;
  Stream<GravityEvent>? _gravityEvents;
  Stream<MagnetometerEvent>? _magnetometerEvents;
  Stream<UncalibratedMagnetometerEvent>? _uncalibratedMagnetometerEvents;
  Stream<PressureEvent>? _pressureEvents;
  Stream<GameRotationEvent>? _gameRotationEvents;
  Stream<RotationEvent>? _rotationEvents;
  GameRotationEvent? _initialGameRotation;
  Stream<LinearAccelerationEvent>? _linearAccelerationEvents;
  Stream<LocationEvent>? _locationEvents;

  static const int TYPE_ACCELEROMETER = 1;
  static const int TYPE_MAGNETIC_FIELD = 2;
  static const int TYPE_MAGNETIC_FIELD_UNCALIBRATED = 3;
  static const int TYPE_GRAVITY = 9;
  static const int TYPE_GYROSCOPE = 4;
  static const int TYPE_GYROSCOPE_UNCALIBRATED = 5;
  static const int TYPE_PRESSURE = 6;
  static const int TYPE_GAME_ROTATION_VECTOR = 15;
  static const int TYPE_ROTATION_VECTOR = 11;
  static const int TYPE_LINEAR_ACCELERATION = 10;
  static const int TYPE_LOCATION = 11;

  /// Determines whether sensor is available.
  Future<bool> isSensorAvailable(int sensorType) async {
    final available =
        await _methodChannel.invokeMethod('isSensorAvailable', sensorType);
    return available;
  }

  /// Determines whether accelerometer is available.
  Future<bool> isAccelerometerAvailable() =>
      isSensorAvailable(TYPE_ACCELEROMETER);

  /// Determines whether Gravity is available.
  Future<bool> isGravityAvailable() => isSensorAvailable(TYPE_GRAVITY);

  /// Determines whether gyroscope is available.
  Future<bool> isGyroscopeAvailable() => isSensorAvailable(TYPE_GYROSCOPE);

  /// Determines whether Uncalibrated gyroscope is available.
  Future<bool> isUncalibratedGyroscopeAvailable() =>
      isSensorAvailable(TYPE_GYROSCOPE_UNCALIBRATED);

  /// Determines whether magnetometer is available.
  Future<bool> isMagnetometerAvailable() =>
      isSensorAvailable(TYPE_MAGNETIC_FIELD);

  /// Determines whether Uncalibrated magnetometer is available.
  Future<bool> isUncalibratedMagnetometerAvailable() =>
      isSensorAvailable(TYPE_MAGNETIC_FIELD_UNCALIBRATED);

  /// Determines whether Pressure is available.
  Future<bool> isPressureAvailable() => isSensorAvailable(TYPE_PRESSURE);

  /// Determines whether GameRotation is available.
  Future<bool> isGameRotationAvailable() =>
      isSensorAvailable(TYPE_GAME_ROTATION_VECTOR);

  /// Determines whether Rotation is available.
  Future<bool> isRotationAvailable() => isSensorAvailable(TYPE_ROTATION_VECTOR);

  /// Determines whether LinearAcceleration is available.
  Future<bool> isLinearAccelerationAvailable() =>
      isSensorAvailable(TYPE_LINEAR_ACCELERATION);

  /// Determines whether LinearAcceleration is available.
  Future<bool> isLocationAvailable() => isSensorAvailable(TYPE_LOCATION);

  /// Change the update interval of sensor. The units are in microseconds.
  Future setSensorUpdateInterval(int sensorType, int interval) async {
    await _methodChannel.invokeMethod('setSensorUpdateInterval',
        {"sensorType": sensorType, "interval": interval});
  }

  /// The update interval of accelerometer. The units are in microseconds.
  set accelerometerUpdateInterval(int interval) =>
      setSensorUpdateInterval(TYPE_ACCELEROMETER, interval);

  /// The update interval of gravity. The units are in microseconds.
  set gravityUpdateInterval(int interval) =>
      setSensorUpdateInterval(TYPE_GRAVITY, interval);

  /// The update interval of Gyroscope. The units are in microseconds.
  set gyroscopeUpdateInterval(int interval) =>
      setSensorUpdateInterval(TYPE_GYROSCOPE, interval);

  /// The update interval of uncalibrated Gyroscope. The units are in microseconds.
  set uncalibratedGyroscopeUpdateInterval(int interval) =>
      setSensorUpdateInterval(TYPE_GYROSCOPE_UNCALIBRATED, interval);

  /// The update interval of magnetometer. The units are in microseconds.
  set magnetometerUpdateInterval(int interval) =>
      setSensorUpdateInterval(TYPE_MAGNETIC_FIELD, interval);

  /// The update interval of uncalibrated magnetometer. The units are in microseconds.
  set uncalibratedMagnetometerUpdateInterval(int interval) =>
      setSensorUpdateInterval(TYPE_MAGNETIC_FIELD_UNCALIBRATED, interval);

  /// The update interval of pressure. The units are in microseconds.
  set pressureUpdateInterval(int interval) =>
      setSensorUpdateInterval(TYPE_PRESSURE, interval);

  /// The update interval of gameRotation. The units are in microseconds.
  set gameRotationUpdateInterval(int interval) =>
      setSensorUpdateInterval(TYPE_GAME_ROTATION_VECTOR, interval);

  /// The update interval of rotation. The units are in microseconds.
  set rotationUpdateInterval(int interval) =>
      setSensorUpdateInterval(TYPE_ROTATION_VECTOR, interval);

  /// The update interval of linearAcceleration. The units are in microseconds.
  set linearAccelerationUpdateInterval(int interval) =>
      setSensorUpdateInterval(TYPE_LINEAR_ACCELERATION, interval);

  // /// The update interval of linearAcceleration. The units are in microseconds.
  // set locationUpdateInterval(int interval) => {
  //   Future locationUpdateInterval(int interval) async {
  //       await _methodChannel.invokeMethod('setLocationUpdateInterval',
  //       {"interval": interval});
  //   }
  // }

  /// A broadcast stream of events from the device accelerometer.
  Stream<AccelerometerEvent> get accelerometer {
    _accelerometerEvents ??= _accelerometerEventChannel
          .receiveBroadcastStream()
          .map((dynamic event) =>
              AccelerometerEvent.fromList(event.cast<double>()));
    return _accelerometerEvents!;
  }

  /// A broadcast stream of events from the device linearAcceleration.
  Stream<LinearAccelerationEvent> get linearAcceleration {
    _linearAccelerationEvents ??= _linearAccelerationEventChannel
          .receiveBroadcastStream()
          .map((dynamic event) =>
              LinearAccelerationEvent.fromList(event.cast<double>()));
    return _linearAccelerationEvents!;
  }

  /// A broadcast stream of events from the device gyroscope.
  Stream<GyroscopeEvent> get gyroscope {
    _gyroscopeEvents ??= _gyroscopeEventChannel.receiveBroadcastStream().map(
          (dynamic event) => GyroscopeEvent.fromList(event.cast<double>()));
    return _gyroscopeEvents!;
  }

  /// A broadcast stream of events from the device uncalibratedGyroscope.
  Stream<UncalibratedGyroscopeEvent> get uncalibratedGyroscope {
    _uncalibratedGyroscopeEvents ??= _uncalibratedGyroscopeEventChannel
          .receiveBroadcastStream()
          .map((dynamic event) =>
              UncalibratedGyroscopeEvent.fromList(event.cast<double>()));
    return _uncalibratedGyroscopeEvents!;
  }

  /// A broadcast stream of events from the device gravity.
  Stream<GravityEvent> get gravity {
    _gravityEvents ??= _gravityEventChannel
          .receiveBroadcastStream()
          .map((dynamic event) => GravityEvent.fromList(event.cast<double>()));
    return _gravityEvents!;
  }

  /// A broadcast stream of events from the device magnetometer.
  Stream<MagnetometerEvent> get magnetometer {
    _magnetometerEvents ??= _magnetometerEventChannel
          .receiveBroadcastStream()
          .map((dynamic event) =>
              MagnetometerEvent.fromList(event.cast<double>()));
    return _magnetometerEvents!;
  }

  /// A broadcast stream of events from the device uncalibratedMagnetometer.
  Stream<UncalibratedMagnetometerEvent> get uncalibratedMagnetometer {
    _uncalibratedMagnetometerEvents ??= _uncalibratedMagnetometerEventChannel
          .receiveBroadcastStream()
          .map((dynamic event) =>
              UncalibratedMagnetometerEvent.fromList(event.cast<double>()));
    return _uncalibratedMagnetometerEvents!;
  }

  /// A broadcast stream of events from the device pressure.
  Stream<PressureEvent> get pressure {
    _pressureEvents ??= _pressureEventChannel
          .receiveBroadcastStream()
          .map((dynamic event) => PressureEvent.fromList(event.cast<double>()));
    return _pressureEvents!;
  }

  Stream<GameRotationEvent> get gameRotaion {
    _gameRotationEvents ??= _gameRotationEventChannel
          .receiveBroadcastStream()
          .map((dynamic event) {
        var gameRotation = GameRotationEvent.fromList(event.cast<double>());
        _initialGameRotation ??= gameRotation;
        // Change the initial yaw of the Game Rotation to zero
        var yaw = (gameRotation.yaw + math.pi - _initialGameRotation!.yaw) %
                (math.pi * 2) -
            math.pi;
        return GameRotationEvent(yaw, gameRotation.pitch, gameRotation.roll);
      });
    return _gameRotationEvents!;
  }

  /// The current rotation of the device.
  Stream<RotationEvent> get rotation {
    _rotationEvents ??= _rotationEventChannel
          .receiveBroadcastStream()
          .map((dynamic event) => RotationEvent.fromList(event.cast<double>()));
    return _rotationEvents!;
  }

  /// The GPS location of the device.
  Stream<LocationEvent> get location {
    _locationEvents ??= _locationEventChannel
          .receiveBroadcastStream()
          .map((dynamic event) => LocationEvent.fromList(event.cast<double>()));
    return _locationEvents!;
  }
}
