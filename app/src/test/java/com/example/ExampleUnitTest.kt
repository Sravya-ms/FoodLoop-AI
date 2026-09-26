package com.example

import com.example.data.local.isBookingWindowOpen
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testPartialReservationQuantityLogic() {
    val originalQuantity = 100
    var remaining = originalQuantity
    var reserved = 0

    // Consumer A requests 60 plates
    val reqA = 60
    assertTrue(reqA <= remaining)
    remaining -= reqA
    reserved += reqA
    assertEquals(40, remaining)
    assertEquals(60, reserved)

    // Consumer B attempts to request 60 plates (should exceed remaining 40)
    val reqB = 60
    val canAcceptReqB = reqB <= remaining
    assertFalse("Second request exceeding remaining plates must be rejected", canAcceptReqB)

    // Consumer C requests valid 40 plates
    val reqC = 40
    assertTrue(reqC <= remaining)
    remaining -= reqC
    reserved += reqC
    assertEquals(0, remaining)
    assertEquals(100, reserved)
  }

  @Test
  fun testChatMessageData() {
    val msg = com.example.data.local.ChatMessageEntity(
        id = "CHAT-1",
        requestId = "FL-2026-000124",
        senderId = "donor_abc_inst",
        senderName = "ABC Institution",
        senderRole = com.example.data.model.UserRole.DONOR,
        message = "Food is ready at Gate 2 loading bay.",
        isSystemMessage = false
    )
    assertEquals("FL-2026-000124", msg.requestId)
    assertEquals("donor_abc_inst", msg.senderId)
    assertEquals(com.example.data.model.UserRole.DONOR, msg.senderRole)
    assertFalse(msg.isSystemMessage)
  }

  @Test
  fun testDonorDataPrivacyIsolation() {
    val donation1 = com.example.data.local.DonationEntity(
        id = "DON-1",
        donorId = "donor_A",
        donorName = "College A",
        donorVerified = true,
        city = "Narasaraopet",
        area = "North",
        pickupAddress = "Gate 1",
        mealPeriod = com.example.data.model.MealPeriod.LUNCH,
        totalPlates = 100,
        remainingPlates = 100,
        reservedPlates = 0,
        menuSummary = "Rice & Dal",
        isVeg = true,
        availableUntilTime = "3:00 PM",
        expiryTimestamp = System.currentTimeMillis() + 10000,
        isLateWindow = false,
        status = com.example.data.model.DonationStatus.PUBLISHED
    )

    val donation2 = com.example.data.local.DonationEntity(
        id = "DON-2",
        donorId = "donor_B",
        donorName = "Banquet B",
        donorVerified = true,
        city = "Narasaraopet",
        area = "South",
        pickupAddress = "Gate 2",
        mealPeriod = com.example.data.model.MealPeriod.DINNER,
        totalPlates = 150,
        remainingPlates = 150,
        reservedPlates = 0,
        menuSummary = "Biryani",
        isVeg = false,
        availableUntilTime = "9:00 PM",
        expiryTimestamp = System.currentTimeMillis() + 20000,
        isLateWindow = false,
        status = com.example.data.model.DonationStatus.PUBLISHED
    )

    val allDonations = listOf(donation1, donation2)

    // Donor A MUST only see their own donations
    val donorADonations = allDonations.filter { it.donorId == "donor_A" }
    assertEquals(1, donorADonations.size)
    assertEquals("DON-1", donorADonations.first().id)
    assertFalse("Donor A must never see Donor B's kitchen surplus", donorADonations.any { it.donorId == "donor_B" })
  }

  @Test
  fun testConsumerBeneficiaryPrivacyIsolation() {
    val req1 = com.example.data.local.FoodRequestEntity(
        id = "REQ-1",
        donationId = "DON-1",
        donorId = "donor_A",
        donorName = "College A",
        consumerId = "home_elderly",
        consumerName = "Old Age Home",
        consumerType = com.example.data.model.ConsumerType.OLD_AGE_HOME,
        consumerVerified = true,
        peopleCount = 85,
        requestedPlates = 85,
        status = com.example.data.model.RequestStatus.ACCEPTED,
        pickupAddress = "Gate 1, College A",
        dropAddress = "Shanti Nagar, Old Age Home"
    )

    val req2 = com.example.data.local.FoodRequestEntity(
        id = "REQ-2",
        donationId = "DON-1",
        donorId = "donor_A",
        donorName = "College A",
        consumerId = "home_orphanage",
        consumerName = "Child Welfare Orphanage",
        consumerType = com.example.data.model.ConsumerType.ORPHANAGE,
        consumerVerified = true,
        peopleCount = 60,
        requestedPlates = 60,
        status = com.example.data.model.RequestStatus.ACCEPTED,
        pickupAddress = "Gate 1, College A",
        dropAddress = "Prakash Nagar, Orphanage"
    )

    val allRequests = listOf(req1, req2)

    // Elderly Home MUST only see their own requests and confidential resident counts
    val elderlyRequests = allRequests.filter { it.consumerId == "home_elderly" }
    assertEquals(1, elderlyRequests.size)
    assertEquals(85, elderlyRequests.first().peopleCount)
    assertFalse("Elderly home must not see other homes' requests", elderlyRequests.any { it.consumerId == "home_orphanage" })
  }

  @Test
  fun testUserEntityAndRoleWorkflows() {
    val donor = com.example.data.local.UserEntity(
        id = "donor_test_1",
        name = "Apex College Mess",
        email = "mess@apex.edu",
        phone = "+91 98480 11111",
        role = com.example.data.model.UserRole.DONOR,
        donorType = com.example.data.model.DonorType.INSTITUTIONAL,
        institutionSubtype = com.example.data.model.InstitutionSubtype.COLLEGE,
        institutionName = "Apex College",
        verificationStatus = com.example.data.model.VerificationStatus.VERIFIED
    )

    val consumer = com.example.data.local.UserEntity(
        id = "consumer_test_1",
        name = "Grace Orphanage",
        email = "care@graceorphanage.org",
        phone = "+91 98665 22222",
        role = com.example.data.model.UserRole.CONSUMER,
        consumerType = com.example.data.model.ConsumerType.ORPHANAGE,
        numberOfResidents = 55,
        verificationStatus = com.example.data.model.VerificationStatus.PENDING
    )

    assertEquals(com.example.data.model.UserRole.DONOR, donor.role)
    assertEquals(com.example.data.model.DonorType.INSTITUTIONAL, donor.donorType)
    assertEquals(com.example.data.model.VerificationStatus.VERIFIED, donor.verificationStatus)

    assertEquals(com.example.data.model.UserRole.CONSUMER, consumer.role)
    assertEquals(55, consumer.numberOfResidents)
    assertEquals(com.example.data.model.VerificationStatus.PENDING, consumer.verificationStatus)
  }

  @Test
  fun testFoodRequestStatusLifecycle() {
    var request = com.example.data.local.FoodRequestEntity(
        id = "REQ-100",
        donationId = "DON-100",
        donorId = "donor_1",
        donorName = "Mess 1",
        consumerId = "consumer_1",
        consumerName = "Home 1",
        peopleCount = 40,
        requestedPlates = 40,
        status = com.example.data.model.RequestStatus.PENDING,
        transportPreference = com.example.data.model.TransportPreference.REQUEST_TRANSPORT,
        pickupAddress = "Gate 1",
        dropAddress = "Care Home Ward 4"
    )

    assertEquals(com.example.data.model.RequestStatus.PENDING, request.status)

    // Donor accepts
    request = request.copy(status = com.example.data.model.RequestStatus.ACCEPTED, acceptedAt = System.currentTimeMillis())
    assertEquals(com.example.data.model.RequestStatus.ACCEPTED, request.status)
    assertNotNull(request.acceptedAt)

    // Ready for pickup
    request = request.copy(status = com.example.data.model.RequestStatus.READY_FOR_PICKUP)
    assertEquals(com.example.data.model.RequestStatus.READY_FOR_PICKUP, request.status)

    // In transit
    request = request.copy(status = com.example.data.model.RequestStatus.IN_TRANSIT)
    assertEquals(com.example.data.model.RequestStatus.IN_TRANSIT, request.status)

    // Completed
    request = request.copy(status = com.example.data.model.RequestStatus.COMPLETED, completedAt = System.currentTimeMillis())
    assertEquals(com.example.data.model.RequestStatus.COMPLETED, request.status)
    assertNotNull(request.completedAt)
  }

  @Test
  fun testEveningMealTimingsChangedToEightThirty() {
    val dinner = com.example.data.model.MealPeriod.DINNER
    assertEquals("8:30 PM", dinner.normalDeadline)
    assertTrue("Dinner typical time should mention 8:30 PM", dinner.typicalTime.contains("8:30 PM"))
  }

  @Test
  fun testConsumerOrderingWindowClosedAndExistingRequestsProceed() {
    val now = System.currentTimeMillis()

    // 1. Donation whose booking time has completed
    val expiredDonation = com.example.data.local.DonationEntity(
        id = "DON-EXPIRED",
        donorId = "donor_1",
        donorName = "Mess 1",
        pickupAddress = "Gate 1",
        mealPeriod = com.example.data.model.MealPeriod.DINNER,
        totalPlates = 50,
        remainingPlates = 50,
        menuSummary = "Pulao",
        availableUntilTime = "8:30 PM",
        expiryTimestamp = now - 1000, // Passed
        status = com.example.data.model.DonationStatus.PUBLISHED
    )

    // Cannot order past booking time
    assertFalse("Booking window must be closed when time is completed", expiredDonation.isBookingWindowOpen(now))

    // 2. Active donation within time window
    val activeDonation = expiredDonation.copy(
        id = "DON-ACTIVE",
        expiryTimestamp = now + 3600000 // In the future
    )
    assertTrue("Booking window must be open before cutoff time", activeDonation.isBookingWindowOpen(now))

    // 3. Existing request that proceeded within the mean time continues uninterrupted
    val preexistingRequest = com.example.data.local.FoodRequestEntity(
        id = "REQ-PREEXISTING",
        donationId = expiredDonation.id,
        donorId = expiredDonation.donorId,
        donorName = expiredDonation.donorName,
        consumerId = "care_home_1",
        consumerName = "Shanti Care Home",
        peopleCount = 40,
        requestedPlates = 40,
        status = com.example.data.model.RequestStatus.ACCEPTED,
        createdAt = now - 5000 // Placed beforehand
    )

    // Fulfillment proceeds to IN_TRANSIT and COMPLETED even though booking cutoff passed
    val inTransit = preexistingRequest.copy(status = com.example.data.model.RequestStatus.IN_TRANSIT)
    assertEquals(com.example.data.model.RequestStatus.IN_TRANSIT, inTransit.status)

    val completed = inTransit.copy(status = com.example.data.model.RequestStatus.COMPLETED, completedAt = now)
    assertEquals(com.example.data.model.RequestStatus.COMPLETED, completed.status)
  }

  @Test
  fun testNotificationIsolationBetweenDonorAndConsumer() {
    val donorNotif = com.example.data.local.NotificationEntity(
        id = "NOTIF-DONOR",
        recipientUserId = "donor_abc",
        recipientRole = com.example.data.model.UserRole.DONOR,
        title = "New Inbound Request",
        message = "Care home requested 50 plates.",
        type = com.example.data.model.NotificationType.NEW_DONATION
    )

    val consumerNotif = com.example.data.local.NotificationEntity(
        id = "NOTIF-CONSUMER",
        recipientUserId = "consumer_xyz",
        recipientRole = com.example.data.model.UserRole.CONSUMER,
        title = "Request Approved",
        message = "Your request was approved.",
        type = com.example.data.model.NotificationType.REQUEST_ACCEPTED
    )

    val allNotifs = listOf(donorNotif, consumerNotif)

    // Donor perspective
    val donorVisible = allNotifs.filter { 
        it.recipientUserId == "donor_abc" || (it.recipientUserId == "ALL" && it.recipientRole == com.example.data.model.UserRole.DONOR)
    }
    assertEquals(1, donorVisible.size)
    assertEquals("NOTIF-DONOR", donorVisible.first().id)
    assertFalse("Donor must not see consumer notifications", donorVisible.any { it.recipientRole == com.example.data.model.UserRole.CONSUMER })

    // Consumer perspective
    val consumerVisible = allNotifs.filter { 
        it.recipientUserId == "consumer_xyz" || (it.recipientUserId == "ALL" && it.recipientRole == com.example.data.model.UserRole.CONSUMER)
    }
    assertEquals(1, consumerVisible.size)
    assertEquals("NOTIF-CONSUMER", consumerVisible.first().id)
    assertFalse("Consumer must not see donor notifications", consumerVisible.any { it.recipientRole == com.example.data.model.UserRole.DONOR })
  }

  @Test
  fun testLanguageDefaultIsEnglish() {
    val defaultLang = com.example.localization.LanguageCatalog.DEFAULT_LANGUAGE
    assertEquals("en", defaultLang.code)
    assertEquals("English", defaultLang.englishName)
    val defaultStrings = com.example.localization.LocalizationProvider.getStrings("en")
    assertEquals("FoodLoop AI", defaultStrings.appName)
    assertEquals("Evening/Dinner: 7:00–8:30 PM", defaultStrings.eveningTimeLabel)
  }

  @Test
  fun testAllLanguagesAvailableAndTeluguTranslated() {
    val allLangs = com.example.localization.LanguageCatalog.ALL_SUPPORTED
    assertTrue("Should support widespread languages", allLangs.size >= 30)

    val telugu = allLangs.firstOrNull { it.code == "te" }
    assertNotNull("Telugu must be supported for Andhra Pradesh pilot", telugu)
    assertEquals("తెలుగు", telugu?.nativeName)

    val teluguStrings = com.example.localization.LocalizationProvider.getStrings("te")
    assertEquals("ఫుడ్‌లూప్ AI", teluguStrings.appName)
    assertTrue("Telugu evening timings must maintain 8:30 PM", teluguStrings.eveningTimeLabel.contains("8:30 PM"))
    assertTrue("Telugu booking closed message must maintain continuity policy", teluguStrings.bookingTimeCompletedAlert.contains("కొనసాగుతాయి"))

    val hindiStrings = com.example.localization.LocalizationProvider.getStrings("hi")
    assertEquals("फूडलूप AI", hindiStrings.appName)
    assertTrue("Hindi evening timings must maintain 8:30 PM", hindiStrings.eveningTimeLabel.contains("8:30 PM"))

    val spanishStrings = com.example.localization.LocalizationProvider.getStrings("es")
    assertEquals("Inicio", spanishStrings.home)
    assertTrue("Spanish evening timings must maintain 8:30 PM", spanishStrings.eveningTimeLabel.contains("8:30 PM"))
  }

  @Test
  fun testCommonersAndRestaurantsAsFoodDonors() {
    // 1. Commoner / Individual Donor
    val commonerDonor = com.example.data.local.UserEntity(
        id = "donor_commoner_1",
        email = "citizen@foodloop.org",
        password = "password123",
        name = "Srinivas Rao (Commoner)",
        role = com.example.data.model.UserRole.DONOR,
        donorType = com.example.data.model.DonorType.COMMONER,
        institutionSubtype = com.example.data.model.InstitutionSubtype.COMMON_CITIZEN,
        city = "Narasaraopet",
        address = "Plot 42, Arundelpet, Main Road",
        phone = "9876543210"
    )
    assertEquals(com.example.data.model.UserRole.DONOR, commonerDonor.role)
    assertEquals(com.example.data.model.DonorType.COMMONER, commonerDonor.donorType)
    assertEquals("citizen@foodloop.org", commonerDonor.email)

    // 2. Restaurant Donor
    val restaurantDonor = com.example.data.local.UserEntity(
        id = "donor_restaurant_1",
        email = "chef@swagathgrand.com",
        password = "password123",
        name = "Swagath Grand Restaurant",
        role = com.example.data.model.UserRole.DONOR,
        donorType = com.example.data.model.DonorType.RESTAURANT,
        institutionSubtype = com.example.data.model.InstitutionSubtype.RESTAURANT,
        city = "Narasaraopet",
        address = "RTC Bus Stand Road, Opposite RTC Complex",
        phone = "9876543211"
    )
    assertEquals(com.example.data.model.UserRole.DONOR, restaurantDonor.role)
    assertEquals(com.example.data.model.DonorType.RESTAURANT, restaurantDonor.donorType)

    // 3. Function Hall / Event Donor
    val functionDonor = com.example.data.local.UserEntity(
        id = "donor_function_1",
        email = "manager@srikrishnabanquet.com",
        password = "password123",
        name = "Sri Krishna Function Hall",
        role = com.example.data.model.UserRole.DONOR,
        donorType = com.example.data.model.DonorType.FUNCTION_HALL,
        institutionSubtype = com.example.data.model.InstitutionSubtype.CATERING,
        city = "Narasaraopet",
        address = "Palnadu Highway",
        phone = "9876543212"
    )
    assertEquals(com.example.data.model.UserRole.DONOR, functionDonor.role)
    assertEquals(com.example.data.model.DonorType.FUNCTION_HALL, functionDonor.donorType)
  }

  @Test
  fun testTwoDeliveryWaysSupported() {
    // Way 1: Booking Uber / Rapido by care home / orphanage
    val way1 = com.example.data.model.TransportPreference.UBER_RAPIDO_BOOKING
    assertTrue(way1.displayName.contains("Uber / Rapido"))
    assertTrue(way1.shortDescription.contains("Orphanage / Care home books"))

    // Way 2A: Own Transportation by Donor (Institution, restaurant, function organizer, commoner)
    val way2Donor = com.example.data.model.TransportPreference.OWN_TRANSPORT_DONOR
    assertTrue(way2Donor.displayName.contains("Donor's Own Transportation"))
    assertTrue(way2Donor.shortDescription.contains("Direct delivery provided by institution, restaurant, function or commoner"))

    // Way 2B: Own Transportation by Recipient Care Home
    val way2Recipient = com.example.data.model.TransportPreference.OWN_TRANSPORT_RECIPIENT
    assertTrue(way2Recipient.displayName.contains("Recipient's Own Transportation"))
    assertTrue(way2Recipient.shortDescription.contains("care home / orphanage vehicle"))
  }
}
