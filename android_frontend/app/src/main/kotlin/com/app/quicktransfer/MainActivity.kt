package com.app.quicktransfer

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.quicktransfer.ui.home.HomeScreen
import com.app.quicktransfer.ui.theme.AppTheme
import androidx.compose.foundation.layout.fillMaxSize
import com.app.quicktransfer.data.ProfileRepository
import com.app.quicktransfer.data.local.AppDatabase
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room database and repository using file-backed persistent storage
        val database = AppDatabase.getInstance(applicationContext)
        val profileRepository = ProfileRepository(database.profileDao())

        // Simple verification: log number of profiles persisted at launch
        lifecycleScope.launch {
            val count = database.profileDao().getAll().first().size
            Log.d("MainActivity", "Profiles in DB on launch: $count")
        }

        setContent {
            AppTheme(dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNav(repository = profileRepository)
                }
            }
        }
    }
}

@Composable
private fun AppNav(
    repository: ProfileRepository,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                repository = repository,
                onAddProfile = { navController.navigate("add_profile") },
                onOpenProfile = { /* TODO: navigate to details in future */ }
            )
        }
        // Add profile screen
        composable("add_profile") {
            com.app.quicktransfer.ui.home.NewProfileScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
