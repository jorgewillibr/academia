package com.jorgewilli.academia.view

import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.jorgewilli.academia.R
import com.jorgewilli.academia.databinding.ActivityTrainingFormBinding
import com.jorgewilli.academia.service.constants.AcademiaConstants
import com.jorgewilli.academia.service.model.TrainingModel
import com.jorgewilli.academia.viewmodel.TrainingFormViewModel
import java.util.*

class TrainingFormActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private lateinit var binding: ActivityTrainingFormBinding
    private lateinit var mViewModel: TrainingFormViewModel
    private val mDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private var mTrainingkey = ""
    private var mTrainingid = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrainingFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mViewModel = ViewModelProvider(this).get(TrainingFormViewModel::class.java)

        // Inicializa eventos
        listeners()
        observe()

        // Carrega dados passados para a activity
        loadDataFromActivity()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        val strDate: String = mDateFormat.format(calendar.time)
        //binding.buttonDate.setText(strDate)
        mViewModel.setDate(strDate)
    }

    override fun onClick(v: View) {
//        val id = v.id
//        if (id == R.id.button_date) {
//            showDateDialog()
//        } else if (id == R.id.button_save) {
//            handleSave()
//        }

        if (v == binding.buttonDate) {
            showDateDialog()
        }else if (v == binding.buttonSave){
            handleSave()
        }
    }

    private fun listeners() {
        binding.buttonDate.setOnClickListener(this)
        binding.buttonSave.setOnClickListener(this)
    }

    private fun observe() {
        // Carregamento de tarefa
        mViewModel.training.observe(this) {

            // Caso ocorra algum erro no carregamento
            if (it == null) {
                // toast(applicationContext.getString(R.string.ERROR_LOAD_TASK))
                finish()
            } else {
                binding.editDescription.setText(it.descricao)
                mTrainingid = it.nome

                val date = SimpleDateFormat("yyyy-MM-dd").parse(it.data)
                binding.buttonDate.setText(mDateFormat.format(date))
            }
        }

        mViewModel.validation.observe(this) {
            if (it.success()) {
                if (mTrainingkey == "") {
                    toast(applicationContext.getString(R.string.training_created))
                } else {
                    toast(applicationContext.getString(R.string.training_updated))
                }
                finish()
            } else {
                toast(it.failure())
            }
        }

        mViewModel.dateButton.observe(this){
            binding.buttonDate.setText(it)
            binding.buttonDate.setTextColor(Color.BLACK);
        }
    }

    private fun loadDataFromActivity() {
        val bundle = intent.extras;
        if (bundle != null) {
            mTrainingkey = bundle.getString(AcademiaConstants.BUNDLE.TRAININGID, "")

            // Carrega tarefa
            if (this.mTrainingkey != "") {
                binding.buttonSave.setText(R.string.update_training)
                mViewModel.load(mTrainingkey)
            }
        }
    }

    /**
     * Trata click
     */
    private fun handleSave() {
        val task = TrainingModel().apply {
            this.key = mTrainingkey
            this.nome = mTrainingid
            this.descricao = binding.editDescription.text.toString()
            this.data = binding.buttonDate.text.toString()
        }

        // Envia informação para ViewModel
        mViewModel.save(task)
    }

    /**
     * Mostra datepicker de seleção
     */
    private fun showDateDialog() {
        val c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val day: Int = c.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, this, year, month, day).show()
    }

    private fun toast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}