package com.example.hardware_sensor_flutter

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager
import androidx.annotation.NonNull;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry.Registrar

private val LOCATION_UPDATE_INTERVAL = 1000L

/** SensorFlutterPlugin */
public class SensorFlutterPlugin : FlutterPlugin, MethodChannel.MethodCallHandler {
  private val METHOD_CHANNEL_NAME = "hardware_sensors/method"
  private val ACCELEROMETER_CHANNEL_NAME = "hardware_sensors/accelerometer"
  private val LINEAR_ACCELERATION_CHANNEL_NAME = "hardware_sensors/linear_acceleration"
  private val GYROSCOPE_CHANNEL_NAME = "hardware_sensors/gyroscope"
  private val UNCALIBRATED_GYROSCOPE_CHANNEL_NAME = "hardware_sensors/uncalibrated_gyroscope"
  private val GRAVITY_CHANNEL_NAME = "hardware_sensors/gravity"
  private val MAGNETOMETER_CHANNEL_NAME = "motion_sensors/magnetometer"
  private val UNCALIBRATED_MAGNETOMETER_CHANNEL_NAME = "motion_sensors/uncalibrated_magnetometer"
  private val PRESSURE_CHANNEL_NAME = "motion_sensors/pressure"
  private val GAME_ROTATION_CHANNEL_NAME = "motion_sensors/game_rotation"
  private val ROTATION_CHANNEL_NAME = "motion_sensors/rotation"
  private val LOCATION_CHANNEL_NAME = "hardware_sensors/location"

  private var sensorManager: SensorManager? = null
  private var locationManager: LocationManager? = null

  private var methodChannel: MethodChannel? = null
  private var accelerometerChannel: EventChannel? = null
  private var linearAccelerationChannel: EventChannel? = null
  private var gyroscopeChannel: EventChannel? = null
  private var uncalibratedGyroscopeChannel: EventChannel? = null
  private var gravityChannel: EventChannel? = null
  private var magnetometerChannel: EventChannel? = null
  private var uncalibratedMagnetometerChannel: EventChannel? = null
  private var pressureChannel: EventChannel? = null
  private var gameRotationChannel: EventChannel? = null
  private var rotationChannel: EventChannel? = null
  private var locationChannel: EventChannel? = null

