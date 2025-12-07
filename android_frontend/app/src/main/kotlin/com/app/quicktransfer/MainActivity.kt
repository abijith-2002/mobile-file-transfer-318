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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.quicktransfer.ui.home.HomeScreen
import com.app.quicktransfer.ui.theme.AppTheme
import androidx.compose.foundation.layout.fillMaxSize
import com.app.quicktransfer.data.ProfileRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SharedPreferences-backed repository for profiles
        val profileRepository = ProfileRepository.getInstance(applicationContext)

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
                onOpenProfile = { profile ->
                    navController.navigate("edit_profile/${profile.id}")
                }
            )
        }
        // Add profile screen
        composable("add_profile") {
            com.app.quicktransfer.ui.home.NewProfileScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }
        // Edit profile screen with pre-populated data
        composable(
            route = "edit_profile/{profileId}",
            arguments = listOf(navArgument("profileId") { type = NavType.StringType })
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getString("profileId")
            com.app.quicktransfer.ui.home.NewProfileScreen(
                repository = repository,
                profileId = profileId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
