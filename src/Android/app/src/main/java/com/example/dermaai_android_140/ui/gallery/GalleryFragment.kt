package com.example.dermaai_android_140.ui.gallery

import android.content.Intent
import android.graphics.BitmapFactory
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
import com.example.dermaai_android_140.databinding.FragmentGalleryBinding
import com.example.dermaai_android_140.ui.result.ResultActivity
import java.io.File

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val galleryViewModel = ViewModelProvider(this)[GalleryViewModel::class.java]

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)


        val root: View = binding.root

        val textView: TextView = binding.textGallery

        val filesDir : File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)


        galleryViewModel.loadImages(filesDir)

        galleryViewModel.images.observe(viewLifecycleOwner) { images ->
            fillView(images)
        }

        return root
        
    }


    // TODO: coroutine

    private fun fillView(images: List<File>) {

        val imageContainer = binding.imageContainer
        imageContainer.removeAllViews()


        for (image in images) {

            val bitmap = BitmapFactory.decodeFile(image.absolutePath)
            
            val imageName = image.name

            // Thumbnail
            val scaledBitmap = bitmap.scale(300, 200, false)

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
                        putExtra("EXTRA_RESULT", image.absolutePath)
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}