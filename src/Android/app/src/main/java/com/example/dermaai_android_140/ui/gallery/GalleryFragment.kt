package com.example.dermaai_android_140.ui.gallery

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.MainActivity
import com.example.dermaai_android_140.MyClasses.Storage
import com.example.dermaai_android_140.databinding.FragmentGalleryBinding
import com.example.dermaai_android_140.ui.result.ResultActivity

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
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

        val storage = Storage()
        val images = storage.retrieveImagesFromStorage(requireActivity(), true)
        val imageContainer = binding.imageContainer

        imageContainer.removeAllViews()


        for (image in images) {

            val bitmap = BitmapFactory.decodeFile(image.absolutePath)
            var imageName = image.name

            // horizontal Container config
            val horizontalContainer = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                setPadding(12, 12, 12, 12)



                setOnClickListener {

                    val intent = Intent(requireContext(), ResultActivity::class.java)
                    startActivity(intent)

                    //Toast.makeText(context, "Container clicked", Toast.LENGTH_LONG).show()
                }
            }

            // image View config
            val imageView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setImageBitmap(bitmap)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            // add Text
            val textLabel = TextView(requireContext()).apply {
                text = imageName
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(12, 0, 0, 0)
            }

            // Add to Views
            horizontalContainer.addView(imageView)
            horizontalContainer.addView(textLabel)
            imageContainer.addView(horizontalContainer)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}