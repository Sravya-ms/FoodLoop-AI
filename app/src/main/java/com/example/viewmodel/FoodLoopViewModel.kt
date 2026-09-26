package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.data.model.RequestStatus
import com.example.data.model.TransportPreference
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.data.repository.FoodLoopRepository
import com.example.ui.navigation.AppScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import com.example.localization.LanguageCatalog
import com.example.localization.LocalizationProvider
import com.example.localization.SupportedLanguage
import com.example.localization.AppStrings

data class AiChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class FoodLoopViewModel(private val repository: FoodLoopRepository) : ViewModel() {

    // Navigation Stack
    private val _screenStack = MutableStateFlow(listOf(AppScreen.LANDING))
    val currentScreen: StateFlow<AppScreen> = _screenStack.combine(MutableStateFlow(Unit)) { stack, _ ->
        stack.lastOrNull() ?: AppScreen.LANDING
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppScreen.LANDING)

    // Current logged-in user
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Database states
    val allDonations: StateFlow<List<DonationEntity>> = repository.allDonations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRequests: StateFlow<List<FoodRequestEntity>> = repository.allRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAuditLogs: StateFlow<List<AuditLogEntity>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications for current user
    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications.asStateFlow()

    // Selections
    val selectedDonationId = MutableStateFlow<String?>("FL-DON-101")
    val selectedRequestId = MutableStateFlow<String?>("FL-2026-000124")

    // Active Coordination Chat for accepted requests
    val activeChatRequestId = MutableStateFlow<String?>("FL-2026-000124")
    private val _chatMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessageEntity>> = _chatMessages.asStateFlow()

    // Consumer search & filters
    val searchQuery = MutableStateFlow("")
    val filterMealPeriod = MutableStateFlow<MealPeriod?>(null)
    val filterMaxDistance = MutableStateFlow(15f) // km
    val filterMinPlates = MutableStateFlow(0)
    val filterVegOnly = MutableStateFlow(false)

    // Donate Wizard Form State
    val donateStep = MutableStateFlow(1) // 1 to 7
    val donateMealPeriod = MutableStateFlow(MealPeriod.LUNCH)
    val donatePlates = MutableStateFlow("100")
    val donateMenuItems = MutableStateFlow(listOf("Rice", "Dal", "Vegetable Curry", "Curd"))
    val donateIsVeg = MutableStateFlow(true)
    val donateCity = MutableStateFlow("Narasaraopet")
    val donateArea = MutableStateFlow("College Road / Kotappakonda By-pass")
    val donatePickupAddress = MutableStateFlow("Hostel Mess Gate 2, ABC College, Narasaraopet")
    val donateAvailableUntil = MutableStateFlow("3:30 PM")
    val donateSafetyCheck1 = MutableStateFlow(true)
    val donateSafetyCheck2 = MutableStateFlow(true)
    val donateSafetyCheck3 = MutableStateFlow(true)
    val donateSafetyCheck4 = MutableStateFlow(true)
    val donateSafetyCheck5 = MutableStateFlow(true)

    // Request Dialog State
    val requestPeopleCount = MutableStateFlow(85)
    val requestPlates = MutableStateFlow(90)
    val requestTransportPref = MutableStateFlow(TransportPreference.REQUEST_TRANSPORT)
    val requestNotes = MutableStateFlow("Scheduled for lunch distribution for senior residents.")

    // Toast/Snackbar Message
    val userFeedbackMessage = MutableStateFlow<String?>(null)

    // Localization - English by default
    val currentLanguage = MutableStateFlow(LanguageCatalog.DEFAULT_LANGUAGE)
    val currentStrings = MutableStateFlow<AppStrings>(LocalizationProvider.EnglishStrings)

    fun setLanguage(language: SupportedLanguage) {
        currentLanguage.value = language
        currentStrings.value = LocalizationProvider.getStrings(language.code)
        userFeedbackMessage.value = "${language.flagEmoji} ${language.nativeName} (${language.englishName})"
    }

