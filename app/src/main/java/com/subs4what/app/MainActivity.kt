package com.subs4what.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.subs4what.app.billing.BillingViewModel
import com.subs4what.app.billing.SubscriptionState
import com.subs4what.app.data.PreferencesManager
import com.subs4what.app.navigation.Routes
import com.subs4what.app.navigation.SubsNavGraph
import com.subs4what.app.ui.theme.Subs4WhatTheme
import com.subs4what.app.ui.theme.SurfaceDark

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Subs4WhatTheme {
                val navController = rememberNavController()
                val billingViewModel: BillingViewModel = viewModel()
                val preferencesManager = remember { PreferencesManager(this@MainActivity) }

                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    val onboarded = preferencesManager.isOnboarded()
                    val cached = preferencesManager.getMemberData()
                    startDestination = when {
                        cached != null -> Routes.NOTHING
                        onboarded -> Routes.SUBSCRIBE
                        else -> Routes.WELCOME
                    }
                    if (!onboarded) {
                        preferencesManager.setOnboarded()
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SurfaceDark),
                    color = SurfaceDark
                ) {
                    startDestination?.let { dest ->
                        SubsNavGraph(
                            navController = navController,
                            startDestination = dest,
                            billingViewModel = billingViewModel
                        )
                    }
                }
            }
        }
    }
}
