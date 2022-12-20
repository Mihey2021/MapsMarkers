package ru.netology.mapsmarkers.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.mapsmarkers.databinding.MarkerItemBinding
import ru.netology.mapsmarkers.dto.UserMarker

interface MarkersListActionListener {
    fun onRemoveListener(id: Long)
    fun onEditListener(marker: UserMarker)
    fun onItemClickListener(marker: UserMarker)
}

class MarkersListRecyclerViewAdapter(
    private val actionListener: MarkersListActionListener,
) : ListAdapter<UserMarker, MarkersListRecyclerViewAdapter.MarkersListViewHolder>(PostDiffCallback()) {

    class MarkersListViewHolder(
        private val binding: MarkerItemBinding,
        private val actionListener: MarkersListActionListener,
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(marker: UserMarker) {
            binding.apply {
                root.setOnClickListener {
                    actionListener.onItemClickListener(marker)
                }
                descriptionText.text = marker.description
                coordinatesText.text = "${marker.latitude} ${marker.longitude}"
                editButton.setOnClickListener {
                    actionListener.onEditListener(marker)
                }
                removeButton.setOnClickListener {
                    actionListener.onRemoveListener(marker.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkersListViewHolder {
        val binding = MarkerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MarkersListViewHolder(binding, actionListener)
    }

    override fun onBindViewHolder(holder: MarkersListViewHolder, position: Int) {
        val marker = getItem(position)
        holder.bind(marker)
    }

}

class PostDiffCallback : DiffUtil.ItemCallback<UserMarker>() {
    override fun areItemsTheSame(oldItem: UserMarker, newItem: UserMarker): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserMarker, newItem: UserMarker): Boolean {
        return oldItem == newItem
    }
}