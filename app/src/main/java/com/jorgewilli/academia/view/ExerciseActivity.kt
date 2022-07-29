package com.jorgewilli.academia.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jorgewilli.academia.R
import com.jorgewilli.academia.databinding.ActivityExerciseBinding
import com.jorgewilli.academia.service.constants.AcademiaConstants
import com.jorgewilli.academia.service.listener.ExerciseListener
import com.jorgewilli.academia.view.adapter.ExerciseAdapter
import com.jorgewilli.academia.viewmodel.ExerciseViewModel

class ExerciseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExerciseBinding
    private lateinit var mViewModel: ExerciseViewModel

    private lateinit var mListener: ExerciseListener
    private val mAdapter = ExerciseAdapter()
    private var mTrainingId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mViewModel = ViewModelProvider(this).get(ExerciseViewModel::class.java)

        val recycler = binding.recyclerAllExercise
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = mAdapter

        mListener = object : ExerciseListener {
            override fun onListClick(id: String, nome: Int) {
                val intent = Intent(applicationContext, ExerciseFormActivity::class.java)
                val bundle = Bundle()
                bundle.putString(AcademiaConstants.BUNDLE.EXERCISEID, id)
                bundle.putInt(AcademiaConstants.BUNDLE.TRAININGID, nome)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onDeleteClick(id: String) {
                mViewModel.deleteTask(id)
            }
        }

        observe()

        loadDataFromActivity()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId){
            R.id.action_settings -> {
                val intent = Intent(applicationContext, ExerciseFormActivity::class.java)
                val bundle = Bundle()
                bundle.putInt(AcademiaConstants.BUNDLE.TRAININGID, mTrainingId)
                intent.putExtras(bundle)
                startActivity(intent)
                //startActivity(Intent(this, ExerciseFormActivity::class.java))
            }
        }

        return true
    }

    override fun onResume() {
        super.onResume()
        mAdapter.attachListener(mListener)
        mViewModel.list()
    }

    private fun loadDataFromActivity() {
        val bundle = intent.extras;
        if (bundle != null) {
            mTrainingId = bundle.getInt(AcademiaConstants.BUNDLE.TRAININGID, 0)

            // Carrega tarefa
            if (this.mTrainingId != 0) {
                //button_save.setText(R.string.update_task)
                mViewModel.load(mTrainingId)
            }
        }
    }

    private fun observe() {
        mViewModel.exerciseList.observe(this, Observer {
            if (it != null) {
                mAdapter.updateList(it)
            }
        })

        mViewModel.validation.observe(this, Observer {
            if (it.success()) {
                Toast.makeText(this, R.string.exercise_removed, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, it.failure(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}