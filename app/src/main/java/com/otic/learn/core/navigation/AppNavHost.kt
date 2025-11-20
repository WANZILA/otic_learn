//package com.otic.learn.core.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
////import com.otic.learn.presentation.auth.AuthNavGraph
////import com.otic.learn.presentation.home.HomeNavGraph
////import com.otic.learn.presentation.root.RootViewModel
//////
////import com.otic.learn.presentation.root.RootViewModel
//
//@Composable
//fun AppNavHost() {
//    val navController = rememberNavController()
//    val rootVm: RootViewModel = hiltViewModel()
//    val user by rootVm.user.collectAsState()
//
//    val start = if (user == null) Routes.AUTH else Routes.MAIN
//
//    NavHost(navController, startDestination = start) {
//        composable(Routes.AUTH) { AuthNavGraph(navController) }
//        composable(Routes.MAIN) { HomeNavGraph(navController) }
//    }
//}
