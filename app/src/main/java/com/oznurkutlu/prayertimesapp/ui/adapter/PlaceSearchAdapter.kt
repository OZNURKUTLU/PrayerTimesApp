package com.oznurkutlu.prayertimesapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.oznurkutlu.prayertimesapp.databinding.ItemSearchResultBinding
import com.oznurkutlu.prayertimesapp.ui.model.City

class PlaceSearchAdapter(private val onItemClick: (City) -> Unit) : ListAdapter<City, PlaceSearchAdapter.PlaceSearchViewHolder>(PlaceDiffCallback()) {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun submitList(list: List<City>?) {
        selectedPosition = RecyclerView.NO_POSITION // Seçili pozisyonu sıfırla
        super.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceSearchViewHolder {
        val binding = ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceSearchViewHolder, position: Int) {
        val city = getItem(position)
        holder.bind(city)
        holder.itemView.setOnClickListener {
            val adapterPosition = holder.adapterPosition // Doğru kullanım
            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(selectedPosition)
                }
                selectedPosition = adapterPosition
                notifyItemChanged(adapterPosition)
                onItemClick(city)
            }
        }
    }

    inner class PlaceSearchViewHolder(private val binding: ItemSearchResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: City) {
            binding.textViewPlaceName.text = city.name
            binding.textViewPlaceDetails.text = "${city.city}, ${city.country}"
            binding.root.isSelected = adapterPosition == selectedPosition
        }
    }

    class PlaceDiffCallback : DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City, newItem: City): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: City, newItem: City): Boolean = oldItem == newItem
    }
}