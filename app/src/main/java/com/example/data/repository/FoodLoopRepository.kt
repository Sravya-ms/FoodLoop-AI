package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.AuditLogEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.DonationEntity
import com.example.data.local.FoodRequestEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.UserEntity
import com.example.data.local.isBookingWindowOpen
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
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class FoodLoopRepository(private val db: AppDatabase) {

    private val userDao = db.userDao()
    private val donationDao = db.donationDao()
    private val requestDao = db.foodRequestDao()
    private val notificationDao = db.notificationDao()
    private val auditDao = db.auditLogDao()
    private val chatDao = db.chatMessageDao()

    val allDonations: Flow<List<DonationEntity>> = donationDao.getAllDonations()
    val allRequests: Flow<List<FoodRequestEntity>> = requestDao.getAllRequests()
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val allAuditLogs: Flow<List<AuditLogEntity>> = auditDao.getAllLogs()

    fun getChatMessages(requestId: String): Flow<List<ChatMessageEntity>> =
        chatDao.getMessagesForRequest(requestId)

    fun getDonationsByDonor(donorId: String): Flow<List<DonationEntity>> =
        donationDao.getDonationsByDonor(donorId)

    fun getRequestsByConsumer(consumerId: String): Flow<List<FoodRequestEntity>> =
        requestDao.getRequestsByConsumer(consumerId)

    fun getRequestsByDonor(donorId: String): Flow<List<FoodRequestEntity>> =
        requestDao.getRequestsByDonor(donorId)

    fun getNotificationsForUser(userId: String, role: UserRole): Flow<List<NotificationEntity>> =
        notificationDao.getNotificationsForUser(userId, role)

    fun getDonationById(id: String): Flow<DonationEntity?> =
        donationDao.getDonationById(id)

    fun getRequestById(id: String): Flow<FoodRequestEntity?> =
        requestDao.getRequestById(id)

    fun getActiveDonations(): Flow<List<DonationEntity>> =
        donationDao.getActiveDonations()

    fun getPendingRequestsForDonor(donorId: String): Flow<List<FoodRequestEntity>> =
        requestDao.getPendingRequestsByDonor(donorId)

    fun getActiveRequestsForConsumer(consumerId: String): Flow<List<FoodRequestEntity>> =
        requestDao.getActiveRequestsByConsumer(consumerId)

    fun getUserByEmail(email: String): Flow<UserEntity?> =
        userDao.getUserByEmail(email)

    suspend fun deleteUser(id: String) =
        userDao.deleteUserById(id)

    suspend fun deleteDonation(id: String) =
        donationDao.deleteDonationById(id)

    suspend fun deleteRequest(id: String) =
        requestDao.deleteRequestById(id)

    suspend fun seedInitialDataIfEmpty() {
        val existing = userDao.getUserByIdOnce("donor_abc_inst")
        if (existing == null) {
            val users = listOf(
                UserEntity(
                    id = "donor_abc_inst",
                    name = "ABC Institution",
                    email = "principal@abcinstitution.edu.in",
                    phone = "+91 98765 43210",
                    role = UserRole.DONOR,
                    donorType = DonorType.INSTITUTIONAL,
                    institutionSubtype = InstitutionSubtype.COLLEGE,
                    institutionName = "ABC Engineering College & Hostels",
                    address = "Kotappakonda Road, Narasaraopet",
                    city = "Narasaraopet",
                    district = "Palnadu",
                    state = "Andhra Pradesh",
                    password = "password123",
                    verificationStatus = VerificationStatus.VERIFIED,
                    verificationNotes = "Government recognized engineering college hostel mess inspected and certified."
                ),
                UserEntity(
                    id = "donor_royal_catering",
                    name = "Royal Grand Catering & Banquet",
                    email = "royal.grand.catering@gmail.com",
                    phone = "+91 98480 12345",
                    role = UserRole.DONOR,
                    donorType = DonorType.FUNCTION_HALL,
                    institutionSubtype = InstitutionSubtype.CATERING,
                    institutionName = "Royal Grand Event & Banquet Hall",
                    address = "Arundelpet, Narasaraopet",
                    city = "Narasaraopet",
                    district = "Palnadu",
                    state = "Andhra Pradesh",
                    password = "password123",
                    verificationStatus = VerificationStatus.VERIFIED,
                    verificationNotes = "Commercial banquet & function hall certified for food safety."
                ),
                UserEntity(
                    id = "donor_pending_hotel",
                    name = "Spice Garden Restaurant",
                    email = "spicegarden@contact.in",
                    phone = "+91 94401 56789",
                    role = UserRole.DONOR,
                    donorType = DonorType.RESTAURANT,
                    institutionSubtype = InstitutionSubtype.RESTAURANT,
                    institutionName = "Spice Garden Multi-Cuisine Restaurant",
                    address = "Station Road, Narasaraopet",
                    city = "Narasaraopet",
                    district = "Palnadu",
                    state = "Andhra Pradesh",
                    password = "password123",
                    verificationStatus = VerificationStatus.VERIFIED,
                    verificationNotes = "Licensed restaurant kitchen in Narasaraopet."
                ),
                UserEntity(
                    id = "donor_sita_function",
                    name = "Sita Kalyana Mandapam (Function Hall)",
                    email = "functions@sitamandapam.com",
                    phone = "+91 98480 33445",
                    role = UserRole.DONOR,
                    donorType = DonorType.FUNCTION_HALL,
                    institutionSubtype = InstitutionSubtype.FAMILY_FUNCTION,
                    institutionName = "Sita Kalyana Mandapam",
                    address = "Bypass Road, Narasaraopet",
                    city = "Narasaraopet",
                    district = "Palnadu",
                    state = "Andhra Pradesh",
                    password = "password123",
                    verificationStatus = VerificationStatus.VERIFIED,
                    verificationNotes = "Marriage and reception event hall with bulk surplus capacity."
                ),
                UserEntity(
                    id = "donor_commoner_srikanth",
                    name = "Srikanth (Commoner / Citizen Donor)",
                    email = "srikanth.donations@gmail.com",
                    phone = "+91 99591 66778",
                    role = UserRole.DONOR,
                    donorType = DonorType.COMMONER,
                    institutionSubtype = InstitutionSubtype.COMMON_CITIZEN,
                    institutionName = "Commoner / Citizen Donation",
                    address = "Ramireddy Pet, Narasaraopet",
                    city = "Narasaraopet",
                    district = "Palnadu",
                    state = "Andhra Pradesh",
                    password = "password123",
                    verificationStatus = VerificationStatus.VERIFIED,
                    verificationNotes = "Verified individual resident donor for private food donations."
                ),
                UserEntity(
                    id = "consumer_abc_oldage",
                    name = "ABC Old Age Home",
                    email = "care@abcoldagehome.org",
                    phone = "+91 98665 11223",
                    role = UserRole.CONSUMER,
                    consumerType = ConsumerType.OLD_AGE_HOME,
                    address = "Shanti Nagar, Near Clock Tower, Narasaraopet",
                    city = "Narasaraopet",
                    district = "Palnadu",
                    state = "Andhra Pradesh",
                    numberOfResidents = 85,
                    verificationStatus = VerificationStatus.VERIFIED,
                    verificationNotes = "Registered charitable trust. 85 senior citizen residents verified on-site."
                ),
                UserEntity(
                    id = "consumer_sunrise_orphanage",
                    name = "Sunrise Child Welfare Home",
                    email = "director@sunriseorphanage.org",
                    phone = "+91 99887 76655",
                    role = UserRole.CONSUMER,
                    consumerType = ConsumerType.ORPHANAGE,
                    address = "Prakash Nagar, Narasaraopet",
                    city = "Narasaraopet",
                    district = "Palnadu",
                    state = "Andhra Pradesh",
                    numberOfResidents = 60,
                    verificationStatus = VerificationStatus.VERIFIED,
                    verificationNotes = "State registered child care institution with 60 children."
                ),
                UserEntity(
                    id = "consumer_pending_home",
                    name = "Karuna Senior Haven",
                    email = "info@karunaseniors.org",
                    phone = "+91 91234 56780",
                    role = UserRole.CONSUMER,
                    consumerType = ConsumerType.OLD_AGE_HOME,
                    address = "Vinukonda Road, Narasaraopet",
                    city = "Narasaraopet",
                    district = "Palnadu",
                    state = "Andhra Pradesh",
                    numberOfResidents = 45,
                    verificationStatus = VerificationStatus.PENDING,
                    verificationNotes = "Trust registration document uploaded, pending verification."
                ),
                UserEntity(
                    id = "admin_super",
                    name = "FoodLoop District Administrator",
                    email = "admin@foodloop.ai",
                    phone = "+91 90000 00001",
                    role = UserRole.ADMIN,
                    address = "District Coordination Hub, Narasaraopet",
                    city = "Narasaraopet",
                    district = "Palnadu",
                    state = "Andhra Pradesh",
                    verificationStatus = VerificationStatus.VERIFIED
                )
            )
            userDao.insertUsers(users)

            val now = System.currentTimeMillis()
            val donations = listOf(
                DonationEntity(
                    id = "FL-DON-101",
                    donorId = "donor_abc_inst",
                    donorName = "ABC Institution",
                    donorVerified = true,
                    donorType = DonorType.INSTITUTIONAL,
                    city = "Narasaraopet",
                    area = "College Road / Hostel Mess Gate 2",
                    pickupAddress = "Hostel Mess Gate 2, ABC College, Narasaraopet",
                    mealPeriod = MealPeriod.LUNCH,
                    totalPlates = 100,
                    remainingPlates = 10,
                    reservedPlates = 90,
                    menuSummary = "Rice, Dal, Vegetable Curry, Curd",
                    isVeg = true,
                    availableUntilTime = "3:30 PM",
                    expiryTimestamp = now + (3 * 3600 * 1000),
                    isLateWindow = false,
                    status = DonationStatus.PARTIALLY_RESERVED,
                    distanceKm = 2.4f,
                    createdAt = now - (35 * 60 * 1000)
                ),
                DonationEntity(
                    id = "FL-DON-102",
                    donorId = "donor_royal_catering",
                    donorName = "Royal Grand Catering",
                    donorVerified = true,
                    donorType = DonorType.INSTITUTIONAL,
                    city = "Narasaraopet",
                    area = "Arundelpet Banquet Hall",
                    pickupAddress = "Kitchen Loading Bay, Royal Grand, Arundelpet, Narasaraopet",
                    mealPeriod = MealPeriod.LUNCH,
                    totalPlates = 120,
                    remainingPlates = 120,
                    reservedPlates = 0,
                    menuSummary = "Veg Pulao, Paneer Butter Masala, Raita, Gulab Jamun",
                    isVeg = true,
                    availableUntilTime = "3:15 PM",
                    expiryTimestamp = now + (2 * 3600 * 1000),
                    isLateWindow = false,
                    status = DonationStatus.PUBLISHED,
                    distanceKm = 3.8f,
                    createdAt = now - (15 * 60 * 1000)
                ),
                DonationEntity(
                    id = "FL-DON-103",
                    donorId = "donor_abc_inst",
                    donorName = "ABC Institution",
                    donorVerified = true,
                    donorType = DonorType.INSTITUTIONAL,
                    city = "Narasaraopet",
                    area = "Hostel Campus",
                    pickupAddress = "Main Canteen, ABC College",
                    mealPeriod = MealPeriod.BREAKFAST,
                    totalPlates = 75,
                    remainingPlates = 0,
                    reservedPlates = 75,
                    menuSummary = "Idli, Vada, Sambar, Coconut Chutney",
                    isVeg = true,
                    availableUntilTime = "9:30 AM",
                    expiryTimestamp = now - (2 * 3600 * 1000),
                    isLateWindow = false,
                    status = DonationStatus.COMPLETED,
                    distanceKm = 2.4f,
                    createdAt = now - (6 * 3600 * 1000)
                ),
                DonationEntity(
                    id = "FL-DON-104",
                    donorId = "donor_royal_catering",
                    donorName = "Royal Grand Catering",
                    donorVerified = true,
                    donorType = DonorType.INSTITUTIONAL,
                    city = "Narasaraopet",
                    area = "Arundelpet Banquet Hall",
                    pickupAddress = "Kitchen Loading Bay, Royal Grand, Arundelpet, Narasaraopet",
                    mealPeriod = MealPeriod.DINNER,
                    totalPlates = 150,
                    remainingPlates = 150,
                    reservedPlates = 0,
                    menuSummary = "Jeera Rice, Mixed Veg Curry, Chapati, Kheer",
                    isVeg = true,
                    availableUntilTime = "8:30 PM",
                    expiryTimestamp = now + (4 * 3600 * 1000),
                    isLateWindow = false,
                    status = DonationStatus.PUBLISHED,
                    distanceKm = 3.8f,
                    createdAt = now - (20 * 60 * 1000)
                ),
                DonationEntity(
                    id = "FL-DON-105",
                    donorId = "donor_abc_inst",
                    donorName = "ABC Institution",
                    donorVerified = true,
                    donorType = DonorType.INSTITUTIONAL,
                    city = "Narasaraopet",
                    area = "Hostel Mess Gate 2",
                    pickupAddress = "Hostel Mess Gate 2, ABC College, Narasaraopet",
                    mealPeriod = MealPeriod.DINNER,
                    totalPlates = 80,
                    remainingPlates = 0,
                    reservedPlates = 80,
                    menuSummary = "Vegetable Biryani, Mirchi Ka Salan, Raita",
                    isVeg = true,
                    availableUntilTime = "8:30 PM",
                    expiryTimestamp = now - (25 * 60 * 1000),
                    isLateWindow = false,
                    status = DonationStatus.FULLY_RESERVED,
                    distanceKm = 2.4f,
                    createdAt = now - (90 * 60 * 1000)
                )
            )
            donationDao.insertDonations(donations)

            val requests = listOf(
                FoodRequestEntity(
                    id = "FL-2026-000124",
                    donationId = "FL-DON-101",
                    donorId = "donor_abc_inst",
                    donorName = "ABC Institution",
                    consumerId = "consumer_abc_oldage",
                    consumerName = "ABC Old Age Home",
                    consumerType = ConsumerType.OLD_AGE_HOME,
                    consumerVerified = true,
                    peopleCount = 85,
                    requestedPlates = 90,
                    status = RequestStatus.ACCEPTED,
                    transportPreference = TransportPreference.REQUEST_TRANSPORT,
                    pickupAddress = "Hostel Mess Gate 2, ABC College, Narasaraopet",
                    dropAddress = "Shanti Nagar, Near Clock Tower, Narasaraopet",
                    estimatedDistanceKm = 2.4f,
                    estimatedTransportCost = 60,
                    notes = "Scheduled collection for 85 elders at lunchtime.",
                    createdAt = now - (25 * 60 * 1000),
                    acceptedAt = now - (10 * 60 * 1000)
                ),
                FoodRequestEntity(
                    id = "FL-2026-000125",
                    donationId = "FL-DON-102",
                    donorId = "donor_royal_catering",
                    donorName = "Royal Grand Catering",
                    consumerId = "consumer_sunrise_orphanage",
                    consumerName = "Sunrise Child Welfare Home",
                    consumerType = ConsumerType.ORPHANAGE,
                    consumerVerified = true,
                    peopleCount = 60,
                    requestedPlates = 60,
                    status = RequestStatus.READY_FOR_PICKUP,
                    transportPreference = TransportPreference.SELF_PICKUP,
                    pickupAddress = "Kitchen Loading Bay, Royal Grand, Arundelpet, Narasaraopet",
                    dropAddress = "Prakash Nagar, Narasaraopet",
                    estimatedDistanceKm = 3.8f,
                    estimatedTransportCost = 0,
                    notes = "Self pickup arranged with shelter volunteer van for 60 children.",
                    createdAt = now - (20 * 60 * 1000),
                    acceptedAt = now - (12 * 60 * 1000)
                ),
                FoodRequestEntity(
                    id = "FL-2026-000126",
                    donationId = "FL-DON-105",
                    donorId = "donor_abc_inst",
                    donorName = "ABC Institution",
                    consumerId = "consumer_abc_oldage",
                    consumerName = "ABC Old Age Home",
                    consumerType = ConsumerType.OLD_AGE_HOME,
                    consumerVerified = true,
                    peopleCount = 80,
                    requestedPlates = 80,
                    status = RequestStatus.IN_TRANSIT,
                    transportPreference = TransportPreference.REQUEST_TRANSPORT,
                    pickupAddress = "Hostel Mess Gate 2, ABC College, Narasaraopet",
                    dropAddress = "Shanti Nagar, Near Clock Tower, Narasaraopet",
                    estimatedDistanceKm = 2.4f,
                    estimatedTransportCost = 60,
                    notes = "Requested prior to 8:30 PM cutoff. Fulfillment proceeding on schedule.",
                    createdAt = now - (45 * 60 * 1000),
                    acceptedAt = now - (35 * 60 * 1000)
                )
            )
            requestDao.insertRequests(requests)

            val notifications = listOf(
                NotificationEntity(
                    id = "NOTIF-1",
                    recipientUserId = "consumer_abc_oldage",
                    recipientRole = UserRole.CONSUMER,
                    title = "New Lunch Donation Available",
                    message = "ABC Institution announced 100 plates (Rice, Dal, Veg Curry) available until 3:30 PM (2.4 km away).",
                    type = NotificationType.NEW_DONATION,
                    relatedDonationId = "FL-DON-101",
                    timestamp = now - (30 * 60 * 1000)
                ),
                NotificationEntity(
                    id = "NOTIF-2",
                    recipientUserId = "consumer_abc_oldage",
                    recipientRole = UserRole.CONSUMER,
                    title = "Request Accepted!",
                    message = "ABC Institution accepted your request for 90 plates (FL-2026-000124). Ready for transport.",
                    type = NotificationType.REQUEST_ACCEPTED,
                    relatedRequestId = "FL-2026-000124",
                    timestamp = now - (10 * 60 * 1000)
                ),
                NotificationEntity(
                    id = "NOTIF-3",
                    recipientUserId = "donor_abc_inst",
                    recipientRole = UserRole.DONOR,
                    title = "New Request Received",
                    message = "ABC Old Age Home requested 90 plates for 85 elders.",
                    type = NotificationType.NEW_DONATION,
                    relatedRequestId = "FL-2026-000124",
                    timestamp = now - (20 * 60 * 1000)
                ),
                NotificationEntity(
                    id = "NOTIF-4",
                    recipientUserId = "ALL",
                    recipientRole = UserRole.CONSUMER,
                    title = "🍱 Evening Dinner Surplus Announced",
                    message = "Royal Grand Catering announced 150 plates available until 8:30 PM.",
                    type = NotificationType.NEW_DONATION,
                    relatedDonationId = "FL-DON-104",
                    timestamp = now - (5 * 60 * 1000)
                )
            )
            notificationDao.insertNotifications(notifications)

            // Seed initial real-time coordination chat messages for the accepted request
            val initialChats = listOf(
                ChatMessageEntity(
                    id = "CHAT-INIT-1",
                    requestId = "FL-2026-000124",
                    senderId = "SYSTEM",
                    senderName = "FoodLoop Coordination Hub",
                    senderRole = UserRole.ADMIN,
                    message = "Donation request accepted by ABC Institution. Real-time coordination channel opened between donor and recipient.",
                    timestamp = now - (9 * 60 * 1000),
                    isSystemMessage = true
                ),
                ChatMessageEntity(
                    id = "CHAT-INIT-2",
                    requestId = "FL-2026-000124",
                    senderId = "donor_abc_inst",
                    senderName = "ABC Institution",
                    senderRole = UserRole.DONOR,
                    message = "Hello ABC Old Age Home team! 90 plates of fresh Lunch (Rice, Dal, Veg Curry, Curd) are being packed in sealed hygienic containers at Hostel Mess Gate 2.",
                    timestamp = now - (8 * 60 * 1000),
                    isSystemMessage = false
                ),
                ChatMessageEntity(
                    id = "CHAT-INIT-3",
                    requestId = "FL-2026-000124",
                    senderId = "consumer_abc_oldage",
                    senderName = "ABC Old Age Home",
                    senderRole = UserRole.CONSUMER,
                    message = "Thank you so much! Our transport vehicle has started from Shanti Nagar. Our driver Suresh will arrive by 1:15 PM.",
                    timestamp = now - (5 * 60 * 1000),
                    isSystemMessage = false
                ),
                ChatMessageEntity(
                    id = "CHAT-INIT-4",
                    requestId = "FL-2026-000124",
                    senderId = "donor_abc_inst",
                    senderName = "ABC Institution",
                    senderRole = UserRole.DONOR,
                    message = "Noted! We have kept the loading bay clear for Suresh. Please call us if there is any traffic on Kotappakonda bypass.",
                    timestamp = now - (2 * 60 * 1000),
                    isSystemMessage = false
                )
            )
            chatDao.insertMessages(initialChats)

            auditDao.insertLog(
                AuditLogEntity(
                    action = "INITIAL_SYSTEM_SETUP",
                    performedBy = "System",
                    details = "FoodLoop AI initialized with verified institutional participants and coordination chat in Narasaraopet."
                )
            )
        }
    }

    suspend fun registerUser(user: UserEntity) {
        userDao.insertUser(user)
        auditDao.insertLog(
            AuditLogEntity(
                action = "USER_REGISTERED",
                performedBy = user.id,
                details = "Registered as ${user.role} (${user.name}) with status ${user.verificationStatus}."
            )
        )
    }

    suspend fun createDonation(donation: DonationEntity) {
        donationDao.insertDonation(donation)
        auditDao.insertLog(
            AuditLogEntity(
                action = "DONATION_PUBLISHED",
                performedBy = donation.donorId,
                details = "Published donation ${donation.id}: ${donation.totalPlates} plates of ${donation.mealPeriod} available until ${donation.availableUntilTime}."
            )
        )
        // Broadcast notification to verified consumers only (donors do not see this)
        notificationDao.insertNotification(
            NotificationEntity(
                id = "NOTIF-" + UUID.randomUUID().toString().take(8),
                recipientUserId = "ALL",
                recipientRole = UserRole.CONSUMER,
                title = "🍱 New Food Donation: ${donation.totalPlates} Plates",
                message = "${donation.donorName} published ${donation.totalPlates} plates (${donation.menuSummary}) in ${donation.city}. Available until ${donation.availableUntilTime}.",
                type = NotificationType.NEW_DONATION,
                relatedDonationId = donation.id
            )
        )
    }

    suspend fun placeFoodRequest(
        donationId: String,
        consumer: UserEntity,
        peopleCount: Int,
        requestedPlates: Int,
        transportPreference: TransportPreference,
        notes: String
    ): Result<FoodRequestEntity> {
        val donation = donationDao.getDonationByIdOnce(donationId)
            ?: return Result.failure(Exception("Donation not found."))

        if (!donation.isBookingWindowOpen()) {
            return Result.failure(Exception("The booking time for this donation (${donation.availableUntilTime}) has completed. New requests cannot be placed."))
        }

        if (donation.status == DonationStatus.CANCELLED || donation.status == DonationStatus.EXPIRED || donation.status == DonationStatus.COMPLETED) {
            return Result.failure(Exception("Donation is no longer available."))
        }

        if (requestedPlates > donation.remainingPlates) {
            return Result.failure(Exception("Requested $requestedPlates plates exceeds remaining ${donation.remainingPlates} plates."))
        }

        val requestId = "FL-" + System.currentTimeMillis().toString().takeLast(6)
        val request = FoodRequestEntity(
            id = requestId,
            donationId = donation.id,
            donorId = donation.donorId,
            donorName = donation.donorName,
            consumerId = consumer.id,
            consumerName = consumer.name,
            consumerType = consumer.consumerType ?: ConsumerType.OLD_AGE_HOME,
            consumerVerified = consumer.verificationStatus == VerificationStatus.VERIFIED,
            peopleCount = peopleCount,
            requestedPlates = requestedPlates,
            status = RequestStatus.PENDING,
            transportPreference = transportPreference,
            pickupAddress = donation.pickupAddress,
            dropAddress = consumer.address,
            estimatedDistanceKm = donation.distanceKm,
            estimatedTransportCost = (donation.distanceKm * 25).toInt().coerceAtLeast(40),
            notes = notes,
            createdAt = System.currentTimeMillis()
        )

        requestDao.insertRequest(request)

        // Notification sent strictly to the specific donor (consumers do not see this)
        notificationDao.insertNotification(
            NotificationEntity(
                id = "NOTIF-" + UUID.randomUUID().toString().take(8),
                recipientUserId = donation.donorId,
                recipientRole = UserRole.DONOR,
                title = "New Food Request ($requestedPlates plates)",
                message = "${consumer.name} requested $requestedPlates plates for $peopleCount people. Awaiting your approval.",
                type = NotificationType.NEW_DONATION,
                relatedRequestId = requestId,
                relatedDonationId = donation.id
            )
        )

        auditDao.insertLog(
            AuditLogEntity(
                action = "REQUEST_PLACED",
                performedBy = consumer.id,
                details = "Requested $requestedPlates plates for $peopleCount people on donation ${donation.id}."
            )
        )

        return Result.success(request)
    }

    suspend fun acceptRequest(requestId: String): Result<Unit> {
        val request = requestDao.getRequestByIdOnce(requestId)
            ?: return Result.failure(Exception("Request not found."))
        val donation = donationDao.getDonationByIdOnce(request.donationId)
            ?: return Result.failure(Exception("Donation not found."))

        if (request.requestedPlates > donation.remainingPlates) {
            return Result.failure(Exception("Cannot accept: remaining plates (${donation.remainingPlates}) is less than requested (${request.requestedPlates})."))
        }

        val newRemaining = donation.remainingPlates - request.requestedPlates
        val newReserved = donation.reservedPlates + request.requestedPlates
        val newStatus = if (newRemaining == 0) DonationStatus.FULLY_RESERVED else DonationStatus.PARTIALLY_RESERVED

        donationDao.updateQuantities(donation.id, newRemaining, newReserved, newStatus)
        requestDao.markAccepted(requestId, RequestStatus.ACCEPTED, System.currentTimeMillis())

        // Automatically initiate real-time coordination chat channel
        initiateCoordinationChat(request, donation)

        notificationDao.insertNotification(
            NotificationEntity(
                id = "NOTIF-" + UUID.randomUUID().toString().take(8),
                recipientUserId = request.consumerId,
                recipientRole = UserRole.CONSUMER,
                title = "✓ Food Request Confirmed!",
                message = "${donation.donorName} accepted your request for ${request.requestedPlates} plates. Coordination chat is now open.",
                type = NotificationType.REQUEST_ACCEPTED,
                relatedRequestId = requestId,
                relatedDonationId = donation.id
            )
        )

        auditDao.insertLog(
            AuditLogEntity(
                action = "REQUEST_ACCEPTED",
                performedBy = donation.donorId,
                details = "Accepted request $requestId for ${request.requestedPlates} plates. Coordination chat channel initiated."
            )
        )

        return Result.success(Unit)
    }

    suspend fun initiateCoordinationChat(request: FoodRequestEntity, donation: DonationEntity) {
        val now = System.currentTimeMillis()
        val sysMsg = ChatMessageEntity(
            id = "CHAT-SYS-" + UUID.randomUUID().toString().take(8),
            requestId = request.id,
            senderId = "SYSTEM",
            senderName = "FoodLoop Coordination Hub",
            senderRole = UserRole.ADMIN,
            message = "Donation request accepted. Real-time coordination channel opened between ${donation.donorName} and ${request.consumerName}.",
            timestamp = now,
            isSystemMessage = true
        )
        val donorGreeting = ChatMessageEntity(
            id = "CHAT-MSG-" + UUID.randomUUID().toString().take(8),
            requestId = request.id,
            senderId = donation.donorId,
            senderName = donation.donorName,
            senderRole = UserRole.DONOR,
            message = "Hello ${request.consumerName}! We have approved your food request for ${request.requestedPlates} plates. We are preparing the packages at ${donation.pickupAddress}. What time will your pickup vehicle arrive?",
            timestamp = now + 50,
            isSystemMessage = false
        )
        chatDao.insertMessages(listOf(sysMsg, donorGreeting))
    }

    suspend fun sendChatMessage(
        requestId: String,
        senderId: String,
        senderName: String,
        senderRole: UserRole,
        message: String
    ): ChatMessageEntity {
        val msg = ChatMessageEntity(
            id = "CHAT-MSG-" + UUID.randomUUID().toString().take(8),
            requestId = requestId,
            senderId = senderId,
            senderName = senderName,
            senderRole = senderRole,
            message = message,
            timestamp = System.currentTimeMillis(),
            isSystemMessage = false
        )
        chatDao.insertMessage(msg)
        return msg
    }

    suspend fun declineRequest(requestId: String, reason: String = "Capacity mismatch"): Result<Unit> {
        val request = requestDao.getRequestByIdOnce(requestId)
            ?: return Result.failure(Exception("Request not found."))

        requestDao.updateStatus(requestId, RequestStatus.DECLINED)

        notificationDao.insertNotification(
            NotificationEntity(
                id = "NOTIF-" + UUID.randomUUID().toString().take(8),
                recipientUserId = request.consumerId,
                recipientRole = UserRole.CONSUMER,
                title = "Request Declined",
                message = "Your request $requestId was declined by donor: $reason.",
                type = NotificationType.REQUEST_DECLINED,
                relatedRequestId = requestId
            )
        )

        auditDao.insertLog(
            AuditLogEntity(
                action = "REQUEST_DECLINED",
                performedBy = request.donorId,
                details = "Declined request $requestId. Reason: $reason."
            )
        )

        return Result.success(Unit)
    }

    suspend fun updateRequestStatus(requestId: String, status: RequestStatus) {
        requestDao.updateStatus(requestId, status)
        val request = requestDao.getRequestByIdOnce(requestId) ?: return
        auditDao.insertLog(
            AuditLogEntity(
                action = "REQUEST_STATUS_UPDATED",
                performedBy = "System",
                details = "Request $requestId updated to status: ${status.name}"
            )
        )
        if (status == RequestStatus.IN_TRANSIT) {
            notificationDao.insertNotification(
                NotificationEntity(
                    id = "NOTIF-" + UUID.randomUUID().toString().take(8),
                    recipientUserId = request.consumerId,
                    recipientRole = UserRole.CONSUMER,
                    title = "🚚 Food is on the way!",
                    message = "Food collection from ${request.donorName} is currently in transit to ${request.consumerName}.",
                    type = NotificationType.IN_DELIVERY,
                    relatedRequestId = requestId
                )
            )
        }
    }

    suspend fun confirmReceipt(requestId: String): Result<Unit> {
        val request = requestDao.getRequestByIdOnce(requestId)
            ?: return Result.failure(Exception("Request not found."))
        val now = System.currentTimeMillis()

        requestDao.markCompleted(requestId, RequestStatus.COMPLETED, now)

        val donation = donationDao.getDonationByIdOnce(request.donationId)
        if (donation != null) {
            if (donation.remainingPlates == 0) {
                donationDao.updateStatus(donation.id, DonationStatus.COMPLETED)
            }
        }

        notificationDao.insertNotification(
            NotificationEntity(
                id = "NOTIF-" + UUID.randomUUID().toString().take(8),
                recipientUserId = request.donorId,
                recipientRole = UserRole.DONOR,
                title = "❤️ Food Received Successfully!",
                message = "${request.consumerName} confirmed receipt of ${request.requestedPlates} plates. Thank you for making a difference!",
                type = NotificationType.RECEIVED_COMPLETED,
                relatedRequestId = requestId,
                relatedDonationId = request.donationId
            )
        )

        auditDao.insertLog(
            AuditLogEntity(
                action = "FOOD_RECEIPT_CONFIRMED",
                performedBy = request.consumerId,
                details = "Confirmed receipt of ${request.requestedPlates} plates from ${request.donorName}. Donation successfully completed."
            )
        )

        return Result.success(Unit)
    }

    suspend fun verifyOrganization(userId: String, status: VerificationStatus, notes: String) {
        userDao.updateVerification(userId, status, notes)
        notificationDao.insertNotification(
            NotificationEntity(
                id = "NOTIF-" + UUID.randomUUID().toString().take(8),
                recipientUserId = userId,
                title = if (status == VerificationStatus.VERIFIED) "✓ Verification Approved!" else "Verification Status: ${status.displayName}",
                message = if (status == VerificationStatus.VERIFIED)
                    "Your organization has been officially verified by the District Administration. You can now access full platform privileges."
                else
                    "Admin update: $notes",
                type = NotificationType.SYSTEM_VERIFIED
            )
        )
        auditDao.insertLog(
            AuditLogEntity(
                action = "VERIFICATION_UPDATED",
                performedBy = "admin_super",
                details = "Updated user $userId to status $status ($notes)."
            )
        )
    }

    suspend fun markNotificationRead(id: String) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsRead(userId: String, role: UserRole) {
        notificationDao.markAllAsRead(userId, role)
    }
}
