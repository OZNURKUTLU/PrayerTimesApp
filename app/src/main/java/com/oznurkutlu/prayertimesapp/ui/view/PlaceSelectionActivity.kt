package com.oznurkutlu.prayertimesapp.ui.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.oznurkutlu.prayertimesapp.data.repository.PrayerTimesRepository
import com.oznurkutlu.prayertimesapp.databinding.ActivityPlaceSelectionBinding
import com.oznurkutlu.prayertimesapp.ui.adapter.PlaceAdapter
import com.oznurkutlu.prayertimesapp.ui.model.City
import com.oznurkutlu.prayertimesapp.ui.viewmodel.PlaceSelectionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlaceSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaceSelectionBinding
    private val viewModel: PlaceSelectionViewModel by viewModels()
    private lateinit var locationPermissionRequest: ActivityResultLauncher<String>

    private val adapter by lazy {
        PlaceAdapter(
            onDeleteClick = { city ->
                viewModel.deleteCity(city)
            },
            onDefaultClick = { city ->
                viewModel.setDefaultCity(city)
            },
            onGpsCheckClick = { isChecked ->
                viewModel.onGpsSelected(isChecked)
                if (isChecked) {
                    checkLocationPermissions()
                }
            },
            context = applicationContext
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getLocationFromGps()
            } else {
                Toast.makeText(this, "Konum izni reddedildi.", Toast.LENGTH_SHORT).show()
                // ViewModel'daki GPS seçimini geri al
                viewModel.onGpsSelected(false)
            }
        }

        binding.recyclerViewCities.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCities.adapter = adapter

        viewModel.cities.observe(this) { cities ->
            Log.d("PlaceSelectionActivity", "Gelen Şehirler Listesi (Observe): $cities")
            val list = mutableListOf<Any>()
            list.add(Any()) // GPS öğesi (adapter'da işlenecek)
            list.addAll(cities)
            adapter.submitList(list)
        }

        viewModel.gpsCity.observe(this) { gpsCity ->
            adapter.setGpsCity(gpsCity) // Adapter'a GPS şehrini gönder
        }

        viewModel.useGps.observe(this) { useGps ->
            // Adapter'daki GPS seçeneğini buna göre işaretle (adapter'da implementasyon gerekiyor)
            // Adapter'a GPS durumu bilgisini göndermek için yeni bir fonksiyon ekleyebilirsiniz.
            adapter.setGpsEnabled(useGps)
        }

        binding.fabAddCity.setOnClickListener {
            val intent = Intent(this, PlaceSearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            getLocationFromGps()
        }
    }

    private fun getLocationFromGps() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        location?.let {
                            val latitude = it.latitude
                            val longitude = it.longitude

                            // Konum değerlerini Preferences'a kaydet
                            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                            prefs.edit()
                                .putBoolean("use_gps", true)
                                .putFloat("gps_latitude", latitude.toFloat())
                                .putFloat("gps_longitude", longitude.toFloat())
                                .apply()

                            viewModel.fetchPrayerTimesForGps(latitude, longitude)

                        } ?: run {
                            Toast.makeText(this, "Son bilinen konum alınamadı.", Toast.LENGTH_SHORT).show()
                            viewModel.onGpsSelected(false)
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Konum alınırken hata oluştu.", Toast.LENGTH_SHORT).show()
                        viewModel.onGpsSelected(false)
                    }
            } catch (e: SecurityException) {
                // Kullanıcı izni reddettiğinde veya izinler olmadan çağrıldığında bu hatayı yakalar
                Toast.makeText(this, "Konum izni verilmedi.", Toast.LENGTH_SHORT).show()
                viewModel.onGpsSelected(false)

            }
        } else {
            // İzin henüz verilmemiş, izin isteme başlatılır
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }



    override fun onResume() {
        super.onResume()
        viewModel.getCities() // Verileri yeniden yükle
    }

    private fun checkAndGetLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        location?.let {
                            val newLatitude = it.latitude
                            val newLongitude = it.longitude
                            compareWithPreviousLocation(newLatitude, newLongitude)
                        } ?: run {
                            Toast.makeText(this, "Son bilinen konum alınamadı.", Toast.LENGTH_SHORT).show()
                            viewModel.onGpsSelected(false)
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Konum alınırken hata oluştu.", Toast.LENGTH_SHORT).show()
                        viewModel.onGpsSelected(false)
                    }
            } catch (e: SecurityException) {
                Toast.makeText(this, "Konum izni verilmedi.", Toast.LENGTH_SHORT).show()
                viewModel.onGpsSelected(false)
                // İsteğe bağlı: Kullanıcıyı izin ayarlarına yönlendirebilirsiniz
            }
        } else {
            // İzin henüz verilmemiş, izin isteme başlatılır
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun compareWithPreviousLocation(newLatitude: Double, newLongitude: Double) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val previousLatitude = prefs.getFloat("gps_latitude", Float.MAX_VALUE).toDouble()
        val previousLongitude = prefs.getFloat("gps_longitude", Float.MAX_VALUE).toDouble()

        if (previousLatitude == Float.MAX_VALUE.toDouble() || previousLongitude == Float.MAX_VALUE.toDouble()) {
            // İlk konum alımı
            saveNewLocationAndFetchData(newLatitude, newLongitude)
            return
        }

        val results = FloatArray(1)
        Location.distanceBetween(
            previousLatitude,
            previousLongitude,
            newLatitude,
            newLongitude,
            results
        )

        val distanceInMeters = results[0]

        if (distanceInMeters > 1000) { // Örnek eşik: 1 km
            Toast.makeText(this, "Yeni konum bulundu.", Toast.LENGTH_SHORT).show()
            saveNewLocationAndFetchData(newLatitude, newLongitude)
        } else {
            Toast.makeText(this, "Konumunuz değişmedi.", Toast.LENGTH_SHORT).show()
            // İsteğe bağlı: Kullanıcıya mevcut konumu göster veya başka bir işlem yap
        }
    }

    private fun saveNewLocationAndFetchData(latitude: Double, longitude: Double) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit()
            .putFloat("gps_latitude", latitude.toFloat())
            .putFloat("gps_longitude", longitude.toFloat())
            .apply()

    }

}