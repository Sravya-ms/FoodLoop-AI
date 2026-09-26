package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ConsumerType
import com.example.data.model.DonationStatus
import com.example.data.model.DonorType
import com.example.data.model.InstitutionSubtype
import com.example.data.model.MealPeriod
import com.example.data.model.NotificationType
import com.example.data.model.RequestStatus
import com.example.data.model.TransportPreference
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val password: String = "pass123",
    val phone: String,
    val role: UserRole,
    val donorType: DonorType? = null,
    val institutionSubtype: InstitutionSubtype? = null,
    val institutionName: String? = null,
    val consumerType: ConsumerType? = null,
    val address: String = "Main Road, Narasaraopet",
    val city: String = "Narasaraopet",
    val district: String = "Palnadu",
    val state: String = "Andhra Pradesh",
    val numberOfResidents: Int? = null,
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val verificationNotes: String = "",
    val documentName: String? = "Govt_Reg_Cert_2026.pdf",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "food_donations")
data class DonationEntity(
    @PrimaryKey val id: String,
    val donorId: String,
    val donorName: String,
    val donorVerified: Boolean = true,
    val donorType: DonorType = DonorType.INSTITUTIONAL,
    val city: String = "Narasaraopet",
    val area: String = "College Road / Kotappakonda By-pass",
    val pickupAddress: String = "Hostel Mess Gate 2, ABC College, Narasaraopet",
    val mealPeriod: MealPeriod = MealPeriod.LUNCH,
    val totalPlates: Int,
    val remainingPlates: Int,
    val reservedPlates: Int = 0,
    val menuSummary: String, // e.g., "Rice, Dal, Vegetable Curry, Curd"
    val isVeg: Boolean = true,
    val availableUntilTime: String = "3:30 PM",
    val expiryTimestamp: Long = System.currentTimeMillis() + (3 * 3600 * 1000),
    val isLateWindow: Boolean = false,
    val status: DonationStatus = DonationStatus.PUBLISHED,
    val hygieneConfirmed: Boolean = true,
    val distanceKm: Float = 2.4f,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "food_requests")
data class FoodRequestEntity(
    @PrimaryKey val id: String,
    val donationId: String,
    val donorId: String,
    val donorName: String,
    val consumerId: String,
    val consumerName: String,
    val consumerType: ConsumerType = ConsumerType.OLD_AGE_HOME,
    val consumerVerified: Boolean = true,
    val peopleCount: Int,
    val requestedPlates: Int,
    val status: RequestStatus = RequestStatus.PENDING,
    val transportPreference: TransportPreference = TransportPreference.SELF_PICKUP,
    val pickupAddress: String = "",
    val dropAddress: String = "",
    val estimatedDistanceKm: Float = 2.4f,
    val estimatedTransportCost: Int = 60,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val acceptedAt: Long? = null,
    val receivedAt: Long? = null,
    val completedAt: Long? = null
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val recipientUserId: String, // User ID or "ALL"
    val recipientRole: UserRole? = null, // DONOR, CONSUMER, or ADMIN
    val title: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val relatedDonationId: String? = null,
    val relatedRequestId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

fun DonationEntity.isBookingWindowOpen(currentTimeMillis: Long = System.currentTimeMillis()): Boolean {
    if (status == DonationStatus.CANCELLED || status == DonationStatus.EXPIRED || status == DonationStatus.COMPLETED) {
        return false
    }
    if (remainingPlates <= 0) {
        return false
    }
    if (expiryTimestamp > 0 && currentTimeMillis >= expiryTimestamp) {
        return false
    }
    return true
}

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val performedBy: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val requestId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: UserRole,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSystemMessage: Boolean = false
)
