package com.subs4what.app.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

class FirebaseService {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun ensureSignedIn(): String {
        auth.currentUser?.let { return it.uid }
        val result = auth.signInAnonymously().await()
        return result.user?.uid ?: throw Exception("Anonymous sign-in failed")
    }

    /**
     * Первая подписка: создаёт номер участника.
     * Сохраняет маппинг и по UID, и по purchaseToken (для restore).
     */
    suspend fun createMember(uid: String, purchaseToken: String): MemberData {
        val tokenHash = hashToken(purchaseToken)

        return firestore.runTransaction { transaction ->
            // Может уже есть по UID (повторный вызов)
            val memberRef = firestore.collection("members").document(uid)
            val existing = transaction.get(memberRef)
            if (existing.exists()) {
                return@runTransaction MemberData(
                    memberNumber = existing.getLong("memberNumber")?.toInt() ?: 0,
                    createdAt = existing.getString("createdAt") ?: ""
                )
            }

            // Может уже есть по токену (переустановка, новый anon UID)
            val tokenRef = firestore.collection("subscriptions").document(tokenHash)
            val tokenSnap = transaction.get(tokenRef)
            if (tokenSnap.exists()) {
                val number = tokenSnap.getLong("memberNumber")?.toInt() ?: 0
                val created = tokenSnap.getString("createdAt") ?: ""
                // Привязываем к новому UID
                transaction.set(memberRef, mapOf(
                    "memberNumber" to number.toLong(),
                    "createdAt" to created,
                    "subscriptionActive" to true
                ))
                return@runTransaction MemberData(memberNumber = number, createdAt = created)
            }

            // Новый участник: инкремент счётчика
            val counterRef = firestore.collection("counters").document("members")
            val counterSnap = transaction.get(counterRef)
            val currentCount = counterSnap.getLong("count") ?: 0
            val newNumber = (currentCount + 1).toInt()
            val now = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                .format(java.util.Date())

            transaction.set(counterRef, mapOf("count" to newNumber.toLong()))
            transaction.set(memberRef, mapOf(
                "memberNumber" to newNumber.toLong(),
                "createdAt" to now,
                "subscriptionActive" to true
            ))
            // Маппинг токена → номер (для restore)
            transaction.set(tokenRef, mapOf(
                "memberNumber" to newNumber.toLong(),
                "createdAt" to now,
                "uid" to uid
            ))

            MemberData(memberNumber = newNumber, createdAt = now)
        }.await()
    }

    /**
     * Restore: ищет номер по purchaseToken (после переустановки).
     */
    suspend fun findMemberByToken(purchaseToken: String): MemberData? {
        val tokenHash = hashToken(purchaseToken)
        val snapshot = firestore.collection("subscriptions").document(tokenHash).get().await()
        if (!snapshot.exists()) return null
        return MemberData(
            memberNumber = snapshot.getLong("memberNumber")?.toInt() ?: 0,
            createdAt = snapshot.getString("createdAt") ?: ""
        )
    }

    /**
     * Restore: ищет номер по текущему UID.
     */
    suspend fun findMemberByUid(uid: String): MemberData? {
        val snapshot = firestore.collection("members").document(uid).get().await()
        if (!snapshot.exists()) return null
        return MemberData(
            memberNumber = snapshot.getLong("memberNumber")?.toInt() ?: 0,
            createdAt = snapshot.getString("createdAt") ?: ""
        )
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(token.toByteArray())
            .joinToString("") { "%02x".format(it) }
            .take(32)
    }
}

data class MemberData(
    val memberNumber: Int,
    val createdAt: String
)
