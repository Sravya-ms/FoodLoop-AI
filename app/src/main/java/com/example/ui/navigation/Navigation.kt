package com.example.ui.navigation

enum class AppScreen(val title: String) {
    LANDING("FoodLoop AI"),
    LOGIN("Choose Portal to Sign In"),
    REGISTER("Choose Portal to Register"),
    DONOR_LOGIN("Donor Sign In"),
    DONOR_REGISTER("Register Food Donor"),
    CONSUMER_LOGIN("Care Home Sign In"),
    CONSUMER_REGISTER("Register Care Home"),
    DONOR_AUTH("Donor Portal"),
    CONSUMER_AUTH("Recipient Home Portal"),
    ADMIN_AUTH("Admin Login"),
    DONOR_DASHBOARD("Donor Dashboard"),
    DONATE_FOOD("Donate Food"),
    DONATION_PREVIEW("Donation Preview"),
    CONSUMER_DASHBOARD("Recipient Hub"),
    DONATION_DETAILS("Donation Details"),
    ORDER_TRACKING("Live Order Tracking"),
    COORDINATION_CHAT("Coordination Chat"),
    THANK_YOU("Impact Confirmation"),
    ADMIN_DASHBOARD("Admin Control Center"),
    ADMIN_VERIFICATION("Verification Review"),
    NOTIFICATIONS("Notifications"),
    DONOR_PROFILE("Donor Profile"),
    CONSUMER_PROFILE("Organization Profile"),
    IMPACT_DASHBOARD("Platform Impact"),
    AI_ASSISTANT("FoodLoop AI Assistant")
}
