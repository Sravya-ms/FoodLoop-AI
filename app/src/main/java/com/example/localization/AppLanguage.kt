package com.example.localization

data class SupportedLanguage(
    val code: String,
    val englishName: String,
    val nativeName: String,
    val flagEmoji: String,
    val category: LanguageCategory = LanguageCategory.POPULAR,
    val isRtl: Boolean = false
)

enum class LanguageCategory {
    POPULAR,
    INDIAN,
    GLOBAL
}

object LanguageCatalog {
    val DEFAULT_LANGUAGE = SupportedLanguage(
        code = "en",
        englishName = "English",
        nativeName = "English",
        flagEmoji = "🇬🇧",
        category = LanguageCategory.POPULAR
    )

    val ALL_SUPPORTED: List<SupportedLanguage> = listOf(
        // Default
        DEFAULT_LANGUAGE,

        // Primary Indian Regional Languages (with Telugu as Andhra Pradesh pilot focus)
        SupportedLanguage("te", "Telugu", "తెలుగు", "🇮🇳", LanguageCategory.INDIAN),
        SupportedLanguage("hi", "Hindi", "हिन्दी", "🇮🇳", LanguageCategory.INDIAN),
        SupportedLanguage("ta", "Tamil", "தமிழ்", "🇮🇳", LanguageCategory.INDIAN),
        SupportedLanguage("kn", "Kannada", "ಕನ್ನಡ", "🇮🇳", LanguageCategory.INDIAN),
        SupportedLanguage("ml", "Malayalam", "മലയാളം", "🇮🇳", LanguageCategory.INDIAN),
        SupportedLanguage("mr", "Marathi", "मराठी", "🇮🇳", LanguageCategory.INDIAN),
        SupportedLanguage("bn", "Bengali", "বাংলা", "🇮🇳", LanguageCategory.INDIAN),
        SupportedLanguage("gu", "Gujarati", "ગુજરાતી", "🇮🇳", LanguageCategory.INDIAN),
        SupportedLanguage("pa", "Punjabi", "ਪੰਜਾਬੀ", "🇮🇳", LanguageCategory.INDIAN),
        SupportedLanguage("or", "Odia", "ଓଡ଼ିଆ", "🇮🇳", LanguageCategory.INDIAN),
        SupportedLanguage("ur", "Urdu", "اردو", "🇮🇳", LanguageCategory.INDIAN, isRtl = true),

        // Major World Languages
        SupportedLanguage("es", "Spanish", "Español", "🇪🇸", LanguageCategory.GLOBAL),
        SupportedLanguage("fr", "French", "Français", "🇫🇷", LanguageCategory.GLOBAL),
        SupportedLanguage("de", "German", "Deutsch", "🇩🇪", LanguageCategory.GLOBAL),
        SupportedLanguage("ar", "Arabic", "العربية", "🇸🇦", LanguageCategory.GLOBAL, isRtl = true),
        SupportedLanguage("zh", "Chinese (Simplified)", "简体中文", "🇨🇳", LanguageCategory.GLOBAL),
        SupportedLanguage("ja", "Japanese", "日本語", "🇯🇵", LanguageCategory.GLOBAL),
        SupportedLanguage("pt", "Portuguese", "Português", "🇵🇹", LanguageCategory.GLOBAL),
        SupportedLanguage("ru", "Russian", "Русский", "🇷🇺", LanguageCategory.GLOBAL),
        SupportedLanguage("it", "Italian", "Italiano", "🇮🇹", LanguageCategory.GLOBAL),
        SupportedLanguage("ko", "Korean", "한국어", "🇰🇷", LanguageCategory.GLOBAL),
        SupportedLanguage("tr", "Turkish", "Türkçe", "🇹🇷", LanguageCategory.GLOBAL),
        SupportedLanguage("vi", "Vietnamese", "Tiếng Việt", "🇻🇳", LanguageCategory.GLOBAL),
        SupportedLanguage("id", "Indonesian", "Bahasa Indonesia", "🇮🇩", LanguageCategory.GLOBAL),
        SupportedLanguage("sw", "Swahili", "Kiswahili", "🇰🇪", LanguageCategory.GLOBAL),
        SupportedLanguage("nl", "Dutch", "Nederlands", "🇳🇱", LanguageCategory.GLOBAL),
        SupportedLanguage("pl", "Polish", "Polski", "🇵🇱", LanguageCategory.GLOBAL),
        SupportedLanguage("th", "Thai", "ไทย", "🇹🇭", LanguageCategory.GLOBAL),
        SupportedLanguage("fa", "Persian", "فارسی", "🇮🇷", LanguageCategory.GLOBAL, isRtl = true),
        SupportedLanguage("el", "Greek", "Ελληνικά", "🇬🇷", LanguageCategory.GLOBAL),
        SupportedLanguage("he", "Hebrew", "עברית", "🇮🇱", LanguageCategory.GLOBAL, isRtl = true),
        SupportedLanguage("sv", "Swedish", "Svenska", "🇸🇪", LanguageCategory.GLOBAL),
        SupportedLanguage("cs", "Czech", "Čeština", "🇨🇿", LanguageCategory.GLOBAL),
        SupportedLanguage("ro", "Romanian", "Română", "🇷🇴", LanguageCategory.GLOBAL),
        SupportedLanguage("hu", "Hungarian", "Magyar", "🇭🇺", LanguageCategory.GLOBAL),
        SupportedLanguage("uk", "Ukrainian", "Українська", "🇺🇦", LanguageCategory.GLOBAL),
        SupportedLanguage("ms", "Malay", "Bahasa Melayu", "🇲🇾", LanguageCategory.GLOBAL),
        SupportedLanguage("fil", "Filipino", "Tagalog", "🇵🇭", LanguageCategory.GLOBAL),
        SupportedLanguage("my", "Burmese", "မြန်မာဘာသာ", "🇲🇲", LanguageCategory.GLOBAL),
        SupportedLanguage("ne", "Nepali", "नेपाली", "🇳🇵", LanguageCategory.GLOBAL),
        SupportedLanguage("si", "Sinhala", "සිංහල", "🇱🇰", LanguageCategory.GLOBAL)
    )

    fun findByCode(code: String): SupportedLanguage {
        return ALL_SUPPORTED.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: DEFAULT_LANGUAGE
    }
}
