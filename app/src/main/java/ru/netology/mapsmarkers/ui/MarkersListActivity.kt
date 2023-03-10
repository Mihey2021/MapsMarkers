package ru.netology.mapsmarkers.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yandex.mapkit.geometry.Point
import ru.netology.mapsmarkers.R
import ru.netology.mapsmarkers.adapters.*
import ru.netology.mapsmarkers.databinding.ActivityMarkersListBinding
import ru.netology.mapsmarkers.databinding.AddMarkerDialogBinding
import ru.netology.mapsmarkers.dto.UserMarker
import ru.netology.mapsmarkers.viewmodel.UserMarkerViewModel

class MarkersListActivity : AppCompatActivity() {

    private val viewModel: UserMarkerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMarkersListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cancelBtn = binding.btnCancel
        cancelBtn.setOnClickListener {
            this.finish()
        }

        val markersRecyclerView = binding.recyclerView

        viewModel.userMarkersList.observe(this) { markersList ->
            setupRecyclerViewAdapter(markersRecyclerView, markersList)
        }

    }

    private fun setupRecyclerViewAdapter(
        recyclerView: RecyclerView,
        markersList: List<UserMarker>
    ) {
        val adapter = MarkersListRecyclerViewAdapter(object : MarkersListActionListener {
            override fun onRemoveListener(id: Long) {
                removeMarker(id)
            }

            override fun onEditListener(marker: UserMarker) {
                showEditMarkerDialog(Point(marker.latitude, marker.longitude), marker)
            }

            override fun onItemClickListener(marker: UserMarker) {
                val intent = Intent()
                setResult(
                    Activity.RESULT_OK,
                    intent.putExtra(IntentKeys.RESULT_KEY.key, marker)
                )
                this@MarkersListActivity.finish()
            }
        })

        recyclerView.adapter = adapter
        adapter.submitList(markersList)
    }

    private fun removeMarker(id: Long) {
        viewModel.removeMarkerById(id)
    }

    @SuppressLint("SetTextI18n")
    private fun showEditMarkerDialog(coordinates: Point, editingMarker: UserMarker? = null) {
        val dialogBinding = AddMarkerDialogBinding.inflate(layoutInflater)
        dialogBinding.coordinatesText.text = "${coordinates.latitude} ${coordinates.longitude}"
        editingMarker?.let { dialogBinding.descriptionText.setText(it.description) }
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.editing_marker))
            .setView(dialogBinding.root)
            .setCancelable(false)
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(if (editingMarker == null) getString(R.string.add) else getString(R.string.save)) { dialog, _ ->
                val description = dialogBinding.descriptionText.text.toString()
                if (description.isNotBlank()) {
                    val marker = UserMarker(
                        latitude = coordinates.latitude,
                        longitude = coordinates.longitude,
                        description = description
                    )
                    editingMarker?.let { viewModel.save(editingMarker.copy(description = description)) }
                        ?: viewModel.save(marker)
                }
                dialog.dismiss()
            }
            .show()
    }

}