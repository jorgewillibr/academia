package com.jorgewilli.academia

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jorgewilli.academia.databinding.ActivityMainBinding
import com.jorgewilli.academia.databinding.NavHeaderMainBinding
import com.jorgewilli.academia.view.ExerciseActivity
import com.jorgewilli.academia.view.ExerciseFormActivity
import com.jorgewilli.academia.view.LoginActivity
import com.jorgewilli.academia.view.TrainingFormActivity
import com.jorgewilli.academia.viewmodel.NavHeaderViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingNav: NavHeaderMainBinding
    private lateinit var mViewModel: NavHeaderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mViewModel = ViewModelProvider(this)[NavHeaderViewModel::class.java]

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            startActivity(Intent(this, TrainingFormActivity::class.java))
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val viewHeader = binding.navView.getHeaderView(0)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_sobre, R.id.nav_sair), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        bindingNav = NavHeaderMainBinding.bind(viewHeader)
        bindingNav.imageButton.setOnClickListener { view ->
            mViewModel.doSignOut()
        }


        observe()
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun observe() {
        mViewModel.nome.observe(this) {
            bindingNav.navNomeTextView.text = it
        }
        mViewModel.email.observe(this) {
            bindingNav.navEmailTextView.text = it
        }
//        mViewModel.foto.observe(this) {
//            bindingNav.text = it
//        }

        mViewModel.login.observe(this) {
            if (it.success()) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(applicationContext, it.failure(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}