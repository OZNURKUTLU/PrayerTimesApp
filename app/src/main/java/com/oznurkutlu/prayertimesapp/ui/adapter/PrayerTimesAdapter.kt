package com.oznurkutlu.prayertimesapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oznurkutlu.prayertimesapp.R
import com.oznurkutlu.prayertimesapp.ui.model.PrayerTimeItem

class PrayerTimesAdapter : RecyclerView.Adapter<PrayerTimesAdapter.PrayerTimeViewHolder>() {

    private var prayerTimes: List<PrayerTimeItem> = emptyList()

    fun submitList(newList: List<PrayerTimeItem>) {
        val diffResult = DiffUtil.calculateDiff(PrayerTimeDiffCallback(prayerTimes, newList))
        prayerTimes = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class PrayerTimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayerTimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_prayer_time, parent, false)
        return PrayerTimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrayerTimeViewHolder, position: Int) {
        val item = prayerTimes[position]
        holder.textViewName.text = item.name
        holder.textViewTime.text = item.time
    }

    override fun getItemCount(): Int {
        return prayerTimes.size
    }

    class PrayerTimeDiffCallback(
        private val oldList: List<PrayerTimeItem>,
        private val newList: List<PrayerTimeItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}