package com.jorgewilli.academia.view.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jorgewilli.academia.R
import com.jorgewilli.academia.databinding.FragmentHomeBinding
import com.jorgewilli.academia.service.constants.AcademiaConstants
import com.jorgewilli.academia.service.listener.TrainingListener
import com.jorgewilli.academia.view.ExerciseActivity
import com.jorgewilli.academia.view.TrainingFormActivity
import com.jorgewilli.academia.view.adapter.TrainingAdapter
import com.jorgewilli.academia.viewmodel.HomeViewModel

class TrainingFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var mViewModel: HomeViewModel


    private lateinit var mListener: TrainingListener
    private val mAdapter = TrainingAdapter()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val recycler = root.findViewById<RecyclerView>(R.id.recycler_all_tasks)
        val recycler = _binding!!.recyclerAllTraining
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = mAdapter

        mListener = object : TrainingListener {
            override fun onListClick(id: Int) {
                val intent = Intent(context, ExerciseActivity::class.java)
                val bundle = Bundle()
                bundle.putInt(AcademiaConstants.BUNDLE.TRAININGID, id)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onDeleteClick(id: String) {
                mViewModel.deleteTask(id)
            }

            override fun onEditeClick(key: String) {
                val intent = Intent(context, TrainingFormActivity::class.java)
                val bundle = Bundle()
                bundle.putString(AcademiaConstants.BUNDLE.TRAININGID, key)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }


        observe()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        mAdapter.attachListener(mListener)
        mViewModel.list()
    }

    private fun observe() {
        mViewModel.trainingList.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                mAdapter.updateList(it)
            }
        })

        mViewModel.validation.observe(viewLifecycleOwner, Observer {
            if (it.success()) {
                Toast.makeText(context, R.string.training_removed, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.failure(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}