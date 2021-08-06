package com.example.scrollstopper

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Display
import android.view.Surface
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_wake_up.*

class WakeUpActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val VALUE_DRIFT = 0.05f

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private var mDisplay: Display? = null
    private var mAccelerometerData = FloatArray(3)
    private var mMagnetometerData = FloatArray(3)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wake_up)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Get the display from the window manager (for rotation).
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        mDisplay = wm.defaultDisplay
        updateOrientationAngles()

        button.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    override fun onResume() {
        super.onResume()
        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                this,
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onPause() {
        super.onPause()
        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this)
    }

    // Get readings from accelerometer and magnetometer. To simplify calculations,
    // consider storing these readings as unit vectors.
    override fun onSensorChanged(sensorEvent: SensorEvent) {
        // The sensor type (as defined in the Sensor class).

        // The sensor type (as defined in the Sensor class).
        val sensorType: Int = sensorEvent.sensor.getType()

        // The sensorEvent object is reused across calls to onSensorChanged().
        // clone() gets a copy so the data doesn't change out from under us
        when (sensorType) {
            Sensor.TYPE_ACCELEROMETER -> mAccelerometerData = sensorEvent.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> mMagnetometerData = sensorEvent.values.clone()
            else -> return
        }
        // Compute the rotation matrix: merges and translates the data
        // from the accelerometer and magnetometer, in the device coordinate
        // system, into a matrix in the world's coordinate system.
        //
        // The second argument is an inclination matrix, which isn't
        // used in this example.
        // Compute the rotation matrix: merges and translates the data
        // from the accelerometer and magnetometer, in the device coordinate
        // system, into a matrix in the world's coordinate system.
        //
        // The second argument is an inclination matrix, which isn't
        // used in this example.
        val rotationMatrix = FloatArray(9)
        val rotationOK = SensorManager.getRotationMatrix(
            rotationMatrix,
            null, mAccelerometerData, mMagnetometerData
        )

        // Remap the matrix based on current device/activity rotation.

        // Remap the matrix based on current device/activity rotation.
        var rotationMatrixAdjusted = FloatArray(9)
        when (mDisplay?.rotation) {
            Surface.ROTATION_0 -> rotationMatrixAdjusted = rotationMatrix.clone()
            Surface.ROTATION_90 -> SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X,
                rotationMatrixAdjusted
            )
            Surface.ROTATION_180 -> SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y,
                rotationMatrixAdjusted
            )
            Surface.ROTATION_270 -> SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X,
                rotationMatrixAdjusted
            )
        }

        // Get the orientation of the device (azimuth, pitch, roll) based
        // on the rotation matrix. Output units are radians.

        // Get the orientation of the device (azimuth, pitch, roll) based
        // on the rotation matrix. Output units are radians.
        val orientationValues = FloatArray(3)
        if (rotationOK) {
            SensorManager.getOrientation(
                rotationMatrixAdjusted,
                orientationValues
            )
        }

        // Pull out the individual values from the array.

        // Pull out the individual values from the array.
        val azimuth = orientationValues[0]
        var pitch = orientationValues[1]
        var roll = orientationValues[2]

        // Pitch and roll values that are close to but not 0 cause the
        // animation to flash a lot. Adjust pitch and roll to 0 for very
        // small values (as defined by VALUE_DRIFT).

        // Pitch and roll values that are close to but not 0 cause the
        // animation to flash a lot. Adjust pitch and roll to 0 for very
        // small values (as defined by VALUE_DRIFT).
        if (Math.abs(pitch) < VALUE_DRIFT) {
            pitch = 0f
        }
        if (Math.abs(roll) < VALUE_DRIFT) {
            roll = 0f
        }
        if (Math.abs(roll) >= 1.5) {
            congrats.setText("Stop scrolling! Place phone on a flat surface, face up.")
        }
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    fun updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )
        // "rotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        // "orientationAngles" now has up-to-date information.
    }
}