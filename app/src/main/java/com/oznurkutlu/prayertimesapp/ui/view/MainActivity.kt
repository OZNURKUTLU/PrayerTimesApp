package com.oznurkutlu.prayertimesapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.oznurkutlu.prayertimesapp.databinding.ActivityMainBinding
import com.oznurkutlu.prayertimesapp.ui.adapter.PrayerTimesAdapter
import com.oznurkutlu.prayertimesapp.ui.viewmodel.PrayerTimesViewModel
import com.oznurkutlu.prayertimesapp.utils.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: PrayerTimesViewModel by viewModels()
    private val adapter = PrayerTimesAdapter()
    private var isQiblaFragmentVisible = false
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewPrayerTimes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPrayerTimes.adapter = adapter

        binding.kibleContainer.visibility = View.GONE

        setupListeners()
        observeViewModel()
        loadPrayerTimes()

        // OnBackPressedCallback'i oluştur ve kaydet
        onBackPressedCallback = object : OnBackPressedCallback(false) { // Başlangıçta devre dışı
            override fun handleOnBackPressed() {
                if (isQiblaFragmentVisible) {
                    supportFragmentManager.popBackStack()
                    binding.kibleContainer.visibility = View.GONE
                    isQiblaFragmentVisible = false
                    isEnabled = false // Geri tuşu işlendikten sonra bu callback'i devre dışı bırak
                    onBackPressedDispatcher.onBackPressed() // Varsayılan geri tuşu davranışını tetikle
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun setupListeners() {
        binding.buttonSettings.setOnClickListener {
            val intent = Intent(this, PlaceSelectionActivity::class.java)
            //val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.buttonQibla.setOnClickListener {
            Log.d("MainActivity", "Kıble butonu tıklandı (Activity başlatılıyor)")
            val intent = Intent(this, QiblaCompassActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.prayerTimes.observe(this) { prayerTimes ->
            adapter.submitList(prayerTimes)
        }

        viewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

        viewModel.defaultLocation.observe(this) { defaultLocation ->
            if (defaultLocation != null) {
                viewModel.fetchPrayerTimesByPlace(defaultLocation.id, DateUtils.getCurrentDate(), 1)
            } else {
                Toast.makeText(this, "Lütfen Önce Konum Seçiniz", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadPrayerTimes() {
        viewModel.getDefaultLocation()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDefaultLocation()

    }
}

