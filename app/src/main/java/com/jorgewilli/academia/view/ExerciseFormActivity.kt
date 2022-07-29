package com.jorgewilli.academia.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jorgewilli.academia.R
import com.jorgewilli.academia.databinding.ActivityExerciseFormBinding
import com.jorgewilli.academia.service.constants.AcademiaConstants
import com.jorgewilli.academia.service.model.ExerciseModel
import com.jorgewilli.academia.viewmodel.ExerciseFormViewModel
import java.time.LocalDateTime

class ExerciseFormActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityExerciseFormBinding
    private lateinit var mViewModel: ExerciseFormViewModel
    private var mExerciseId = ""
    private var mExerciseknome = 0

    private val register = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { image: Bitmap? ->
        image?.let {
            binding.imageView.setImageBitmap(image)
            mViewModel.getImageUri(this,image)?.let { it1 -> mViewModel.setUri(it1) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExerciseFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mViewModel = ViewModelProvider(this).get(ExerciseFormViewModel::class.java)

        // Inicializa eventos
        listeners()
        observe()

        // Carrega dados passados para a activity
        loadDataFromActivity()

        binding.progressCircular.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 11 && data != null) {
                data.data?.let { mViewModel.setUri(it) }
                binding.imageView.setImageURI(mViewModel.uriImagem.value)

            }
        }
    }

    override fun onClick(v: View) {
        when(v){
            binding.buttonSave -> handleSave()
            binding.buttonDelete -> handleDeleteImage()
            binding.buttonSelectGallery -> handleSelectGallery()
            binding.buttonTakePicture -> handleTakePicture()
        }
    }

    private fun listeners() {
        binding.buttonSave.setOnClickListener(this)
        binding.buttonDelete.setOnClickListener(this)
        binding.buttonSelectGallery.setOnClickListener(this)
        binding.buttonTakePicture.setOnClickListener(this)
    }

    private fun observe() {
        // Carregamento de tarefa
        mViewModel.exercise.observe(this) {

            // Caso ocorra algum erro no carregamento
            if (it == null) {
                // toast(applicationContext.getString(R.string.ERROR_LOAD_TASK))
                finish()
            } else {
                binding.editDescription.setText(it.observacoes)
                mViewModel.setUri(it.imagem.toUri())
            }
        }

        mViewModel.validation.observe(this) {
            if (it.success()) {
                if (mExerciseId == "") {
                    toast(applicationContext.getString(R.string.exercise_created))
                } else {
                    toast(applicationContext.getString(R.string.exercise_updated))
                }
                finish()
            } else {
                toast(it.failure())
            }
        }

        mViewModel.progressBar.observe(this){
            if (it){
                binding.progressCircular.visibility = View.VISIBLE
            }else{
                binding.progressCircular.visibility = View.GONE
            }
        }

        mViewModel.image.observe(this){
            binding.imageView.setImageBitmap(it)
        }

        mViewModel.uriImagem.observe(this){
            Glide.with(this).asBitmap().load(it).listener(object :
                RequestListener<Bitmap> {

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    binding.imageView.setImageBitmap(resource)
                    return true
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    return false
                }

            }).into(binding.imageView)
        }
    }

    private fun loadDataFromActivity() {
        val bundle = intent.extras;
        if (bundle != null) {
            mExerciseId = bundle.getString(AcademiaConstants.BUNDLE.EXERCISEID, "")
            mExerciseknome = bundle.getInt(AcademiaConstants.BUNDLE.TRAININGID, 0)

            // Carrega tarefa
            if (this.mExerciseId != "") {
                binding.buttonSave.setText(R.string.update_training)
                mViewModel.load(mExerciseId)
            }
        }
    }

    /**
     * Trata click
     */
    private fun handleSave() {
        val exercise = ExerciseModel().apply {
            this.key = mExerciseId
            this.nome = mExerciseknome
            this.imagem = mViewModel.uriImagem.value.toString()
            this.observacoes = binding.editDescription.text.toString()
        }

        // Envia informação para ViewModel
        mViewModel.save(exercise)
    }

    private fun handleDeleteImage(){
        mViewModel.deleteImage()
    }

    private fun handleSelectGallery(){
        mViewModel.getImageGallery(binding.imageView,this)
        //mViewModel.downloadImage(binding.imageView)
    }

    private fun handleTakePicture(){
        mViewModel.getImageCam( register, this)
    }

    private fun toast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}