package com.example.dermaai_android_140.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.databinding.FragmentAdminBinding
import com.example.dermaai_android_140.myClasses.InputEpochReshapeDialog
import com.example.dermaai_android_140.myClasses.InputReshapeDialog
import com.example.dermaai_android_140.myClasses.ModelSelectionDialog
import com.example.dermaai_android_140.myClasses.ModelTrainer
import com.example.dermaai_android_140.myClasses.Report
import com.example.dermaai_android_140.myClasses.ReportAll
import com.example.dermaai_android_140.myClasses.Retrain
import com.example.dermaai_android_140.myClasses.RetrainAll
import com.example.dermaai_android_140.myClasses.Storage

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var adminViewModel: AdminViewModel
    private lateinit var decision : String

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

        val retrainAllBtn = view.findViewById<Button>(R.id.retrainAllBtn)
        val retrainBtn = view.findViewById<Button>(R.id.retrainBtn)

        val allReportsBtn = view.findViewById<Button>(R.id.allReportsBtn)
        val oneReportBtn = view.findViewById<Button>(R.id.oneReportBtn)

        adminViewModel = ViewModelProvider(this)[AdminViewModel::class.java]
        adminViewModel.setCurrentUser()

        adminViewModel.message.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }



        allReportsBtn.setOnClickListener {
            if (adminViewModel.getCurrentUser()?.isAdmin == true) {
                getAllReports()
            } else {
                showToast("You are not an Admin!")
            }
        }

        oneReportBtn.setOnClickListener {
            if (adminViewModel.getCurrentUser()?.isAdmin == true) {
                decision = "report"
                getModels()
            } else {
                showToast("You are not an Admin!")
            }
        }


        retrainAllBtn.setOnClickListener {
            if (adminViewModel.getCurrentUser()?.isAdmin == true) {
                showEpochReshapeInputDialog { epochs, reshapeSize ->
                    retrainAll(epochs, reshapeSize)
                }
                //showEpochReshapeInputDialog()

                //showModelSelectionDialog()
            //retrainAll()
            } else {
                showToast("You are not an Admin!")
            }
        }
        
        retrainBtn.setOnClickListener {
            val user = adminViewModel.getCurrentUser()
            if (adminViewModel.getCurrentUser()?.isAdmin == true) {
                decision = "retrain"
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
        

        adminViewModel.report.observe(viewLifecycleOwner){report ->
            if(report != null)
            {
                showToast("Report received!")
                Storage.createReportFile(requireActivity(), report.toString(), adminViewModel.getCurrentUser()!!.email)
            }else
            {
                showToast("Error receiving report")
            }
        }

        adminViewModel.message.observe(viewLifecycleOwner){message ->
            showToast(message.toString())
        }

        adminViewModel.currentUser.observe(viewLifecycleOwner){user ->

            if(user != null)
            {
                observeReports(adminViewModel.getCurrentUser()!!.email)
            }

        }


    }

    fun observeReports(username: String) {

        adminViewModel.report.observe(viewLifecycleOwner) { reportJson ->
            reportJson?.let {
                Storage.createReportFile(requireActivity(), it, username)
            }
            Toast.makeText(context, "Successfully created Report!", Toast.LENGTH_LONG).show()
        }

        adminViewModel.allReports.observe(viewLifecycleOwner) { allReportsJson ->
            allReportsJson?.let {
                Storage.createReportFile(requireActivity(), it, username)
            }
            Toast.makeText(context, "Successfully created Report!", Toast.LENGTH_LONG).show()
        }
    }

    private fun getAllReports()
    {
        val url = getString(R.string.main) +
                getString(R.string.model_controller_gateway) +
                getString(R.string.getAllReports_gateway)



        if(adminViewModel.getCurrentUser() == null)
        {
            adminViewModel.setCurrentUser()
        }
        else{

            val currentUser = adminViewModel.getCurrentUser()!!

            showReshapeSizeInputDialog { reshapeSize ->

                val model = ReportAll(currentUser.email, currentUser.password,reshapeSize)
                adminViewModel.getAllReports(url,model)
            }

        }
    }

    private fun getOneReport(framework : String, index : Int)
    {
        val url = getString(R.string.main) +
                getString(R.string.model_controller_gateway) +
                getString(R.string.getReport_gateway)

        showReshapeSizeInputDialog { reshapeSize ->

            val currentUser = adminViewModel.getCurrentUser()!!

            val model = Report(currentUser.email,currentUser.password,framework,index,reshapeSize)

            adminViewModel.getOneReport(url, model)
        }
    }



    private fun showReshapeSizeInputDialog(
        onReshapeSubmitted: (reshapeSize: Int) -> Unit
    ) {
        InputReshapeDialog(requireContext()) { reshapeSize ->
            onReshapeSubmitted(reshapeSize)
        }.show()
    }

    private fun retrainAll(epochs : Int, reshapeSize: Int) {
        val url = getString(R.string.main) +
                getString(R.string.model_controller_gateway) +
                getString(R.string.retrainAll_gateway)

        val model = RetrainAll(
            adminViewModel.getCurrentUser()?.email ?: "",
            adminViewModel.getCurrentUser()?.password ?: "",
            epochs,
            reshapeSize
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

        //showEpochReshapeInputDialog { epochs, reshapeSize ->  }

    }

    private fun handleModelSelection(
        framework: String,
        model: String,
        modelTrainer: ModelTrainer
    ) {
        val frameworkToSend = if (framework == "PyTorch") {
            "ModelTrainerPyTorch"
        } else if (framework == "Scikit-Learn") {
            "ModelTrainerSKLearn"
        } else if (framework == "TensorFlow") {
            "ModelTrainerTensorFlow"
        } else {
            return
        }

        val index = if (framework == "PyTorch") {
            modelTrainer.ModelTrainerPyTorch.indexOf(model)
        } else if (framework == "Scikit-Learn") {
            modelTrainer.ModelTrainerSKLearn.indexOf(model)
        } else if (framework == "TensorFlow") {
            modelTrainer.ModelTrainerTensorFlow.indexOf(model)
        } else {
            null
        } ?: return


        if(decision.equals("retrain"))
        {
            showEpochReshapeInputDialog { epochs, reshapeSize ->
                val url = getString(R.string.main) +
                        getString(R.string.model_controller_gateway) +
                        getString(R.string.retrain_gateway)

                val retrainModel = Retrain(
                    adminViewModel.getCurrentUser()?.email!!,
                    adminViewModel.getCurrentUser()?.password!!,
                    frameworkToSend,
                    index,
                    epochs,
                    reshapeSize
                )
                adminViewModel.retrain(url, retrainModel)
            }
        }else if(decision.equals("report")){
            getOneReport(frameworkToSend, index)
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