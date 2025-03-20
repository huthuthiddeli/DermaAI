package com.example.dermaai_android_140.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dermaai_android_140.databinding.ActivityAdminBinding
import com.example.dermaai_android_140.R
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.dermaai_android_140.myClasses.InputEpochReshapeDialog
import com.example.dermaai_android_140.myClasses.ModelSelectionDialog
import com.example.dermaai_android_140.myClasses.ModelTrainer
import com.example.dermaai_android_140.myClasses.Retrain
import com.example.dermaai_android_140.myClasses.RetrainAll

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val adminViewModel = ViewModelProvider(this).get(AdminViewModel::class.java)
        adminViewModel.setCurrentUser()

        val retrainAllBtn = findViewById<Button>(R.id.retrainAllBtn)
        val retrainBtn = findViewById<Button>(R.id.retrainBtn)

        val url = getString(R.string.main) + getString(R.string.model_controller_gateway) + getString(R.string.retrainAll_gateway)

        retrainAllBtn.setOnClickListener{
            retrainAll(adminViewModel)
        }

        retrainBtn.setOnClickListener{
            retrain(adminViewModel)
        }


        adminViewModel.models.observe(this) { modelTrainer ->

            if(modelTrainer != null)
            {
                showModelSelectionDialog(modelTrainer, adminViewModel)

                val url = getString(R.string.main) + getString(R.string.model_controller_gateway) + getString(R.string.retrain_gateway)

                val num_epoch : Int = 0
                val reshape_size : Int = 0
                val trainer_string : String = ""
                val model_int : Int = 0

                val model = Retrain(adminViewModel.currentUser.value?.email.toString(), adminViewModel.currentUser.value?.password.toString(),trainer_string, model_int, num_epoch, reshape_size)

                adminViewModel.retrain(url, model)

            }
        }


    }


    private fun retrainAll(adminViewModel : AdminViewModel)
    {
        val url = getString(R.string.main) + getString(R.string.model_controller_gateway) + getString(R.string.retrainAll_gateway)

        val num_epoch : Int = 0
        val reshape_size : Int = 0


        val model = RetrainAll(adminViewModel.currentUser.value?.email.toString(), adminViewModel.currentUser.value?.password.toString(), num_epoch, reshape_size)

        adminViewModel.retrainAll(url, model)
    }

    private fun retrain(adminViewModel : AdminViewModel){

        getModels(adminViewModel)

    }

    private fun getModels(adminViewModel: AdminViewModel) {

        val url =
            getString(R.string.main) + getString(R.string.model_controller_gateway) + getString(
                R.string.getModels_gateway
            )

        adminViewModel.getModels(url)
    }


    private fun showModelSelectionDialog(modelTrainer: ModelTrainer, adminViewModel: AdminViewModel) {
        ModelSelectionDialog(
            this,
            modelTrainer.ModelTrainerPyTorch,
            modelTrainer.ModelTrainerSKLearn,
            modelTrainer.ModelTrainerTensorFlow
        ) { framework, model ->


            var models = adminViewModel.models.value
            var index : Int? = null

            var frameworkToSend : String = ""

            if(framework.equals("PyTorch"))
            {
                frameworkToSend = "ModelTrainerPyTorch"
                for(mdl in models?.getPyTorch()!!)
                {
                    if(mdl.equals(model)) {
                        index = models.getPyTorch().indexOf(mdl)
                    }
                }
            }
            else if(framework.equals("Scikit-Learn"))
            {
                frameworkToSend = "ModelTrainerSKLearn"

                for(mdl in models?.getSKLearn()!!)
                {
                    if(mdl.equals(model)) {
                        index = models.getSKLearn().indexOf(mdl)
                    }
                }
            }
            else if(framework.equals("TensorFlow"))
            {
                frameworkToSend = "ModelTrainerTensorFlow"
                for(mdl in models?.getTensorFlow()!!)
                {
                    if(mdl.equals(model)) {
                        index = models.getTensorFlow().indexOf(mdl)
                    }
                }
            }

            // Start input dialog of epoch and reshape size
            showEpochReshapeInputDialog { epochs, reshapeSize ->
                // Now we have all parameters
                val url = getString(R.string.main) +
                        getString(R.string.model_controller_gateway) +
                        getString(R.string.retrain_gateway)

                val retrainModel = Retrain(
                    adminViewModel.getCurrentUser().email,
                    adminViewModel.getCurrentUser().password,
                    frameworkToSend,
                    index,
                    epochs,
                    reshapeSize
                )

                adminViewModel.retrain(url, retrainModel)
            }


        }.show()
    }


        fun showEpochReshapeInputDialog(
        onInputSubmitted: (epochs: Int, reshapeSize: Int) -> Unit
    ) {
        val dialog = InputEpochReshapeDialog(this) { epochs, reshapeSize ->
            if (epochs > 0 && reshapeSize > 0) {
                onInputSubmitted(epochs, reshapeSize)
            } else {
                Toast.makeText(
                    this,
                    "Please enter valid positive numbers",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        dialog.show()
    }

}