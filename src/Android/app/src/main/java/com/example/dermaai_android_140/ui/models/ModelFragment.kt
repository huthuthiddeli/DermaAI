package com.example.dermaai_android_140.ui.models

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.FragmentModelBinding
import com.example.dermaai_android_140.databinding.FragmentPhotoBinding
import com.example.dermaai_android_140.myClasses.Group
import com.example.dermaai_android_140.myClasses.Model
import com.example.dermaai_android_140.ui.photo.PhotoViewModel
import com.google.android.material.card.MaterialCardView

class ModelFragment : Fragment() {


    private var _binding: FragmentModelBinding? = null


    private lateinit var modelViewModel: ModelViewModel
    private lateinit var mainContainer: LinearLayout
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        modelViewModel = ViewModelProvider(this)[ModelViewModel::class.java]
        _binding = FragmentModelBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mainContainer = root.findViewById(R.id.mainContainer)

        getModelsDescription()

        setUpObserver()

        return root
    }

    private fun setUpObserver()
    {
        modelViewModel.modelsInfo.observe(viewLifecycleOwner) { response ->
            response?.groups?.let { groups ->
                fillModelView(groups)
            }
        }

        modelViewModel.message.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun getModelsDescription()
    {
        val url = getString(R.string.main) + getString(R.string.model_controller_gateway) + getString(R.string.getModelDescription_gateway)

        modelViewModel = ViewModelProvider(this)[ModelViewModel::class.java]

        modelViewModel.getModelsDescription(url)

    }


    private fun fillModelView(groups: List<Group>) {
        mainContainer.removeAllViews()

        for (group in groups) {
            // Create group container
            val groupContainer = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 32.dpToPx())
                }
            }

            // Add group title
            TextView(requireContext()).apply {
                text = group.group
                setTextAppearance(R.style.Theme_Material3_Dark)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 8.dpToPx())
                }
                groupContainer.addView(this)
            }

            // Add group description
            TextView(requireContext()).apply {
                text = group.description
                setTextAppearance(R.style.Theme_Material3_Dark)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16.dpToPx())
                }
                groupContainer.addView(this)
            }

            // Create models horizontal container
            val modelsContainer = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Add models
            for (model in group.models) {
                
                createModelCard(model).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        280.dpToPx(),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 16.dpToPx(), 0)
                    }
                    modelsContainer.addView(this)
                }
            }


            // Add horizontal scroll for models
            val scrollView = HorizontalScrollView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                addView(modelsContainer)
            }

            groupContainer.addView(scrollView)
            mainContainer.addView(groupContainer)
        }
    }

    private fun createModelCard(model: Model): MaterialCardView {
        return MaterialCardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            radius = 8.dpToPx().toFloat()
            elevation = 4.dpToPx().toFloat()
            setContentPadding(16.dpToPx(), 16.dpToPx(), 16.dpToPx(), 16.dpToPx())

            val cardContent = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Model name
            TextView(requireContext()).apply {
                text = model.name
                setTextAppearance(R.style.Theme_Material3_Dark)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                cardContent.addView(this)
            }

            // Model description
            TextView(requireContext()).apply {
                text = model.description
                setTextAppearance(R.style.Theme_Material3_Dark)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8.dpToPx(), 0, 0)
                }
                cardContent.addView(this)
            }

            addView(cardContent)
        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}