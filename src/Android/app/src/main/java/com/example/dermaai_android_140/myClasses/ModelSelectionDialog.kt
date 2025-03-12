package com.example.dermaai_android_140.myClasses

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TabHost
import com.example.dermaai_android_140.R

public class ModelSelectionDialog(
    context: Context,
    private val pytorchModels: List<String>, // Make them member variables
    private val sklearnModels: List<String>, // Make them member variables
    private val tensorflowModels: List<String>, // Make them member variables
    private val onModelSelected: (framework: String, model: String) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_dialog_model_selection)

        // Set layout parameters inside onCreate
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        setupTabs(pytorchModels, sklearnModels, tensorflowModels) // Now it's correct!
    }


    private fun setupTabs(pytorch: List<String>, sklearn: List<String>, tf: List<String>) {
        val tabHost = findViewById<TabHost>(R.id.tabHost)
        tabHost.setup()

        // Create tabs with dynamic data
        listOf(
            Triple("PyTorch", R.id.pytorch_list, pytorch),
            Triple("Scikit-Learn", R.id.sklearn_list, sklearn),
            Triple("TensorFlow", R.id.tensorflow_list, tf)
        ).forEach { (title, listId, models) ->
            tabHost.addTab(
                tabHost.newTabSpec(title).apply {
                    setIndicator(title)
                    setContent(listId)
                }
            )
            setupList(listId, models, title)
        }
    }




    private fun setupList(listViewId: Int, models: List<String>, framework: String) {
        findViewById<ListView>(listViewId).apply {
            adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, models)
            setOnItemClickListener { _, _, position, _ ->
                onModelSelected(framework, models[position])
                dismiss()
            }
        }
    }


}
