package com.example.evcondata

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.evcondata.data.DatabaseManager
import com.example.evcondata.data.auth.UserPreferencesRepository
import com.example.evcondata.databinding.ActivityMainBinding
import com.example.evcondata.ui.auth.login.LoginViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DrawerLocker {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository
    @Inject lateinit var databaseManager: DatabaseManager
    lateinit var loginViewModel: LoginViewModel

    lateinit var navController :NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_my_car, R.id.myConsumptionFragment, R.id.mapsFragment, R.id.carsListFragment, R.id.publicConsumptionFragment
            ), drawerLayout
        )

        setDrawerEnabled(false)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val loginBtn = findViewById<Button>(R.id.login_button)
        loginBtn.setOnClickListener{
            navController.navigateUp()
            navController.navigate(R.id.loginFragment)
            drawerLayout.closeDrawers()
        }

        val logoutBtn = findViewById<Button>(R.id.logout_button)
        logoutBtn.setOnClickListener{
            logoutBtn.isVisible = false
            loginBtn.isVisible = true
            navController.navigateUp()
            navController.navigate(R.id.loginFragment)
            drawerLayout.closeDrawers()

            loginViewModel.logout()
            Toast.makeText(applicationContext, "User logged out", Toast.LENGTH_LONG).show()

            restartApp()
        }
    }

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        this.startActivity(intent)
        (this as Activity).finish()

        Runtime.getRuntime().exit(0)
    }

    override fun onRestart() {
        super.onRestart()
        databaseManager.checkReplicator()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun setDrawerEnabled(enabled: Boolean) {
        val lockMode = if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else LOCK_MODE_LOCKED_CLOSED
        drawer_layout.setDrawerLockMode(lockMode)
    }
}

interface DrawerLocker {
    fun setDrawerEnabled(enabled: Boolean)
}