  private var accelerometerStreamHandler: StreamHandlerImpl? = null
  private var linearAccelerationStreamHandler: StreamHandlerImpl? = null
  private var gyroScopeStreamHandler: StreamHandlerImpl? = null
  private var uncalibratedGyroscopeStreamHandler: StreamHandlerImpl? = null
  private var gravityStreamHandler: StreamHandlerImpl? = null
  private var magnetometerStreamHandler: StreamHandlerImpl? = null
  private var uncalibratedMagnetometerStreamHandler: StreamHandlerImpl? = null
  private var pressureStreamHandler: PressureStreamHandlerImpl? = null
  private var gameRotationStreamHandler: RotationVectorStreamHandlerImpl? = null
  private var rotationStreamHandler: RotationVectorStreamHandlerImpl? = null
  private var locationStreamHandler: LocationStreamHandlerImpl? = null

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val plugin = SensorFlutterPlugin()
      plugin.setupEventChannels(registrar.context(), registrar.messenger())
    }
  }

  override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    val context = binding.applicationContext
    setupEventChannels(context, binding.binaryMessenger)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    teardownEventChannels()
  }

  override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    when (call.method) {
      "isSensorAvailable" -> result.success(sensorManager!!.getSensorList(call.arguments as Int).isNotEmpty())
      "setSensorUpdateInterval" -> setSensorUpdateInterval(call.argument<Int>("sensorType")!!, call.argument<Int>("interval")!!)
      else -> result.notImplemented()
    }
  }

  private fun setupEventChannels(context: Context, messenger: BinaryMessenger) {
    sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    methodChannel = MethodChannel(messenger, METHOD_CHANNEL_NAME)
    methodChannel!!.setMethodCallHandler(this)

    accelerometerChannel = EventChannel(messenger, ACCELEROMETER_CHANNEL_NAME)
    accelerometerStreamHandler = StreamHandlerImpl(sensorManager!!, Sensor.TYPE_ACCELEROMETER)
    accelerometerChannel!!.setStreamHandler(accelerometerStreamHandler!!)

    gyroscopeChannel = EventChannel(messenger, GYROSCOPE_CHANNEL_NAME)
    gyroScopeStreamHandler = StreamHandlerImpl(sensorManager!!, Sensor.TYPE_GYROSCOPE)
    gyroscopeChannel!!.setStreamHandler(gyroScopeStreamHandler!!)

    uncalibratedGyroscopeChannel = EventChannel(messenger, UNCALIBRATED_GYROSCOPE_CHANNEL_NAME)
    uncalibratedGyroscopeStreamHandler = StreamHandlerImpl(sensorManager!!, Sensor.TYPE_GYROSCOPE_UNCALIBRATED)
    uncalibratedGyroscopeChannel!!.setStreamHandler(uncalibratedGyroscopeStreamHandler!!)

    gravityChannel = EventChannel(messenger, GRAVITY_CHANNEL_NAME)
    gravityStreamHandler = StreamHandlerImpl(sensorManager!!, Sensor.TYPE_GRAVITY)
    gravityChannel!!.setStreamHandler(gravityStreamHandler!!)

    magnetometerChannel = EventChannel(messenger, MAGNETOMETER_CHANNEL_NAME)
    magnetometerStreamHandler = StreamHandlerImpl(sensorManager!!, Sensor.TYPE_MAGNETIC_FIELD)
    magnetometerChannel!!.setStreamHandler(magnetometerStreamHandler!!)

    uncalibratedMagnetometerChannel = EventChannel(messenger, UNCALIBRATED_MAGNETOMETER_CHANNEL_NAME)
    uncalibratedMagnetometerStreamHandler = StreamHandlerImpl(sensorManager!!, Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED)
    uncalibratedMagnetometerChannel!!.setStreamHandler(uncalibratedMagnetometerStreamHandler!!)

    pressureChannel = EventChannel(messenger, PRESSURE_CHANNEL_NAME)
    pressureStreamHandler = PressureStreamHandlerImpl(sensorManager!!, Sensor.TYPE_PRESSURE)
    pressureChannel!!.setStreamHandler(pressureStreamHandler!!)

    gameRotationChannel = EventChannel(messenger, GAME_ROTATION_CHANNEL_NAME)
    gameRotationStreamHandler = RotationVectorStreamHandlerImpl(sensorManager!!, Sensor.TYPE_GAME_ROTATION_VECTOR)
    gameRotationChannel!!.setStreamHandler(gameRotationStreamHandler!!)

    rotationChannel = EventChannel(messenger, ROTATION_CHANNEL_NAME)
    rotationStreamHandler = RotationVectorStreamHandlerImpl(sensorManager!!, Sensor.TYPE_ROTATION_VECTOR)
    rotationChannel!!.setStreamHandler(rotationStreamHandler!!)

    linearAccelerationChannel = EventChannel(messenger, LINEAR_ACCELERATION_CHANNEL_NAME)
    linearAccelerationStreamHandler = StreamHandlerImpl(sensorManager!!, Sensor.TYPE_LINEAR_ACCELERATION)
    linearAccelerationChannel!!.setStreamHandler(linearAccelerationStreamHandler!!)

    locationChannel = EventChannel(messenger, LOCATION_CHANNEL_NAME)
    locationStreamHandler = LocationStreamHandlerImpl(locationManager!!)
    locationChannel!!.setStreamHandler(locationStreamHandler!!)
  }

  private fun teardownEventChannels() {
    methodChannel!!.setMethodCallHandler(null)
    accelerometerChannel!!.setStreamHandler(null)
    linearAccelerationChannel!!.setStreamHandler(null)
    gyroscopeChannel!!.setStreamHandler(null)
    uncalibratedGyroscopeChannel!!.setStreamHandler(null)
    gravityChannel!!.setStreamHandler(null)
    magnetometerChannel!!.setStreamHandler(null)
    uncalibratedMagnetometerChannel!!.setStreamHandler(null)
    pressureChannel!!.setStreamHandler(null)
    gameRotationChannel!!.setStreamHandler(null)
    rotationChannel!!.setStreamHandler(null)
    locationChannel!!.setStreamHandler(null)
  }

  private fun setSensorUpdateInterval(sensorType: Int, interval: Int) {

//    This Snippet is to know how many hardware sensors are configured for Particular Device
//    val sensor = listOf(sensorManager!!.getSensorList(Sensor.TYPE_ALL))
//    println("sensors ${sensor}")

    when (sensorType) {
      Sensor.TYPE_ACCELEROMETER -> accelerometerStreamHandler!!.setUpdateInterval(interval)
      Sensor.TYPE_GRAVITY -> gravityStreamHandler!!.setUpdateInterval(interval)
      Sensor.TYPE_GYROSCOPE -> gyroScopeStreamHandler!!.setUpdateInterval(interval)
      Sensor.TYPE_MAGNETIC_FIELD -> magnetometerStreamHandler!!.setUpdateInterval(interval)
      Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> uncalibratedMagnetometerStreamHandler!!.setUpdateInterval(interval)
      Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> uncalibratedGyroscopeStreamHandler!!.setUpdateInterval(interval)
      Sensor.TYPE_PRESSURE -> pressureStreamHandler!!.setUpdateInterval(interval)
      Sensor.TYPE_GAME_ROTATION_VECTOR -> gameRotationStreamHandler!!.setUpdateInterval(interval)
      Sensor.TYPE_ROTATION_VECTOR -> rotationStreamHandler!!.setUpdateInterval(interval)
      Sensor.TYPE_LINEAR_ACCELERATION -> linearAccelerationStreamHandler!!.setUpdateInterval(interval)
    }
  }
    private fun setLocationUpdateInterval(sensorType: Int, interval: Long) {
      locationStreamHandler!!.setUpdateInterval(interval)
    }

}


