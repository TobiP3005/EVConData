package com.example.evcondata

import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.evcondata.data.auth.LoginDataSource
import com.example.evcondata.data.auth.LoginRepository
import com.example.evcondata.data.auth.UserPreferencesRepository
import com.example.evcondata.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository
    @Inject lateinit var loginRepository: LoginRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_my_car, R.id.myConsumptionFragment, R.id.mapsFragment, R.id.carsListFragment, R.id.publicConsumptionFragment
            ), drawerLayout
        )
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

            loginRepository.logout()

            Toast.makeText(applicationContext, "User logged out", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}