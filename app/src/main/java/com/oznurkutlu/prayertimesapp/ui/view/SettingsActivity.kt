package com.oznurkutlu.prayertimesapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.oznurkutlu.prayertimesapp.databinding.ActivitySettingsBinding
import com.oznurkutlu.prayertimesapp.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        observeViewModel()
        setupListeners()
        setupSpinners()

        // onItemSelectedListener'ı onCreate içinde tanımlıyoruz
        binding.spinnerAlarmSound.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSound = parent?.getItemAtPosition(position).toString()
                viewModel.setAlarmSound(selectedSound)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Hiçbir şey seçilmediğinde yapılacak işlemler
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbarSettings.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun observeViewModel() {
       /* viewModel.useGps.observe(this, Observer { useGps ->
            binding.switchUseGps.isChecked = useGps
        })*/

        viewModel.alarmSound.observe(this, Observer { alarmSound ->
            // Alarm sesini spinner'da seçili hale getir
            val adapter = binding.spinnerAlarmSound.adapter as ArrayAdapter<String>
            val position = adapter.getPosition(alarmSound)
            binding.spinnerAlarmSound.setSelection(position)
        })

       // viewModel.useGps.observe(this, Observer { useGps ->
            // GPS kullanım ayarlarını güncelle
        //    binding.switchUseGps.isChecked = useGps
       // })


        // Şehirler listesi için viewModel'den gelen veriyi gözlemleyebilirsiniz
        // Örneğin:
        // viewModel.cities.observe(this, Observer { cities ->
        //     val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        //     binding.spinnerCity.adapter = adapter
        // })
    }

    private fun setupListeners() {
       /* binding.switchUseGps.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setUseGps(isChecked)
        }*/

        // setOnItemClickListener'ı kaldırıyoruz, zaten onItemSelectedListener kullanıyoruz
        // binding.spinnerAlarmSound.setOnItemClickListener { parent, view, position, id ->
        //     val selectedSound = parent.getItemAtPosition(position).toString()
        //     viewModel.setAlarmSound(selectedSound)
        // }

        binding.buttonUpdateTimes.setOnClickListener {
            // Vakitleri güncelleme işlemleri (viewModel'de tanımlanmalı)
            // viewModel.updatePrayerTimes()
        }

        // Konum Tercihleri butonu için tıklama dinleyicisi ekliyoruz
        binding.locationPreferencesButton.setOnClickListener {
            val intent = Intent(this, PlaceSelectionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupSpinners() {
        // Alarm sesleri için örnek bir liste
        val alarmSounds = arrayOf("Varsayılan", "Alarm 1", "Alarm 2")
        val alarmAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, alarmSounds)
        binding.spinnerAlarmSound.adapter = alarmAdapter

        // Şehirler için örnek bir liste (viewModel'den alabilirsiniz)
        //val cities = arrayOf("İstanbul", "Ankara", "İzmir")
        //val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
       // binding.spinnerCity.adapter = cityAdapter
    }
}