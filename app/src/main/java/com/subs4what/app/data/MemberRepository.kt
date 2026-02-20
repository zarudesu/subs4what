package com.subs4what.app.data

import com.subs4what.app.firebase.FirebaseService
import com.subs4what.app.firebase.MemberData

class MemberRepository(
    private val firebaseService: FirebaseService,
    private val preferencesManager: PreferencesManager
) {

    /**
     * Новая подписка: создаёт номер участника и сохраняет маппинг.
     */
    suspend fun createMember(purchaseToken: String): MemberData {
        val uid = firebaseService.ensureSignedIn()
        val memberData = firebaseService.createMember(uid, purchaseToken)
        preferencesManager.saveMemberData(memberData)
        return memberData
    }

    /**
     * Restore: ищет номер по purchaseToken (переустановка/смена устройства),
     * затем по UID, затем по кэшу.
     */
    suspend fun restoreMember(purchaseToken: String): MemberData? {
        // 1. Кэш
        val cached = preferencesManager.getMemberData()
        if (cached != null) return cached

        val uid = firebaseService.ensureSignedIn()

        // 2. По текущему UID (тот же анонимный юзер)
        val byUid = firebaseService.findMemberByUid(uid)
        if (byUid != null) {
            preferencesManager.saveMemberData(byUid)
            return byUid
        }

        // 3. По purchaseToken (переустановка — новый UID, но та же подписка)
        val byToken = firebaseService.findMemberByToken(purchaseToken)
        if (byToken != null) {
            preferencesManager.saveMemberData(byToken)
            return byToken
        }

        return null
    }

    /**
     * Получить кэшированные данные.
     */
    suspend fun getCachedMember(): MemberData? {
        return preferencesManager.getMemberData()
    }
}
