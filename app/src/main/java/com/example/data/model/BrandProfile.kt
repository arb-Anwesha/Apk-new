package com.example.data.model

enum class LogoPosition(val displayNameBengali: String) {
    TOP_LEFT("উপরে বামে"),
    TOP_RIGHT("উপরে ডানে"),
    BOTTOM_LEFT("নিচে বামে"),
    BOTTOM_RIGHT("নিচে ডানে")
}

data class BrandProfile(
    val businessName: String = "অন্বেষার রসনা বিলাস",
    val tagline: String = "বাঙালিয়ানার চেনা স্বাদ, ঘরের রান্নায় সেরা স্বাদ।",
    val location: String = "বহরমপুর, মুর্শিদাবাদ, পশ্চিমবঙ্গ",
    val phone: String = "8509360327",
    val website: String = "anweshar-roshona-bilas.ai.studio",
    val logoUri: String? = null,
    val logoPosition: LogoPosition = LogoPosition.TOP_RIGHT,
    val logoSizePercent: Float = 0.20f,
    val logoOpacity: Float = 0.90f,
    val isWatermarkEnabled: Boolean = true
)
