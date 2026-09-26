package com.example.data.model

enum class UserRole(val displayName: String) {
    DONOR("Food Donor"),
    CONSUMER("Care Home"),
    ADMIN("District Administrator")
}

enum class DonorType(val displayName: String) {
    INSTITUTIONAL("Institutional Donor (Colleges / Hostels)"),
    RESTAURANT("Restaurant / Hotel / Eatery"),
    FUNCTION_HALL("Function Hall / Banquet / Event"),
    COMMONER("Commoner / Individual Donation"),
    INDIVIDUAL("Individual Donor")
}

enum class InstitutionSubtype(val displayName: String) {
    COLLEGE("College / University"),
    HOSTEL("Student / Working Hostel"),
    RESTAURANT("Restaurant / Dhaba / Hotel"),
    CATERING("Catering / Marriage Banquet Hall"),
    FAMILY_FUNCTION("Family Function / Gathering"),
    COMMON_CITIZEN("Commoner / Citizen Donation"),
    HOSPITAL("Hospital / Medical Center"),
    CORPORATE("Corporate Office"),
    RELIGIOUS("Religious / Community Center"),
    HOME_INDIVIDUAL("Home / Private Function")
}

enum class ConsumerType(val displayName: String) {
    ORPHANAGE("Orphanage"),
    OLD_AGE_HOME("Old-Age Home")
}

enum class VerificationStatus(val displayName: String) {
    PENDING("Verification Pending"),
    VERIFIED("Verified"),
    REJECTED("Rejected"),
    SUSPENDED("Suspended")
}

enum class MealPeriod(val displayName: String, val typicalTime: String, val normalDeadline: String, val lateDeadline: String) {
    BREAKFAST("Breakfast", "7:30 AM - 9:30 AM", "9:00 AM", "9:30 AM"),
    LUNCH("Lunch", "12:30 PM - 2:30 PM", "2:30 PM", "3:30 PM"),
    EVENING("Evening Snacks", "4:30 PM - 6:00 PM", "5:30 PM", "6:15 PM"),
    DINNER("Dinner / Evening", "7:00 PM - 8:30 PM", "8:30 PM", "8:30 PM")
}

enum class DonationStatus(val displayName: String) {
    DRAFT("Draft"),
    PUBLISHED("Available"),
    PARTIALLY_RESERVED("Partially Reserved"),
    FULLY_RESERVED("Fully Reserved"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    EXPIRED("Expired")
}

enum class RequestStatus(val displayName: String) {
    PENDING("Pending Donor Review"),
    ACCEPTED("Accepted by Donor"),
    DECLINED("Declined"),
    READY_FOR_PICKUP("Ready for Pickup"),
    IN_TRANSIT("In Delivery / Pickup"),
    RECEIVED("Received"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

enum class TransportPreference(val displayName: String, val shortDescription: String = "") {
    UBER_RAPIDO_BOOKING(
        "Way 1: Book Uber / Rapido (by Care Home)",
        "Orphanage / Care home books an on-demand delivery ride (Uber / Rapido / Auto)"
    ),
    OWN_TRANSPORT_DONOR(
        "Way 2A: Donor's Own Transportation",
        "Direct delivery provided by institution, restaurant, function or commoner"
    ),
    OWN_TRANSPORT_RECIPIENT(
        "Way 2B: Recipient's Own Transportation",
        "Direct pickup provided by care home / orphanage vehicle or volunteer"
    ),
    // Backward compatibility aliases
    SELF_PICKUP(
        "Way 2B: Recipient's Own Transportation",
        "Direct pickup provided by care home staff"
    ),
    REQUEST_TRANSPORT(
        "Way 1: Book Uber / Rapido (by Care Home)",
        "Orphanage / Care home books an on-demand delivery partner"
    )
}

enum class NotificationType {
    NEW_DONATION,
    URGENT_EXPIRY,
    REQUEST_ACCEPTED,
    REQUEST_DECLINED,
    IN_DELIVERY,
    RECEIVED_COMPLETED,
    SYSTEM_VERIFIED
}

data class MenuItem(
    val name: String,
    val isVeg: Boolean = true
)
