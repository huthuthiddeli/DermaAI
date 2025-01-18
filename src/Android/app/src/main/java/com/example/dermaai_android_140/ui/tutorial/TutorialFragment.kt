package com.example.dermaai_android_140.ui.tutorial

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.OverScroller
import android.widget.ScrollView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.example.dermaai_android_140.databinding.FragmentTutorialBinding


class TutorialFragment : Fragment() {

    private var _binding: FragmentTutorialBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTutorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scrollView.setScrollbarPositionLeft() // Call the extension function
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun ScrollView.setScrollbarPositionLeft() {
    try {
        val field = ScrollView::class.java.getDeclaredField("mScrollCache")
        field.isAccessible = true
        val scrollCache = field.get(this)

        val scrollBarField = scrollCache?.javaClass?.getDeclaredField("scrollBar")
        scrollBarField?.isAccessible = true
        val scrollBar = scrollBarField?.get(scrollCache) as? OverScroller

        val positionField = scrollBar?.javaClass?.getDeclaredField("mState")
        positionField?.isAccessible = true
        positionField?.setInt(scrollBar, ViewCompat.SCROLL_INDICATOR_LEFT)

    } catch (e: Exception) {
        Log.e("ScrollView", "Error setting scrollbar position: ${e.message}")
    }
}