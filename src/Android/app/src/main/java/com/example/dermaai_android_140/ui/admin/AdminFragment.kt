package com.example.dermaai_android_140.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.FragmentAdminBinding
import com.example.dermaai_android_140.myClasses.InputEpochReshapeDialog
import com.example.dermaai_android_140.myClasses.ModelSelectionDialog
import com.example.dermaai_android_140.myClasses.ModelTrainer
import com.example.dermaai_android_140.myClasses.Retrain
import com.example.dermaai_android_140.myClasses.RetrainAll

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private val adminViewModel: AdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adminViewModel.setCurrentUser()

        binding.retrainAllBtn.setOnClickListener {
            if (adminViewModel.getCurrentUser()?.isAdmin == true) {
                retrainAll()
            } else {
                showToast("You are not an Admin!")
            }
        }

        binding.retrainBtn.setOnClickListener {
            if (adminViewModel.getCurrentUser()?.isAdmin == true) {
                retrain()
            } else {
                showToast("You are not an Admin!")
            }
        }

        adminViewModel.responseString.observe(viewLifecycleOwner) { responseString ->
            showToast(responseString!!)
        }

        adminViewModel.models.observe(viewLifecycleOwner) { modelTrainer ->
            modelTrainer?.let {
                showModelSelectionDialog(it)
            }
        }
    }

    private fun retrainAll() {
        val url = getString(R.string.main) +
                getString(R.string.model_controller_gateway) +
                getString(R.string.retrainAll_gateway)

        val model = RetrainAll(
            adminViewModel.getCurrentUser()?.email ?: "",
            adminViewModel.getCurrentUser()?.password ?: "",
            0,  // You might want to collect these values from input
            0
        )

        adminViewModel.retrainAll(url, model)
    }

    private fun retrain() {
        getModels()
    }

    private fun getModels() {
        val url = getString(R.string.main) +
                getString(R.string.model_controller_gateway) +
                getString(R.string.getModels_gateway)
        adminViewModel.getModels(url)
    }

    private fun showModelSelectionDialog(modelTrainer: ModelTrainer) {
        ModelSelectionDialog(
            requireContext(),
            modelTrainer.ModelTrainerPyTorch,
            modelTrainer.ModelTrainerSKLearn,
            modelTrainer.ModelTrainerTensorFlow
        ) { framework, model ->
            handleModelSelection(framework, model, modelTrainer)
        }.show()
    }

    private fun handleModelSelection(
        framework: String,
        model: String,
        modelTrainer: ModelTrainer
    ) {
        val frameworkToSend = when (framework) {
            "PyTorch" -> "ModelTrainerPyTorch"
            "Scikit-Learn" -> "ModelTrainerSKLearn"
            "TensorFlow" -> "ModelTrainerTensorFlow"
            else -> return
        }

        val index = when (framework) {
            "PyTorch" -> modelTrainer.ModelTrainerPyTorch.indexOf(model)
            "Scikit-Learn" -> modelTrainer.ModelTrainerSKLearn.indexOf(model)
            "TensorFlow" -> modelTrainer.ModelTrainerTensorFlow.indexOf(model)
            else -> null
        } ?: return

        showEpochReshapeInputDialog { epochs, reshapeSize ->
            val url = getString(R.string.main) +
                    getString(R.string.model_controller_gateway) +
                    getString(R.string.retrain_gateway)

            val retrainModel = Retrain(
                adminViewModel.getCurrentUser()?.email ?: "",
                adminViewModel.getCurrentUser()?.password ?: "",
                frameworkToSend,
                index,
                epochs,
                reshapeSize
            )

            adminViewModel.retrain(url, retrainModel)
        }
    }

    private fun showEpochReshapeInputDialog(
        onInputSubmitted: (epochs: Int, reshapeSize: Int) -> Unit
    ) {
        InputEpochReshapeDialog(requireContext()) { epochs, reshapeSize ->
            if (epochs > 0 && reshapeSize > 0) {
                onInputSubmitted(epochs, reshapeSize)
            } else {
                showToast("Please enter valid positive numbers")
            }
        }.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}