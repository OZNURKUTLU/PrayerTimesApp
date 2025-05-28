package com.oznurkutlu.prayertimesapp.ui.adapter

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oznurkutlu.prayertimesapp.R
import com.oznurkutlu.prayertimesapp.ui.model.City

class PlaceAdapter(
    private var isGpsEnabled: Boolean = false,
    private val onDeleteClick: (City) -> Unit,
    private val onDefaultClick: (City) -> Unit,
    private val onGpsCheckClick: (Boolean) -> Unit, // GPS check/uncheck click listener
    context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CITY = 0
        private const val VIEW_TYPE_GPS = 1
    }

    private var cities: MutableList<Any> = mutableListOf() // Any türünde liste
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private var currentGpsCity: City? = null

    fun submitList(newList: List<Any>) {
        val diffResult = DiffUtil.calculateDiff(CityDiffCallback(cities, newList))
        cities.clear()
        cities.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
        notifyItemChanged(0)
    }

    fun setGpsCity(gpsCity: City?) {
        currentGpsCity = gpsCity
        notifyItemChanged(0) // GPS öğesini yeniden bağla ki label güncellensin
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_GPS else VIEW_TYPE_CITY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
         return when (viewType) {
            VIEW_TYPE_GPS -> GpsSelectionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_gps_selection, parent, false))
            VIEW_TYPE_CITY -> CityViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_GPS -> {
                val gpsViewHolder = holder as GpsSelectionViewHolder
                gpsViewHolder.bind(prefs.getBoolean("use_gps", false), currentGpsCity) { isChecked -> // currentGpsCity'i ekledik
                    // Tüm şehirlerin seçimini kaldır (UI için)
                    val newList = cities.mapIndexed { index, item ->
                        if (index != 0 && item is City) {
                            item.copy(isDefault = false)
                        } else {
                            item
                        }
                    }
                    submitList(newList)
                    onGpsCheckClick.invoke(isChecked)
                }
            }
            VIEW_TYPE_CITY -> {
                if (cities[position] is City) {
                    (holder as CityViewHolder).bind(cities[position] as City) { city ->
                        // GPS'in seçimini kaldır (ViewModel'a bildir)
                        onGpsCheckClick.invoke(false)
                        onDefaultClick.invoke(city) // Şehir seçimi için callback
                        // Şehirlerin seçimini yönet (UI için)
                        val newList = cities.mapIndexed { index, item ->
                            if (index != 0 && item is City) {
                                item.copy(isDefault = item.id == city.id)
                            } else {
                                item
                            }
                        }
                        submitList(newList)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewCityName: TextView = itemView.findViewById(R.id.textViewCityName)
        val textViewCountryName: TextView = itemView.findViewById(R.id.textViewCountryName)
        val imageButtonDelete: ImageButton = itemView.findViewById(R.id.imageButtonDelete)
        val imageButtonDefault: ImageButton = itemView.findViewById(R.id.imageButtonDefault)

        fun bind(city: City, onCityClick: (City) -> Unit) {
            textViewCityName.text = city.name
            textViewCountryName.text = city.country
            imageButtonDelete.setOnClickListener { onDeleteClick(city) }
            imageButtonDefault.setImageResource(
                if (city.isDefault) R.drawable.check_mark else R.drawable.un_check
            )
            itemView.setOnClickListener {
                Log.d("PlaceAdapter", "Şehir tıklandı: ${city.name}")
                onCityClick(city)
            } // Şehire tıklayınca seçimi bildir
        }
    }

    inner class GpsSelectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageButtonGpsCheck: ImageButton = itemView.findViewById(R.id.imageButtonGpsCheck)
        val textViewGpsLabel: TextView = itemView.findViewById(R.id.textViewGpsLabel)

        fun bind(isGpsEnabled: Boolean, gpsCity: City?, onGpsCheckChange: (Boolean) -> Unit) {
            imageButtonGpsCheck.isSelected = isGpsEnabled
            imageButtonGpsCheck.setImageResource(if (isGpsEnabled) R.drawable.check_mark else R.drawable.un_check)
            textViewGpsLabel.text = if (gpsCity != null) {
                "GPS ile Konum Kullan (${gpsCity.name}, ${gpsCity.country})"
            } else {
                "GPS ile Konum Kullan"
            }
            imageButtonGpsCheck.setOnClickListener {
                val newState = !imageButtonGpsCheck.isSelected
                onGpsCheckChange.invoke(newState)
            }
        }
    }

    fun setGpsEnabled(enabled: Boolean) {
        if (this.isGpsEnabled != enabled) {
            this.isGpsEnabled = enabled
            notifyItemChanged(0) // İlk öğe (GPS) güncellenmeli
        }
    }


    class CityDiffCallback(
        private val oldList: List<Any>,
        private val newList: List<Any>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return when {
                oldItemPosition == 0 && newItemPosition == 0 -> true // GPS öğesi
                oldList[oldItemPosition] is City && newList[newItemPosition] is City ->
                    (oldList[oldItemPosition] as City).id == (newList[newItemPosition] as City).id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return when {
                oldItemPosition == 0 && newItemPosition == 0 -> true // GPS öğesi
                oldList[oldItemPosition] is City && newList[newItemPosition] is City -> {
                    val oldCity = oldList[oldItemPosition] as City
                    val newCity = newList[newItemPosition] as City
                    oldCity == newCity // Tüm alanları karşılaştırır, isDefault da dahil
                }
                else -> false
            }
        }
    }
}