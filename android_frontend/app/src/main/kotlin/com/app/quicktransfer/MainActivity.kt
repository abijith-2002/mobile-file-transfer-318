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
import com.app.quicktransfer.data.local.InMemoryProfileDao

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room database and repository; fall back to in-memory if Room init fails
        val profileRepository = try {
            val database = AppDatabase.getInstance(applicationContext)
            ProfileRepository(database.profileDao())
        } catch (t: Throwable) {
            // Fallback ensures the app can start even if Room is not set up (e.g., missing codegen)
            ProfileRepository(InMemoryProfileDao())
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
