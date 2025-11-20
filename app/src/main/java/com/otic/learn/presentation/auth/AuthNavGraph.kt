package com.otic.learn.presentation.auth

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.otic.learn.core.navigation.Routes
import com.otic.learn.presentation.auth.login.LoginScreen
import com.otic.learn.presentation.auth.register.RegisterScreen

@Composable
fun AuthNavGraph(parent: NavHostController) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { parent.navigate(Routes.MAIN) { popUpTo(Routes.AUTH) { inclusive = true } } },
                onRegisterClick = { nav.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { parent.navigate(Routes.MAIN) { popUpTo(Routes.AUTH) { inclusive = true } } },
                onBack = { nav.popBackStack() }
            )
        }
    }
}
