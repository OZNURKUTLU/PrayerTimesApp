package com.oznurkutlu.prayertimesapp.ui.view

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.oznurkutlu.prayertimesapp.databinding.ActivityPlaceSearchBinding
import com.oznurkutlu.prayertimesapp.ui.adapter.PlaceSearchAdapter
import com.oznurkutlu.prayertimesapp.ui.model.City
import com.oznurkutlu.prayertimesapp.ui.viewmodel.PlaceSearchViewModel
import com.oznurkutlu.prayertimesapp.ui.viewmodel.PlaceSelectionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class PlaceSearchActivity : AppCompatActivity() {

    private var searchJob: Job? = null
    private lateinit var binding: ActivityPlaceSearchBinding
    private val viewModel: PlaceSearchViewModel by viewModels()
    private val placeSelectionViewModel: PlaceSelectionViewModel by viewModels() // PlaceSelectionViewModel eklendi
    private val adapter = PlaceSearchAdapter { city ->
        viewModel.addCity(city, placeSelectionViewModel) // placeSelectionViewModel parametresi eklendi
        finish() // Activity'yi kapat
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewSearchResults.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSearchResults.adapter = adapter

        viewModel.searchResults.observe(this) { cities: List<City> -> // Türü açıkça belirtin
            adapter.submitList(cities)
        }
        // Yükleme durumunu gözlemleme
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBarLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Hata mesajlarını gözlemleme
        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.searchViewCity.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {




            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                if ((newText?.length ?: 0) >= 3) { // Minimum 3 karakter
                    searchJob = lifecycleScope.launch {
                        delay(300)
                        viewModel.searchCities(newText.orEmpty())
                    }
                } else {
                    adapter.submitList(emptyList()) // Boş liste göster
                }
                return true
            }
        })

        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    delay(3000) // 3 saniye bekle
                    viewModel.errorMessage.value = null // Hata mesajını temizle
                }
            }
        }
    }
}