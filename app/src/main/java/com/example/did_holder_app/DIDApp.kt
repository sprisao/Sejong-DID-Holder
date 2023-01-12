package com.example.did_holder_app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.did_holder_app.ui.DIDScreen
import com.example.did_holder_app.ui.QRScreen
import com.example.did_holder_app.ui.VCScreen
import com.example.did_holder_app.util.Constants


sealed class BottomNavItem(val screenRoute: String, val title: Int, val icon: Int) {

    object DID :
        BottomNavItem(Constants.DID, R.string.did_screen, R.drawable.baseline_did_24)

    object VC : BottomNavItem(Constants.VC, R.string.vc_screen, R.drawable.baseline_vc_24)
    object QR : BottomNavItem(
        Constants.QR,
        R.string.qr_screen,
        R.drawable.baseline_qr_code_scanner_24
    )
}

@Composable
fun DIDTopBar() {
    androidx.compose.material.TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "DID Holder")
            }
        },
    )
}

@Composable
fun DidApp() {
    val navController = rememberNavController()
    // add top appbar with title "Holder"
    Scaffold(
        bottomBar = { DIDBottomNav(navController = navController) },
        topBar = { DIDTopBar() }) {
        Box(Modifier.padding(it))
        NavigationGraph(navController = navController)
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Constants.DID) {
        composable(Constants.DID) {
            DIDScreen()
        }
        composable(Constants.VC) {
            VCScreen()
        }
        composable(Constants.QR) {
            QRScreen()
        }
    }
}


@Composable
fun DIDBottomNav(navController: NavController) {
    val items =
        listOf<BottomNavItem>(BottomNavItem.DID, BottomNavItem.VC, BottomNavItem.QR)

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color(0xFFECEFF1),
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(icon = {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = stringResource(id = item.title),
                )
            },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.LightGray,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                    indicatorColor = Color.Red
                ),
                alwaysShowLabel = false,
                label = { Text(text = stringResource(id = item.title)) },
                selected = currentRoute == item.screenRoute,
                onClick = {
                    navController.navigate(item.screenRoute) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }
    }
}