class StreamHandlerImpl(private val sensorManager: SensorManager, sensorType: Int, private var interval: Int = SensorManager.SENSOR_DELAY_NORMAL) :
  EventChannel.StreamHandler, SensorEventListener {
  private val sensor = sensorManager.getDefaultSensor(sensorType)
  private var eventSink: EventChannel.EventSink? = null


  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    if (sensor != null) {
      eventSink = events
      sensorManager.registerListener(this, sensor, interval)
    }
  }

  override fun onCancel(arguments: Any?) {
    sensorManager.unregisterListener(this)
    eventSink = null
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

  }

  override fun onSensorChanged(event: SensorEvent?) {
    val sensorValues = listOf(event!!.values[0], event.values[1], event.values[2])
    eventSink?.success(sensorValues)

  }

  fun setUpdateInterval(interval: Int) {
    this.interval = interval
    if (eventSink != null) {
      sensorManager.unregisterListener(this)
      sensorManager.registerListener(this, sensor, interval)
    }
  }
}

class PressureStreamHandlerImpl(private val sensorManager: SensorManager, sensorType: Int, private var interval: Int = SensorManager.SENSOR_DELAY_NORMAL) :
  EventChannel.StreamHandler, SensorEventListener {
  private val sensor = sensorManager.getDefaultSensor(sensorType)
  private var eventSink: EventChannel.EventSink? = null

  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    if (sensor != null) {
      eventSink = events
      sensorManager.registerListener(this, sensor, interval)
    }
  }

  override fun onCancel(arguments: Any?) {
    sensorManager.unregisterListener(this)
    eventSink = null
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

  }

  override fun onSensorChanged(event: SensorEvent?) {
    val sensorValues = listOf(event!!.values[0])
    eventSink?.success(sensorValues)

  }

  fun setUpdateInterval(interval: Int) {
    this.interval = interval
    if (eventSink != null) {
      sensorManager.unregisterListener(this)
      sensorManager.registerListener(this, sensor, interval)
    }
  }
}

class RotationVectorStreamHandlerImpl(private val sensorManager: SensorManager, sensorType: Int, private var interval: Int = SensorManager.SENSOR_DELAY_NORMAL) :
  EventChannel.StreamHandler, SensorEventListener {
  private val sensor = sensorManager.getDefaultSensor(sensorType)
  private var eventSink: EventChannel.EventSink? = null

  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    if (sensor != null) {
      eventSink = events
      sensorManager.registerListener(this, sensor, interval)
    }
  }

  override fun onCancel(arguments: Any?) {
    sensorManager.unregisterListener(this)
    eventSink = null
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
  }

  override fun onSensorChanged(event: SensorEvent?) {
    var matrix = FloatArray(9)
    SensorManager.getRotationMatrixFromVector(matrix, event!!.values)
    if (matrix[7] > 1.0f) matrix[7] = 1.0f
    if (matrix[7] < -1.0f) matrix[7] = -1.0f
    var orientation = FloatArray(3)
    SensorManager.getOrientation(matrix, orientation)
    val sensorValues = listOf(-orientation[0], -orientation[1], orientation[2])
    eventSink?.success(sensorValues)
  }

  fun setUpdateInterval(interval: Int) {
    this.interval = interval
    if (eventSink != null) {
      sensorManager.unregisterListener(this)
      sensorManager.registerListener(this, sensor, interval)
    }
  }
}

class LocationStreamHandlerImpl(private val locationManager: LocationManager, private var interval: Long = LOCATION_UPDATE_INTERVAL) :
  EventChannel.StreamHandler {
  private var eventSink: EventChannel.EventSink? = null
  inner class MylocationListener: LocationListener {
    constructor():super(){
    }

    override fun onLocationChanged(location: Location) {
      // bundle desired location values and put them into the stream/queue for consumption by parent app
      val locationValues = listOf(location!!.latitude, location!!.longitude, location!!.altitude, location!!.accuracy)
      // Log.d("huhf_app", "have location values")
      eventSink?.success(locationValues)
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

    override fun onProviderEnabled(p0: String) {}

    override fun onProviderDisabled(p0: String) {}
  }

  private var locationListener: LocationListener = this.MylocationListener()

  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    // Log.d("huhf_app", "in location onListen")
    if (locationManager != null) {
      eventSink = events
      try {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval, 0.1f, locationListener)
      } catch(e: SecurityException) {
        Log.d("huhf_app", "Security Exception, no permission to use location service")
      }
    }
  }

  override fun onCancel(arguments: Any?) {
    Log.d("huhf_app", "in location onCancel")
    locationManager.removeUpdates(locationListener)
    eventSink = null
  }
  
  fun setUpdateInterval(interval: Long) {
    this.interval = interval
    if (eventSink != null && locationManager != null) {
      locationManager.removeUpdates(locationListener)
      try {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval, 0.1f, locationListener)
      } catch(ex: SecurityException) {
        Log.d("huhf_app", "Security Exception, no location service available")
      }
    }
  }
}