    // AI Chat Assistant
    private val _aiMessages = MutableStateFlow(
        listOf(
            AiChatMessage(
                sender = "ai",
                text = "Hello! I am FoodLoop AI Assistant. I can help coordinate food redistribution, query active donations, check urgent expirations, or forecast community demand. How can I assist you today?"
            )
        )
    )
    val aiMessages: StateFlow<List<AiChatMessage>> = _aiMessages.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }

        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    repository.getNotificationsForUser(user.id, user.role).collect { notifs ->
                        // Strict isolation: only notifications for this specific user or broad alerts matching role
                        _notifications.value = notifs.filter { n ->
                            n.recipientUserId == user.id || (n.recipientUserId == "ALL" && n.recipientRole == user.role)
                        }
                    }
                } else {
                    _notifications.value = emptyList()
                }
            }
        }

        viewModelScope.launch {
            activeChatRequestId.collect { reqId ->
                if (reqId != null) {
                    repository.getChatMessages(reqId).collect { msgs ->
                        _chatMessages.value = msgs
                    }
                } else {
                    _chatMessages.value = emptyList()
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        val user = _currentUser.value
        if (user != null) {
            // Rule 5: Cannot go back to Home (LANDING) or auth portal chooser (LOGIN, REGISTER) while signed in
            if (screen == AppScreen.LANDING || screen == AppScreen.LOGIN || screen == AppScreen.REGISTER) {
                userFeedbackMessage.value = "You are currently signed in as ${user.name}. Only by signing out can you return to Home."
                return
            }
            // Rule 4: If signed in to donor, cannot go to consumer screens or consumer sign in until logging out
            if (user.role == UserRole.DONOR) {
                if (screen == AppScreen.CONSUMER_LOGIN || screen == AppScreen.CONSUMER_REGISTER ||
                    screen == AppScreen.CONSUMER_AUTH || screen == AppScreen.CONSUMER_DASHBOARD ||
                    screen == AppScreen.CONSUMER_PROFILE) {
                    userFeedbackMessage.value = "Active Donor Session: You must log out before opening Care Home accounts."
                    return
                }
            }
            // Rule 4: If signed in to consumer, cannot go to donor screens or donor sign in until logging out
            if (user.role == UserRole.CONSUMER) {
                if (screen == AppScreen.DONOR_LOGIN || screen == AppScreen.DONOR_REGISTER ||
                    screen == AppScreen.DONOR_AUTH || screen == AppScreen.DONOR_DASHBOARD ||
                    screen == AppScreen.DONOR_PROFILE || screen == AppScreen.DONATE_FOOD ||
                    screen == AppScreen.DONATION_PREVIEW) {
                    userFeedbackMessage.value = "Active Care Home Session: You must log out before opening Donor accounts."
                    return
                }
            }
        }
        val currentList = _screenStack.value.toMutableList()
        if (currentList.lastOrNull() != screen) {
            currentList.add(screen)
            _screenStack.value = currentList
        }
    }

    fun navigateBack(): Boolean {
        val user = _currentUser.value
        val currentList = _screenStack.value.toMutableList()
        if (currentList.size > 1) {
            val previousScreen = currentList[currentList.size - 2]
            // Rule 5: If user is logged in, cannot go back to LANDING or LOGIN without signing out
            if (user != null && (previousScreen == AppScreen.LANDING || previousScreen == AppScreen.LOGIN || previousScreen == AppScreen.REGISTER)) {
                userFeedbackMessage.value = "Please use 'Sign Out' to leave your account and return to Home."
                return false
            }
            currentList.removeAt(currentList.lastIndex)
            _screenStack.value = currentList
            return true
        }
        if (user != null) {
            userFeedbackMessage.value = "Please use 'Sign Out' to return to Home."
        }
        return false
    }

    fun logout() {
        _currentUser.value = null
        _notifications.value = emptyList()
        _screenStack.value = listOf(AppScreen.LANDING)
        userFeedbackMessage.value = "Signed out successfully. Returned to Home."
    }

    fun switchRole(role: UserRole) {
        val current = _currentUser.value
        if (current != null && current.role != role) {
            userFeedbackMessage.value = "Account Locked: You are signed in as ${current.name}. You must sign out first to open other accounts."
            return
        }
        viewModelScope.launch {
            when (role) {
                UserRole.DONOR -> {
                    val donor = allUsers.value.firstOrNull { it.id == "donor_abc_inst" }
                    val user = donor ?: UserEntity(
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
                        verificationStatus = VerificationStatus.VERIFIED
                    )
                    _currentUser.value = user
                    _screenStack.value = listOf(AppScreen.DONOR_DASHBOARD)
                }
                UserRole.CONSUMER -> {
                    val consumer = allUsers.value.firstOrNull { it.id == "consumer_abc_oldage" }
                    val user = consumer ?: UserEntity(
                        id = "consumer_abc_oldage",
                        name = "ABC Old Age Home",
                        email = "care@abcoldagehome.org",
                        phone = "+91 98665 11223",
                        role = UserRole.CONSUMER,
                        consumerType = ConsumerType.OLD_AGE_HOME,
                        address = "Shanti Nagar, Near Clock Tower, Narasaraopet",
                        city = "Narasaraopet",
                        numberOfResidents = 85,
                        verificationStatus = VerificationStatus.VERIFIED
                    )
                    _currentUser.value = user
                    _screenStack.value = listOf(AppScreen.CONSUMER_DASHBOARD)
                }
                UserRole.ADMIN -> {
                    val admin = allUsers.value.firstOrNull { it.id == "admin_super" }
                    val user = admin ?: UserEntity(
                        id = "admin_super",
                        name = "FoodLoop District Administrator",
                        email = "admin@foodloop.ai",
                        phone = "+91 90000 00001",
                        role = UserRole.ADMIN,
                        address = "District Coordination Hub, Narasaraopet",
                        city = "Narasaraopet",
                        verificationStatus = VerificationStatus.VERIFIED
                    )
                    _currentUser.value = user
                    _screenStack.value = listOf(AppScreen.ADMIN_DASHBOARD)
                }
            }
        }
    }

    fun selectDonation(id: String) {
        selectedDonationId.value = id
        navigateTo(AppScreen.DONATION_DETAILS)
    }

    fun selectRequest(id: String) {
        selectedRequestId.value = id
        navigateTo(AppScreen.ORDER_TRACKING)
    }

    // Donor Donation Publish
    fun publishDonation() {
        val user = _currentUser.value ?: return
        val count = donatePlates.value.toIntOrNull() ?: 100
        val donationId = "FL-DON-" + System.currentTimeMillis().toString().takeLast(4)

        val donation = DonationEntity(
            id = donationId,
            donorId = user.id,
            donorName = user.name,
            donorVerified = user.verificationStatus == VerificationStatus.VERIFIED,
            donorType = user.donorType ?: DonorType.INSTITUTIONAL,
            city = donateCity.value,
            area = donateArea.value,
            pickupAddress = donatePickupAddress.value,
            mealPeriod = donateMealPeriod.value,
            totalPlates = count,
            remainingPlates = count,
            reservedPlates = 0,
            menuSummary = donateMenuItems.value.joinToString(", "),
            isVeg = donateIsVeg.value,
            availableUntilTime = donateAvailableUntil.value,
            expiryTimestamp = System.currentTimeMillis() + (3 * 3600 * 1000),
            isLateWindow = false,
            status = DonationStatus.PUBLISHED,
            hygieneConfirmed = donateSafetyCheck1.value && donateSafetyCheck2.value,
            distanceKm = 1.8f
        )

        viewModelScope.launch {
            repository.createDonation(donation)
            userFeedbackMessage.value = "Donation published successfully! Nearby recipients notified."
            selectedDonationId.value = donationId
            donateStep.value = 1
            navigateTo(AppScreen.DONOR_DASHBOARD)
        }
    }

    // Consumer Request Placement
    fun submitFoodRequest() {
        val user = _currentUser.value ?: return
        val donationId = selectedDonationId.value ?: return

        val donation = allDonations.value.firstOrNull { it.id == donationId }
        if (donation != null && !donation.isBookingWindowOpen()) {
            userFeedbackMessage.value = "Ordering window closed: The booking time (${donation.availableUntilTime}) has completed. New requests cannot be placed."
            return
        }

        viewModelScope.launch {
            val result = repository.placeFoodRequest(
                donationId = donationId,
                consumer = user,
                peopleCount = requestPeopleCount.value,
                requestedPlates = requestPlates.value,
                transportPreference = requestTransportPref.value,
                notes = requestNotes.value
            )
            result.onSuccess { request ->
                userFeedbackMessage.value = "Food request placed: ${request.id}. Awaiting donor acceptance."
                selectedRequestId.value = request.id
                navigateTo(AppScreen.ORDER_TRACKING)
            }.onFailure { err ->
                userFeedbackMessage.value = "Error placing request: ${err.message}"
            }
        }
    }

    // Donor actions
    fun acceptRequest(requestId: String) {
        viewModelScope.launch {
            val result = repository.acceptRequest(requestId)
            result.onSuccess {
                activeChatRequestId.value = requestId
                userFeedbackMessage.value = "Request accepted! Coordination chat initiated."
            }.onFailure { err ->
                userFeedbackMessage.value = "Acceptance failed: ${err.message}"
            }
        }
    }

    fun declineRequest(requestId: String, reason: String = "Capacity limit reached") {
        viewModelScope.launch {
            val result = repository.declineRequest(requestId, reason)
            result.onSuccess {
                userFeedbackMessage.value = "Request declined."
            }
        }
    }

    fun openChatForRequest(requestId: String) {
        activeChatRequestId.value = requestId
        selectedRequestId.value = requestId
        navigateTo(AppScreen.COORDINATION_CHAT)
    }

    fun sendChatMessage(text: String) {
        val reqId = activeChatRequestId.value ?: return
        val user = currentUser.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendChatMessage(
                requestId = reqId,
                senderId = user.id,
                senderName = user.name,
                senderRole = user.role,
                message = text.trim()
            )
        }
    }

    fun advanceOrderTracking(requestId: String, currentStatus: RequestStatus) {
        viewModelScope.launch {
            if (currentStatus == RequestStatus.PENDING) {
                acceptRequest(requestId)
                return@launch
            }
            val nextStatus = when (currentStatus) {
                RequestStatus.ACCEPTED -> RequestStatus.READY_FOR_PICKUP
                RequestStatus.READY_FOR_PICKUP -> RequestStatus.IN_TRANSIT
                RequestStatus.IN_TRANSIT -> RequestStatus.RECEIVED
                RequestStatus.RECEIVED -> RequestStatus.COMPLETED
                else -> currentStatus
            }
            if (nextStatus == RequestStatus.COMPLETED) {
                repository.confirmReceipt(requestId)
                navigateTo(AppScreen.THANK_YOU)
            } else {
                repository.updateRequestStatus(requestId, nextStatus)
            }
        }
    }

    fun confirmReceipt(requestId: String) {
        viewModelScope.launch {
            val res = repository.confirmReceipt(requestId)
            res.onSuccess {
                userFeedbackMessage.value = "Receipt confirmed! Thank you."
                navigateTo(AppScreen.THANK_YOU)
            }
        }
    }

    // Admin Verification
    fun adminVerifyUser(userId: String, status: VerificationStatus, notes: String) {
        viewModelScope.launch {
            repository.verifyOrganization(userId, status, notes)
            userFeedbackMessage.value = "Organization status updated to ${status.displayName}."
        }
    }

    // Interactive AI Query Engine
    fun sendAiPrompt(prompt: String) {
        val userMsg = AiChatMessage(sender = "user", text = prompt)
        _aiMessages.value = _aiMessages.value + userMsg

        val lower = prompt.lowercase()
        val donations = allDonations.value
        val requests = allRequests.value

        val reply = when {
            "lunch" in lower -> {
                val lunchDonations = donations.filter { it.mealPeriod == MealPeriod.LUNCH && (it.status == DonationStatus.PUBLISHED || it.status == DonationStatus.PARTIALLY_RESERVED) }
                if (lunchDonations.isNotEmpty()) {
                    val summary = lunchDonations.joinToString("\n") { "• ${it.donorName}: ${it.remainingPlates} plates remaining (${it.menuSummary}) available until ${it.availableUntilTime} (${it.distanceKm} km away)" }
                    "Found ${lunchDonations.size} active Lunch donation(s) in Narasaraopet:\n$summary"
                } else {
                    "Currently no surplus lunch donations open. The next window opens around dinner time (7:30 PM)."
                }
            }
            "expire" in lower || "urgency" in lower || "soon" in lower -> {
                val expiring = donations.filter { it.status == DonationStatus.PUBLISHED || it.status == DonationStatus.PARTIALLY_RESERVED }
                if (expiring.isNotEmpty()) {
                    val first = expiring.first()
                    "Urgency Alert: Donation ${first.id} from ${first.donorName} (${first.remainingPlates} plates) is scheduled until ${first.availableUntilTime}. Smart Matching recommends dispatching a transport vehicle within 30 minutes to avoid quality degradation."
                } else {
                    "All current donations have ample safety windows."
                }
            }
            "plates" in lower || "available" in lower || "quantity" in lower -> {
                val totalRemaining = donations.filter { it.status == DonationStatus.PUBLISHED || it.status == DonationStatus.PARTIALLY_RESERVED }.sumOf { it.remainingPlates }
                val totalAll = donations.sumOf { it.totalPlates }
                "Currently, there are $totalRemaining plates immediately available across ${donations.count { it.status == DonationStatus.PUBLISHED || it.status == DonationStatus.PARTIALLY_RESERVED }} verified locations. Cumulative redistribution across the platform stands at $totalAll plates."
            }
            "request" in lower || "history" in lower || "previous" in lower -> {
                val user = currentUser.value
                val myReqs = if (user != null) requests.filter { it.consumerId == user.id || it.donorId == user.id } else requests
                if (myReqs.isNotEmpty()) {
                    val list = myReqs.take(3).joinToString("\n") { "• Request ${it.id}: ${it.requestedPlates} plates (${it.consumerName}) -> Status: ${it.status.displayName}" }
                    "Here are recent coordination requests:\n$list"
                } else {
                    "No previous requests found for the active profile."
                }
            }
            "demand" in lower || "predict" in lower || "forecast" in lower -> {
                "AI Demand Forecasting for Narasaraopet Cluster:\n• ABC Old Age Home: ~85 plates required by 1:00 PM\n• Sunrise Child Welfare: ~60 plates required by 1:30 PM\n• Projected dinner surplus: ~140 plates from Arundelpet banquet hall.\nRecommendation: Coordinate early collection to prevent late window warnings."
            }
            else -> {
                "FoodLoop AI analyzed the platform state: ${donations.count { it.status == DonationStatus.PUBLISHED || it.status == DonationStatus.PARTIALLY_RESERVED }} donations active, ${requests.size} requests tracked. You can ask me to: 'Show nearby lunch donations', 'Check expiring donations', or 'Predict today's meal demand'."
            }
        }

        _aiMessages.value = _aiMessages.value + AiChatMessage(sender = "ai", text = reply)
    }

    fun clearFeedback() {
        userFeedbackMessage.value = null
    }

    fun registerUser(user: UserEntity) {
        viewModelScope.launch {
            repository.registerUser(user)
            _currentUser.value = user
            userFeedbackMessage.value = "Registered successfully. Awaiting administrative inspection."
            when (user.role) {
                UserRole.DONOR -> _screenStack.value = listOf(AppScreen.DONOR_DASHBOARD)
                UserRole.CONSUMER -> _screenStack.value = listOf(AppScreen.CONSUMER_DASHBOARD)
                UserRole.ADMIN -> _screenStack.value = listOf(AppScreen.ADMIN_DASHBOARD)
            }
        }
    }

    fun donorLogin(email: String, passwordInput: String = ""): String? {
        val trimmed = email.trim()
        val trimmedPass = passwordInput.trim()

        if (trimmed.isBlank()) {
            return "Please enter a valid email address."
        }
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (!emailRegex.matches(trimmed)) {
            return "Please enter a valid email address format (e.g. user@domain.com)."
        }

        val users = allUsers.value
        val existing = users.firstOrNull { it.email.equals(trimmed, ignoreCase = true) }
        if (existing != null) {
            if (existing.role == UserRole.CONSUMER) {
                return "Privacy Shield: This account is registered as a Recipient Care Home (${existing.name}). Please use the Care Home Portal."
            }
            if (existing.role == UserRole.ADMIN) {
                return "Administrative account detected. Please use the District Administrator Portal."
            }
            if (trimmedPass.isNotBlank() && existing.password.isNotBlank() && trimmedPass != "••••••••") {
                if (existing.password != trimmedPass) {
                    return "Incorrect password for this donor account. Please check your password."
                }
            }
            _currentUser.value = existing
            userFeedbackMessage.value = "Welcome back, ${existing.name} (${existing.donorType?.displayName ?: "Food Donor"})!"
            _screenStack.value = listOf(AppScreen.DONOR_DASHBOARD)
            return null
        } else {
            // Instant onboarding for any commoner/donor with valid email
            val newDonor = UserEntity(
                id = "donor_" + System.currentTimeMillis().toString().takeLast(5),
                name = trimmed.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() } + " (Donor)",
                email = trimmed,
                password = if (trimmedPass.isNotBlank()) trimmedPass else "pass123",
                phone = "+91 98480 00000",
                role = UserRole.DONOR,
                donorType = DonorType.COMMONER,
                institutionSubtype = InstitutionSubtype.COMMON_CITIZEN,
                institutionName = "Commoner / Food Donor",
                address = "Narasaraopet",
                city = "Narasaraopet",
                verificationStatus = VerificationStatus.VERIFIED
            )
            viewModelScope.launch {
                repository.registerUser(newDonor)
            }
            _currentUser.value = newDonor
            userFeedbackMessage.value = "Registered & signed in as ${newDonor.name}."
            _screenStack.value = listOf(AppScreen.DONOR_DASHBOARD)
            return null
        }
    }

    fun consumerLogin(email: String): String? {
        val trimmed = email.trim()
        val users = allUsers.value
        val existing = users.firstOrNull { it.email.equals(trimmed, ignoreCase = true) }
        if (existing != null) {
            if (existing.role == UserRole.DONOR) {
                return "Privacy Shield: This account is registered as a Food Donor (${existing.name}). Please use the Food Donor Portal."
            }
            if (existing.role == UserRole.ADMIN) {
                return "Administrative account detected. Please use the District Administrator Portal."
            }
            _currentUser.value = existing
            userFeedbackMessage.value = "Welcome back, ${existing.name}!"
            _screenStack.value = listOf(AppScreen.CONSUMER_DASHBOARD)
            return null
        } else {
            // Demo account fallback or auto-create consumer
            val defaultConsumer = users.firstOrNull { it.role == UserRole.CONSUMER } ?: UserEntity(
                id = "consumer_" + System.currentTimeMillis().toString().takeLast(5),
                name = trimmed.substringBefore("@").replace(".", " ").capitalize() + " Care Home",
                email = trimmed,
                phone = "+91 98665 00000",
                role = UserRole.CONSUMER,
                consumerType = ConsumerType.OLD_AGE_HOME,
                numberOfResidents = 75,
                address = "Narasaraopet",
                city = "Narasaraopet",
                verificationStatus = VerificationStatus.VERIFIED
            )
            _currentUser.value = defaultConsumer
            userFeedbackMessage.value = "Signed in as ${defaultConsumer.name}."
            _screenStack.value = listOf(AppScreen.CONSUMER_DASHBOARD)
            return null
        }
    }

    fun selectUserAccount(user: UserEntity) {
        val current = _currentUser.value
        if (current != null && current.id != user.id) {
            userFeedbackMessage.value = "Account Locked: You are currently signed in as ${current.name}. You must sign out first to open other accounts."
            return
        }
        _currentUser.value = user
        userFeedbackMessage.value = "Signed in as ${user.name}."
        when (user.role) {
            UserRole.DONOR -> _screenStack.value = listOf(AppScreen.DONOR_DASHBOARD)
            UserRole.CONSUMER -> _screenStack.value = listOf(AppScreen.CONSUMER_DASHBOARD)
            UserRole.ADMIN -> _screenStack.value = listOf(AppScreen.ADMIN_DASHBOARD)
        }
    }

    fun registerNewDonor(
        name: String,
        email: String,
        password: String = "pass123",
        phone: String,
        donorType: DonorType,
        institutionSubtype: InstitutionSubtype,
        address: String,
        city: String,
        docName: String
    ) {
        val newId = "donor_" + System.currentTimeMillis().toString().takeLast(5)
        val newUser = UserEntity(
            id = newId,
            name = name,
            email = email,
            password = if (password.isNotBlank()) password else "pass123",
            phone = phone,
            role = UserRole.DONOR,
            donorType = donorType,
            institutionSubtype = institutionSubtype,
            institutionName = name,
            address = address,
            city = city,
            verificationStatus = VerificationStatus.VERIFIED,
            verificationNotes = "Registered food donor ($name). Verified email: $email.",
            documentName = docName
        )
        registerUser(newUser)
        navigateTo(AppScreen.DONOR_DASHBOARD)
    }

    fun registerNewConsumer(
        name: String,
        email: String,
        phone: String,
        consumerType: ConsumerType,
        residentsCount: Int,
        address: String,
        city: String,
        docName: String
    ) {
        val newId = "consumer_" + System.currentTimeMillis().toString().takeLast(5)
        val newUser = UserEntity(
            id = newId,
            name = name,
            email = email,
            phone = phone,
            role = UserRole.CONSUMER,
            consumerType = consumerType,
            numberOfResidents = residentsCount,
            address = address,
            city = city,
            verificationStatus = VerificationStatus.PENDING,
            verificationNotes = "New care home registered. Trust documents and resident headcount under verification.",
            documentName = docName
        )
        registerUser(newUser)
        navigateTo(AppScreen.CONSUMER_DASHBOARD)
    }

    fun adminLogin(passcode: String): Boolean {
        if (passcode == "admin123" || passcode.isBlank()) {
            val adminUser = allUsers.value.firstOrNull { it.id == "admin_super" } ?: UserEntity(
                id = "admin_super",
                name = "FoodLoop District Administrator",
                email = "admin@foodloop.ai",
                phone = "+91 90000 00001",
                role = UserRole.ADMIN,
                address = "District Coordination Hub, Narasaraopet",
                city = "Narasaraopet",
                verificationStatus = VerificationStatus.VERIFIED
            )
            _currentUser.value = adminUser
            userFeedbackMessage.value = "Administrator authenticated."
            navigateTo(AppScreen.ADMIN_DASHBOARD)
            return true
        } else {
            userFeedbackMessage.value = "Invalid administrator credentials."
            return false
        }
    }

    fun markAllNotificationsRead() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.markAllNotificationsRead(user.id, user.role)
        }
    }
}
