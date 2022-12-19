package ru.netology.mapsmarkers.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import ru.netology.mapsmarkers.R
import ru.netology.mapsmarkers.databinding.MarkerItemBinding
import ru.netology.mapsmarkers.dto.UserMarker

typealias OnRemoveListener = (UserMarker) -> Unit
typealias OnEditListener = (UserMarker) -> Unit
typealias OnClickListener = (UserMarker) -> Unit

class MarkersListAdapter(
    private val markersList: List<UserMarker>,
    private val onRemoveListener: OnRemoveListener,
    private val onEditListener: OnEditListener,
    private val onClickListener: OnClickListener,
): BaseAdapter(), View.OnClickListener {
    override fun getCount(): Int {
        return markersList.size
    }

    override fun getItem(position: Int): UserMarker {
        return markersList[position]
    }

    override fun getItemId(position: Int): Long {
        return markersList[position].id
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding =
            convertView?.tag as MarkerItemBinding? ?: createBinding(parent.context)

        val marker = getItem(position)
        binding.descriptionText.text = marker.description
        binding.coordinatesText.text = "${marker.latitude} ${marker.longitude}"
        binding.markerImage.tag = marker
        binding.editButton.tag = marker
        binding.removeButton.tag = marker

        return binding.root
    }

    private fun createBinding(context: Context): MarkerItemBinding {
        val binding = MarkerItemBinding.inflate(LayoutInflater.from(context))
        binding.markerImage.setOnClickListener(this)
        binding.removeButton.setOnClickListener(this)
        binding.editButton.setOnClickListener(this)
        binding.root.tag = binding
        return binding
    }

    override fun onClick(v: View) {
        val marker = v.tag as UserMarker
        if (v.id == R.id.removeButton)
            onRemoveListener.invoke(marker)
        if (v.id == R.id.editButton)
            onEditListener.invoke(marker)
        if (v.id == R.id.markerImage)
            onClickListener.invoke(marker)
    }

}