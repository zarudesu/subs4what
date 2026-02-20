package com.subs4what.app.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.subs4what.app.billing.BillingViewModel
import com.subs4what.app.billing.SubscriptionState
import com.subs4what.app.ui.screens.MemberCardScreen
import com.subs4what.app.ui.screens.NothingScreen
import com.subs4what.app.ui.screens.SubscribeScreen
import com.subs4what.app.ui.screens.WelcomeScreen

object Routes {
    const val WELCOME = "welcome"
    const val SUBSCRIBE = "subscribe"
    const val NOTHING = "nothing"
    const val MEMBER_CARD = "member_card"
}

@Composable
fun SubsNavGraph(
    navController: NavHostController,
    startDestination: String,
    billingViewModel: BillingViewModel = viewModel()
) {
    val state by billingViewModel.state.collectAsState()
    val priceText by billingViewModel.priceText.collectAsState()
    val activity = LocalContext.current as Activity

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onBeginJourney = {
                    navController.navigate(Routes.SUBSCRIBE) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SUBSCRIBE) {
            SubscribeScreen(
                subscriptionState = state,
                priceText = priceText,
                onSubscribe = { billingViewModel.subscribe(activity) },
                onRestore = { billingViewModel.restore() }
            )

            // Auto-navigate when subscribed
            LaunchedEffect(state) {
                if (state is SubscriptionState.Subscribed) {
                    navController.navigate(Routes.NOTHING) {
                        popUpTo(Routes.SUBSCRIBE) { inclusive = true }
                    }
                }
            }
        }

        composable(Routes.NOTHING) {
            val memberData = (state as? SubscriptionState.Subscribed)?.memberData
            NothingScreen(
                memberNumber = memberData?.memberNumber ?: 0,
                onViewMemberCard = {
                    navController.navigate(Routes.MEMBER_CARD)
                }
            )
        }

        composable(Routes.MEMBER_CARD) {
            val memberData = (state as? SubscriptionState.Subscribed)?.memberData
            MemberCardScreen(
                memberNumber = memberData?.memberNumber ?: 0,
                memberSince = memberData?.createdAt ?: "The Beginning",
                onBack = { navController.popBackStack() }
            )
        }
    }
}
