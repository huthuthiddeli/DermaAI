package com.example.dermaai_android_140.ui.gallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.MyClasses.Storage
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.FragmentGalleryBinding
import com.example.dermaai_android_140.ui.slideshow.SlideshowFragment

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }


        //

        val storage = Storage()
        val images = storage.retrieveImagesFromStorage(requireActivity())
        val imageContainer = binding.imageContainer

        imageContainer.removeAllViews()

        for(image in images)
        {

            val imageView = ImageView(requireContext())

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            
            layoutParams.setMargins(12, 12, 12, 12)
            imageView.layoutParams = layoutParams

            imageView.setImageBitmap(image)
            imageContainer.addView(imageView)
        }

        //


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}