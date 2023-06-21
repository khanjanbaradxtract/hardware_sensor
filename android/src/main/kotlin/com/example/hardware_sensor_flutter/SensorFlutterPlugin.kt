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
  private val GYROSCOPE_CHANNEL_NAME = "hardware_sensors/gyroscope"
  private val GRAVITY_CHANNEL_NAME = "hardware_sensors/gravity"


  private var sensorManager: SensorManager? = null
  private var methodChannel: MethodChannel? = null
  private var accelerometerChannel: EventChannel? = null
  private var gyroscopeChannel: EventChannel? = null
  private var gravityChannel: EventChannel? = null



  private var accelerationStreamHandler: StreamHandlerImpl? = null
  private var gyroScopeStreamHandler: StreamHandlerImpl? = null
  private var gravityStreamHandler: StreamHandlerImpl? = null


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

    gravityChannel = EventChannel(messenger, GRAVITY_CHANNEL_NAME)
    gravityStreamHandler = StreamHandlerImpl(sensorManager!!, Sensor.TYPE_GRAVITY)
    gravityChannel!!.setStreamHandler(gravityStreamHandler!!)

  }

  private fun teardownEventChannels() {
    methodChannel!!.setMethodCallHandler(null)
    accelerometerChannel!!.setStreamHandler(null)
    gyroscopeChannel!!.setStreamHandler(null)
    gravityChannel!!.setStreamHandler(null)

  }

  private fun setSensorUpdateInterval(sensorType: Int, interval: Int) {
    when (sensorType) {
      Sensor.TYPE_ACCELEROMETER -> accelerationStreamHandler!!.setUpdateInterval(interval)
      Sensor.TYPE_GRAVITY -> gravityStreamHandler!!.setUpdateInterval(interval)
      Sensor.TYPE_GYROSCOPE -> gyroScopeStreamHandler!!.setUpdateInterval(interval)
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

//class RotationVectorStreamHandler(private val sensorManager: SensorManager, sensorType: Int, private var interval: Int = SensorManager.SENSOR_DELAY_NORMAL) :
//  EventChannel.StreamHandler, SensorEventListener {
//  private val sensor = sensorManager.getDefaultSensor(sensorType)
//  private var eventSink: EventChannel.EventSink? = null
//
//  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
//    if (sensor != null) {
//      eventSink = events
//      sensorManager.registerListener(this, sensor, interval)
//    }
//  }
//
//  override fun onCancel(arguments: Any?) {
//    sensorManager.unregisterListener(this)
//    eventSink = null
//  }
//
//  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//
//  }
//
//  override fun onSensorChanged(event: SensorEvent?) {
//    var matrix = FloatArray(9)
//    SensorManager.getRotationMatrixFromVector(matrix, event!!.values)
//    if (matrix[7] > 1.0f) matrix[7] = 1.0f
//    if (matrix[7] < -1.0f) matrix[7] = -1.0f
//    var orientation = FloatArray(3)
//    SensorManager.getOrientation(matrix, orientation)
//    val sensorValues = listOf(-orientation[0], -orientation[1], orientation[2])
//    eventSink?.success(sensorValues)
//  }
//
//  fun setUpdateInterval(interval: Int) {
//    this.interval = interval
//    if (eventSink != null) {
//      sensorManager.unregisterListener(this)
//      sensorManager.registerListener(this, sensor, interval)
//    }
//  }
//}
//
//class ScreenOrientationStreamHandler(private val context: Context, private val sensorManager: SensorManager, sensorType: Int, private var interval: Int = SensorManager.SENSOR_DELAY_NORMAL) :
//  EventChannel.StreamHandler, SensorEventListener {
//  private val sensor = sensorManager.getDefaultSensor(sensorType)
//  private var eventSink: EventChannel.EventSink? = null
//  private var lastRotation: Double = -1.0
//
//  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
//    eventSink = events
//    sensorManager.registerListener(this, sensor, interval)
//  }
//
//  override fun onCancel(arguments: Any?) {
//    sensorManager.unregisterListener(this)
//    eventSink = null
//  }
//
//  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//
//  }
//
//  override fun onSensorChanged(event: SensorEvent?) {
//    val rotation = getScreenOrientation()
//    if (rotation != lastRotation) {
//      eventSink?.success(rotation)
//      lastRotation = rotation
//    }
//  }
//
//  fun setUpdateInterval(interval: Int) {
//    this.interval = interval
//    if (eventSink != null) {
//      sensorManager.unregisterListener(this)
//      sensorManager.registerListener(this, sensor, interval)
//    }
//  }
//
//  private fun getScreenOrientation(): Double {
//    return when ((context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation) {
//      Surface.ROTATION_0 -> 0.0
//      Surface.ROTATION_90 -> 90.0
//      Surface.ROTATION_180 -> 180.0
//      Surface.ROTATION_270 -> -90.0
//      else -> 0.0
//    }
//  }
