package com.example.did_holder_app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.did_holder_app.navigation.Screens
import com.example.did_holder_app.ui.theme.DID_Holder_AppTheme
import kotlinx.coroutines.delay
import timber.log.Timber

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*init timber*/
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        setContent {
            DID_Holder_AppTheme {
                val navController = rememberNavController()
                SetupAppNavigation(navController = navController)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupAppNavigation(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.name
    ) {
        composable(Screens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }
        composable(Screens.MainScreen.name) {
            MainScreen()
        }
    }

}

@Composable
fun SplashScreen(navController: NavHostController) {
    LocalContext.current
//    val scale = remember {
//        Animatable(0f)
//    }

    LaunchedEffect(key1 = true, block = {
//        scale.animateTo(
//            targetValue = 3f,
//            animationSpec = tween(
//                durationMillis = 1000,
//                delayMillis = 1000
//            )
//        )
        delay(1000L)
        navController.popBackStack()
        navController.navigate(Screens.MainScreen.name)
    })

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.sejong_splash),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxSize()
//                    .scale(scale.value)
                    .padding(16.dp)
            )
        }
    }
}

