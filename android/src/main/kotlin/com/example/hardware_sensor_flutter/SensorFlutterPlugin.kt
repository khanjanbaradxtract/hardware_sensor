package com.example.hardware_sensor_flutter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry.Registrar

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





  private var sensorManager: SensorManager? = null
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







  private var accelerationStreamHandler: StreamHandlerImpl? = null
  private var linearAccelerationStreamHandler: StreamHandlerImpl? = null
  private var gyroScopeStreamHandler: StreamHandlerImpl? = null
  private var uncalibratedGyroscopeStreamHandler: StreamHandlerImpl? = null
  private var gravityStreamHandler: StreamHandlerImpl? = null
  private var magnetometerStreamHandler: StreamHandlerImpl? = null
  private var uncalibratedMagnetometerStreamHandler: StreamHandlerImpl? = null
  private var pressureStreamHandler: PressureStreamHandlerImpl? = null
  private var gameRotationStreamHandler: RotationVectorStreamHandler? = null
  private var rotationStreamHandler: RotationVectorStreamHandler? = null







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

    methodChannel = MethodChannel(messenger, METHOD_CHANNEL_NAME)
    methodChannel!!.setMethodCallHandler(this)

    accelerometerChannel = EventChannel(messenger, ACCELEROMETER_CHANNEL_NAME)
    accelerationStreamHandler = StreamHandlerImpl(sensorManager!!, Sensor.TYPE_ACCELEROMETER)
    accelerometerChannel!!.setStreamHandler(accelerationStreamHandler!!)

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
    gameRotationStreamHandler = RotationVectorStreamHandler(sensorManager!!, Sensor.TYPE_GAME_ROTATION_VECTOR)
    gameRotationChannel!!.setStreamHandler(gameRotationStreamHandler!!)

    rotationChannel = EventChannel(messenger, ROTATION_CHANNEL_NAME)
    rotationStreamHandler = RotationVectorStreamHandler(sensorManager!!, Sensor.TYPE_ROTATION_VECTOR)
    rotationChannel!!.setStreamHandler(rotationStreamHandler!!)

    linearAccelerationChannel = EventChannel(messenger, LINEAR_ACCELERATION_CHANNEL_NAME)
    linearAccelerationStreamHandler = StreamHandlerImpl(sensorManager!!, Sensor.TYPE_LINEAR_ACCELERATION)
    linearAccelerationChannel!!.setStreamHandler(linearAccelerationStreamHandler!!)

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




  }

  private fun setSensorUpdateInterval(sensorType: Int, interval: Int) {

//    This Snippet is to know how many hardware sensors are configured for Particular Device
//    val sensor = listOf(sensorManager!!.getSensorList(Sensor.TYPE_ALL))
//    println("sensors ${sensor}")

    when (sensorType) {
      Sensor.TYPE_ACCELEROMETER -> accelerationStreamHandler!!.setUpdateInterval(interval)
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

class RotationVectorStreamHandler(private val sensorManager: SensorManager, sensorType: Int, private var interval: Int = SensorManager.SENSOR_DELAY_NORMAL) :
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
