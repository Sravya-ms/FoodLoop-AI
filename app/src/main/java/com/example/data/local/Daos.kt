package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DonationStatus
import com.example.data.model.RequestStatus
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = :role ORDER BY createdAt DESC")
    fun getUsersByRole(role: UserRole): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdOnce(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    fun getUserByEmail(email: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmailOnce(email: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    fun getUserCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET verificationStatus = :status, verificationNotes = :notes WHERE id = :userId")
    suspend fun updateVerification(userId: String, status: VerificationStatus, notes: String)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: String)
}

@Dao
interface DonationDao {
    @Query("SELECT * FROM food_donations ORDER BY createdAt DESC")
    fun getAllDonations(): Flow<List<DonationEntity>>

    @Query("SELECT * FROM food_donations WHERE status IN ('PUBLISHED', 'PARTIALLY_RESERVED') ORDER BY createdAt DESC")
    fun getActiveDonations(): Flow<List<DonationEntity>>

    @Query("SELECT * FROM food_donations WHERE id = :id LIMIT 1")
    fun getDonationById(id: String): Flow<DonationEntity?>

    @Query("SELECT * FROM food_donations WHERE id = :id LIMIT 1")
    suspend fun getDonationByIdOnce(id: String): DonationEntity?

    @Query("SELECT * FROM food_donations WHERE donorId = :donorId ORDER BY createdAt DESC")
    fun getDonationsByDonor(donorId: String): Flow<List<DonationEntity>>

    @Query("SELECT * FROM food_donations WHERE status = :status ORDER BY createdAt DESC")
    fun getDonationsByStatus(status: DonationStatus): Flow<List<DonationEntity>>

    @Query("SELECT * FROM food_donations WHERE city = :city AND status IN ('PUBLISHED', 'PARTIALLY_RESERVED') ORDER BY createdAt DESC")
    fun getAvailableDonationsByCity(city: String): Flow<List<DonationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: DonationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonations(donations: List<DonationEntity>)

    @Update
    suspend fun updateDonation(donation: DonationEntity)

    @Query("UPDATE food_donations SET remainingPlates = :remaining, reservedPlates = :reserved, status = :status WHERE id = :id")
    suspend fun updateQuantities(id: String, remaining: Int, reserved: Int, status: DonationStatus)

    @Query("UPDATE food_donations SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: DonationStatus)

    @Query("DELETE FROM food_donations WHERE id = :id")
    suspend fun deleteDonationById(id: String)
}

@Dao
interface FoodRequestDao {
    @Query("SELECT * FROM food_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<FoodRequestEntity>>

    @Query("SELECT * FROM food_requests WHERE id = :id LIMIT 1")
    fun getRequestById(id: String): Flow<FoodRequestEntity?>

    @Query("SELECT * FROM food_requests WHERE id = :id LIMIT 1")
    suspend fun getRequestByIdOnce(id: String): FoodRequestEntity?

    @Query("SELECT * FROM food_requests WHERE donorId = :donorId ORDER BY createdAt DESC")
    fun getRequestsByDonor(donorId: String): Flow<List<FoodRequestEntity>>

    @Query("SELECT * FROM food_requests WHERE consumerId = :consumerId ORDER BY createdAt DESC")
    fun getRequestsByConsumer(consumerId: String): Flow<List<FoodRequestEntity>>

    @Query("SELECT * FROM food_requests WHERE donationId = :donationId ORDER BY createdAt DESC")
    fun getRequestsByDonation(donationId: String): Flow<List<FoodRequestEntity>>

    @Query("SELECT * FROM food_requests WHERE donorId = :donorId AND status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingRequestsByDonor(donorId: String): Flow<List<FoodRequestEntity>>

    @Query("SELECT * FROM food_requests WHERE consumerId = :consumerId AND status IN ('PENDING', 'ACCEPTED', 'READY_FOR_PICKUP', 'IN_TRANSIT') ORDER BY createdAt DESC")
    fun getActiveRequestsByConsumer(consumerId: String): Flow<List<FoodRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: FoodRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<FoodRequestEntity>)

    @Update
    suspend fun updateRequest(request: FoodRequestEntity)

    @Query("UPDATE food_requests SET status = :status, acceptedAt = :acceptedAt WHERE id = :id")
    suspend fun markAccepted(id: String, status: RequestStatus, acceptedAt: Long)

    @Query("UPDATE food_requests SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: RequestStatus)

    @Query("UPDATE food_requests SET status = :status, completedAt = :completedAt WHERE id = :id")
    suspend fun markCompleted(id: String, status: RequestStatus, completedAt: Long)

    @Query("DELETE FROM food_requests WHERE id = :id")
    suspend fun deleteRequestById(id: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE recipientUserId = :userId OR (recipientUserId = 'ALL' AND recipientRole = :role) ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: String, role: UserRole): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE recipientUserId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUserId(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE recipientUserId = :userId OR (recipientUserId = 'ALL' AND recipientRole = :role)")
    suspend fun markAllAsRead(userId: String, role: UserRole)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE requestId = :requestId ORDER BY timestamp ASC")
    fun getMessagesForRequest(requestId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Query("SELECT * FROM chat_messages WHERE requestId = :requestId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestMessage(requestId: String): ChatMessageEntity?
}
