package ru.netology.mapsmarkers.ui

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import ru.netology.mapsmarkers.databinding.FragmentMarkersListBinding

class MarkersListFragment : AppCompatActivity() {

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        val binding = FragmentMarkersListBinding.inflate(layoutInflater)
        return binding.root
    }
}