package com.nickpape.dicepokerbattleroyale

import android.os.Bundle
import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.nickpape.dicepokerbattleroyale.auth.AuthInit
import com.nickpape.dicepokerbattleroyale.databinding.ActivityMainBinding
import com.nickpape.dicepokerbattleroyale.fragments.home.HomeFragmentDirections
import com.nickpape.dicepokerbattleroyale.models.Player
import com.nickpape.dicepokerbattleroyale.view_models.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        AuthInit(mainViewModel, signInLauncher)

        mainViewModel.firebaseAuthLiveData.observe(this) {
            if (it != null) {
                mainViewModel.addOrUpdatePlayer(
                    Player(
                        it.displayName ?: it.email!!,
                        it.uid
                    )
                )
            }
        }
    }

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            mainViewModel.updateUser()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Log.d("MainActivity", "sign in failed ${result}")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val nav = findNavController(R.id.nav_host_fragment_content_main)

        return when (item.itemId) {
            R.id.action_settings -> {
                if (nav.currentDestination?.id != R.id.SettingsFragment) {
                    nav.navigate(R.id.SettingsFragment)
                }
                true
            }
            R.id.action_logout -> {
                Log.d(javaClass.simpleName, "Logout button clickedS")

                nav.clearBackStack(R.id.HomeFragment)
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        FirebaseAuth.getInstance().signOut()
                        AuthInit(mainViewModel, signInLauncher)
                    }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}