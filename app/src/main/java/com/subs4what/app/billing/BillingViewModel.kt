package com.subs4what.app.billing

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.subs4what.app.data.MemberRepository
import com.subs4what.app.data.PreferencesManager
import com.subs4what.app.firebase.FirebaseService
import com.subs4what.app.firebase.MemberData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class SubscriptionState {
    data object Loading : SubscriptionState()
    data object NotSubscribed : SubscriptionState()
    data object Subscribing : SubscriptionState()
    data class Subscribed(val memberData: MemberData) : SubscriptionState()
    data class Error(val message: String) : SubscriptionState()
}

class BillingViewModel(application: Application) : AndroidViewModel(application) {

    private val billingManager = BillingManager(application)
    private val firebaseService = FirebaseService()
    private val preferencesManager = PreferencesManager(application)
    private val memberRepository = MemberRepository(firebaseService, preferencesManager)

    private val _state = MutableStateFlow<SubscriptionState>(SubscriptionState.Loading)
    val state: StateFlow<SubscriptionState> = _state

    private val _priceText = MutableStateFlow("")
    val priceText: StateFlow<String> = _priceText

    init {
        checkSubscription()
        observePurchases()
    }

    /**
     * Проверка при старте:
     * 1. Кэш (мгновенно)
     * 2. Google Play Billing (есть ли активная подписка?)
     * 3. Firebase (restore по purchaseToken если кэш пуст)
     */
    private fun checkSubscription() {
        viewModelScope.launch {
            try {
                // Быстрый старт из кэша
                val cached = memberRepository.getCachedMember()
                if (cached != null) {
                    _state.value = SubscriptionState.Subscribed(cached)
                }

                // Проверяем billing
                billingManager.connect()
                billingManager.querySubscription()
                _priceText.value = billingManager.getFormattedPrice()

                val existingPurchase = billingManager.checkExistingSubscription()
                if (existingPurchase != null) {
                    // Подписка активна — восстанавливаем номер
                    val memberData = memberRepository.restoreMember(existingPurchase.purchaseToken)
                    if (memberData != null) {
                        _state.value = SubscriptionState.Subscribed(memberData)
                    } else {
                        // Подписка есть, но номер не найден — создаём
                        val newMember = memberRepository.createMember(existingPurchase.purchaseToken)
                        _state.value = SubscriptionState.Subscribed(newMember)
                    }
                } else if (cached == null) {
                    _state.value = SubscriptionState.NotSubscribed
                }
            } catch (e: Exception) {
                if (_state.value is SubscriptionState.Loading) {
                    _state.value = SubscriptionState.NotSubscribed
                }
            }
        }
    }

    /**
     * Слушаем результат покупки из BillingManager.
     */
    private fun observePurchases() {
        viewModelScope.launch {
            billingManager.purchaseState.collectLatest { result ->
                when (result) {
                    is PurchaseResult.Success -> {
                        _state.value = SubscriptionState.Subscribing
                        try {
                            billingManager.acknowledgePurchase(result.purchase)
                            val memberData = memberRepository.createMember(
                                result.purchase.purchaseToken
                            )
                            _state.value = SubscriptionState.Subscribed(memberData)
                        } catch (e: Exception) {
                            _state.value = SubscriptionState.Error(
                                "Payment received but setup failed. Please restart the app."
                            )
                        }
                    }
                    is PurchaseResult.Cancelled -> {
                        if (_state.value is SubscriptionState.Subscribing) {
                            _state.value = SubscriptionState.NotSubscribed
                        }
                    }
                    is PurchaseResult.Error -> {
                        _state.value = SubscriptionState.Error(result.message)
                    }
                    PurchaseResult.Idle -> { /* no-op */ }
                }
            }
        }
    }

    fun subscribe(activity: Activity) {
        _state.value = SubscriptionState.Subscribing
        val launched = billingManager.launchBillingFlow(activity)
        if (!launched) {
            _state.value = SubscriptionState.Error("Could not start purchase flow")
        }
    }

    fun restore() {
        _state.value = SubscriptionState.Loading
        checkSubscription()
    }

    override fun onCleared() {
        super.onCleared()
        billingManager.disconnect()
    }
}
