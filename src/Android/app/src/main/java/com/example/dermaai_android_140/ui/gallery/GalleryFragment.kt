package com.example.dermaai_android_140.ui.gallery

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.ui.camera.CameraViewModel
import com.example.dermaai_android_140.ui.result.ResultActivity
import java.io.File

class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var imageContainer: LinearLayout
    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        galleryViewModel = ViewModelProvider(this)[GalleryViewModel::class.java]


        val root = inflater.inflate(R.layout.fragment_gallery, container, false)

        // Find views by ID
        textView = root.findViewById(R.id.text_gallery)
        imageContainer = root.findViewById(R.id.imageContainer)

        val filesDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        galleryViewModel.loadImages(filesDir)

        galleryViewModel.images.observe(viewLifecycleOwner) { images ->
            fillView(images)
        }

        return root
    }

    private fun fillView(images: List<File>) {
        imageContainer.removeAllViews()

        for (image in images) {
            val bitmap = BitmapFactory.decodeFile(image.absolutePath)
            val imageName = image.name
            val scaledBitmap = bitmap.scale(200, 300, false)

            val matrix = Matrix().apply {
                postRotate(270f)
            }

            // Horizontal container config
            val horizontalContainer = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(12, 12, 12, 12)

                setOnClickListener {
                    val intent = Intent(requireContext(), ResultActivity::class.java).apply {
                        putExtra("EXTRA_IMAGE_PATH", image.absolutePath)
                    }
                    startActivity(intent)
                }
            }

            // ImageView config
            val imageView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setImageBitmap(scaledBitmap)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            // Add text
            val textLabel = TextView(requireContext()).apply {
                text = imageName
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(12, 0, 0, 0)
            }

            // Add to views
            horizontalContainer.addView(imageView)
            horizontalContainer.addView(textLabel)
            imageContainer.addView(horizontalContainer)
        }
    }
}