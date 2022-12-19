package ru.netology.mapsmarkers.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import ru.netology.mapsmarkers.R
import ru.netology.mapsmarkers.adapters.MarkersListAdapter
import ru.netology.mapsmarkers.databinding.ActivityMainBinding
import ru.netology.mapsmarkers.databinding.AddMarkerDialogBinding
import ru.netology.mapsmarkers.dto.UserMarker
import ru.netology.mapsmarkers.viewmodel.UserMarkerViewModel
import ru.netology.mapsmarkers.extensions.setIcon
import java.io.Serializable

enum class IntentKeys(val key: String) {
    INPUT_KEY("input_key"),
    RESULT_KEY("result_key")
}

private lateinit var mapView: MapView
private lateinit var mapObjects: MapObjectCollection

class MainActivity : AppCompatActivity(), InputListener {

    private val viewModel: UserMarkerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MapKitFactory.initialize(this)

        mapView = binding.mapview
        mapView.map.addInputListener(this)

        mapObjects = mapView.map.mapObjects.addCollection()

        val userLocationBtn = binding.userLocationBtn
        userLocationBtn.setOnClickListener {
            onUserLocationClick()
        }

        val activityLauncher = registerForActivityResult(MarkersListActivityContract()) { result ->
            if (result == null) return@registerForActivityResult
            moveMapCamera(Point(result.latitude, result.longitude))
        }

        val listOfMarkersBtn = binding.markersListBtn
        listOfMarkersBtn.setOnClickListener {
            activityLauncher.launch(0)
        }

        requestLocationPermission()

        val mapKit = MapKitFactory.getInstance()
        mapKit.resetLocationManagerToDefault()
        val userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        //userLocationLayer.setObjectListener(this)

        viewModel.userMarkersList.observe(this) { markersList ->
            clearMarkers()
            markersList.forEach { marker ->
                addMarker(marker)
            }
        }

    }

    private fun onUserLocationClick() {
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        when {
            // 1. Проверяем есть ли уже права
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {

                val fusedLocationProviderClient = LocationServices
                    .getFusedLocationProviderClient(this)

                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    if (location == null) {
                        showLocationPermissionDialog(
                            getString(R.string.err_current_geo),
                            false
                        )
                        return@addOnSuccessListener
                    }
                    moveMapCamera(Point(location.latitude, location.longitude))
                }
            }
            // 2. Должны показать обоснование необходимости прав
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showLocationPermissionDialog(
                    getString(R.string.request_permission_geo_rationale),
                    true
                )
            }
            // 3. Запрашиваем права
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun showLocationPermissionDialog(message: String, retry: Boolean = false) {

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.access_to_geo))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                if (retry) requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                dialog.dismiss()
            }
            .show()

    }

    private fun moveMapCamera(coordinates: Point) {
        mapView.map.move(
            CameraPosition(coordinates, 15f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 5f),
            null
        )
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted)
                showLocationPermissionDialog(getString(R.string.check_geo_permission))
        }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onMapTap(map: Map, coordinates: Point) {
    }

    override fun onMapLongTap(map: Map, coordinates: Point) {
        showEditMarkerDialog(coordinates)
    }

    @SuppressLint("SetTextI18n")
    private fun showEditMarkerDialog(coordinates: Point, editingMarker: UserMarker? = null) {
        val dialogBinding = AddMarkerDialogBinding.inflate(layoutInflater)
        dialogBinding.coordinatesText.text = "${coordinates.latitude} ${coordinates.longitude}"
        editingMarker?.let { dialogBinding.descriptionText.setText(it.description) }
        MaterialAlertDialogBuilder(this)
            .setTitle(if (editingMarker == null) getString(R.string.adding_marker) else getString(R.string.editing_marker))
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addMarker(marker: UserMarker) {
        val coordinates = Point(marker.latitude, marker.longitude)
        mapObjects.addPlacemark(coordinates).apply {
            opacity = 0.9f
            userData = marker
            setIcon(getDrawable(R.drawable.ic_baseline_user_marker)!!)
            addTapListener(markerTapListener)
        }
    }

    private val markerTapListener = MapObjectTapListener { mapObject, _ ->
        if (mapObject is PlacemarkMapObject) {
            val userMarker = mapObject.userData
            if (userMarker is UserMarker)
                showMarkerDetailDialog(userMarker)
        }
        true
    }

    private fun clearMarkers() {
        mapObjects.clear()
    }

    private fun showMarkerDetailDialog(marker: UserMarker) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.marker))
            .setMessage(marker.toString())
            .setIcon(R.drawable.ic_baseline_user_marker)
            .setNegativeButton(getString(R.string.remove)) { dialog, _ ->
                viewModel.removeMarkerById(marker.id)
                dialog.dismiss()
            }
            .setNeutralButton(getString(R.string.edit)) { dialog, _ ->
                showEditMarkerDialog(Point(marker.latitude, marker.longitude), marker)
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    class MarkersListActivityContract : ActivityResultContract<Int?, UserMarker?>() {
        override fun createIntent(context: Context, input: Int?): Intent {
            return Intent(context, MarkersListActivity::class.java)
                .putExtra(IntentKeys.INPUT_KEY.key, 0)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): UserMarker? =
            when {
                resultCode != Activity.RESULT_OK -> null
                else -> getSerializable(intent, IntentKeys.RESULT_KEY.key, UserMarker::class.java)
            }

        private fun <T : Serializable?> getSerializable(
            intent: Intent?,
            name: String,
            clazz: Class<T>
        ): T {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent?.getSerializableExtra(name, clazz)!!
            else
                intent?.getSerializableExtra(name) as T
        }

    }
}
