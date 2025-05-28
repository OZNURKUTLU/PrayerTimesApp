package com.oznurkutlu.prayertimesapp.ui.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.oznurkutlu.prayertimesapp.R
import com.oznurkutlu.prayertimesapp.databinding.ActivityQiblaCompassBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.*

@AndroidEntryPoint
class QiblaCompassActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var compassImage: ImageView
    private lateinit var kabeImage: ImageView
    private lateinit var sensorManager: SensorManager
    private lateinit var binding: ActivityQiblaCompassBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private var currentDegree = 0f
    private var qiblaDegree = 0f
    private val LOCATION_PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQiblaCompassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("CompassActivity", "onCreate çağrıldı")


        compassImage = findViewById(R.id.compassImage)
        kabeImage = findViewById(R.id.kabeImage)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (fusedLocationClient == null) { Log.e("QiblaCompassActivity", "fusedLocationClient null!") }

        getLocation()

        compassImage.post {
            kabeImage.pivotX = kabeImage.width / 2f
            kabeImage.pivotY = kabeImage.height / 2f
        }
    }

    private fun getLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    Log.d("QiblaCompassActivity", "Konum alındı: $location")
                    location?.let {
                        currentLocation = it
                        qiblaDegree = calculateQiblaDirection(it.latitude, it.longitude).toFloat()
                        updateQiblaDirection()
                    } ?: run {
                        Toast.makeText(this, "Konum alınamadı.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("QiblaCompassActivity", "Konum alırken hata oluştu: ${e.localizedMessage}", e)
                    Toast.makeText(this, "Konum alırken hata oluştu: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        } else {
            requestLocationPermission()
        }
    }
    private fun updateQiblaDirection() {
        val targetRotation = (qiblaDegree - currentDegree + 180) % 360
        kabeImage.rotation = targetRotation
    }

    private fun calculateQiblaDirection(lat: Double, lon: Double): Double {
        val kaabaLat = 21.4225 // Kabe'nin enlemi
        val kaabaLon = 39.8262 // Kabe'nin boylamı

        val latRad = Math.toRadians(lat)
        val lonRad = Math.toRadians(lon)
        val kaabaLatRad = Math.toRadians(kaabaLat)
        val kaabaLonRad = Math.toRadians(kaabaLon)

        val deltaLon = kaabaLonRad - lonRad

        val y = sin(deltaLon)
        val x = cos(latRad) * tan(kaabaLatRad) - sin(latRad) * cos(deltaLon)

        return (Math.toDegrees(atan2(y, x)) + 360) % 360
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private var alpha = 0.1f
    private var filteredDegree = 0f

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ORIENTATION) {
            val degree = event.values[0]
            filteredDegree = alpha * degree + (1 - alpha) * filteredDegree

            compassImage.rotation = -filteredDegree

            val targetRotation = (qiblaDegree - filteredDegree + 180) % 360
            kabeImage.rotation = targetRotation
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}


    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("QiblaCompassActivity", "Konum izni verildi.")
                getLocation()
            } else {
                Toast.makeText(this, "Konum izni reddedildi.", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}