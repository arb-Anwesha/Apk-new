package com.example.data.model

import java.util.UUID

enum class VideoDurationOption(val seconds: Int, val displayNameBengali: String) {
    SEC_30(30, "৩০ সেকেন্ড (Reels/Shorts)"),
    SEC_45(45, "৪৫ সেকেন্ড (Promo)"),
    SEC_60(60, "৬০ সেকেন্ড (Facebook/Insta)"),
    SEC_90(90, "৯০ সেকেন্ড (Full Video)"),
    CUSTOM(120, "কাস্টম (১২০ সে)")
}

enum class VideoStyleOption(val displayNameBengali: String, val englishTag: String) {
    FOOD_PROMO("খাবারের বিজ্ঞাপন (Food Ad)", "Food Advertisement"),
    PROMOTIONAL("বাণিজ্যিক প্রমো (Promotional)", "Commercial Promo"),
    CINEMATIC("সিনেমাটিক ও ড্রামাটিক (Cinematic)", "Cinematic"),
    FESTIVAL("উৎসব ও স্পেশাল অফার (Festival)", "Festival Celebration"),
    EMOTIONAL("আবেগঘন ও আন্তরিক (Emotional)", "Emotional Story"),
    INFORMATIVE("তথ্যভিত্তিক রিভিউ (Informative)", "Informative Review")
}

enum class VoiceStyleOption(val displayNameBengali: String, val gender: String, val tone: String) {
    BENGALI_FEMALE_PROMO("বাংলা মহিলা (আকর্ষণীয় ও প্রাণবন্ত)", "Female", "Promotional"),
    BENGALI_FEMALE_NATURAL("বাংলা মহিলা (স্বাভাবিক ও আন্তরিক)", "Female", "Natural"),
    BENGALI_MALE_PROFESSIONAL("বাংলা পুরুষ (গম্ভীর ও প্রফেশনাল)", "Male", "Professional"),
    BENGALI_FEMALE_EMOTIONAL("বাংলা মহিলা (মধুর ও আবেগঘন)", "Female", "Emotional")
}

enum class MusicPreferenceOption(val displayNameBengali: String, val filename: String) {
    BENGALI_ACOUSTIC("বাঙালি লোকজ ও অ্যাকোস্টিক (Folk)", "bengali_folk.mp3"),
    UPBEAT_PROMO("উৎসুক ও চনমনে প্রমোশন (Upbeat)", "upbeat_promo.mp3"),
    CINEMATIC_WARM("মধুর ও সিনেমাটিক মেলোডি (Warm)", "warm_cinematic.mp3"),
    TRADITIONAL_FESTIVAL("উৎসবের আনন্দময় আবহ (Festive)", "festive_mood.mp3")
}

data class VideoProject(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "নতুন বাংলা ভিডিও প্রজেক্ট",
    val topic: String = "ঘরোয়া স্বাদের ঐতিহ্যবাহী বিরিয়ানি ও রকমারি সুস্বাদু খাবার",
    val businessName: String = "অন্বেষার রসনা বিলাস",
    val videoPurpose: String = "সোশ্যাল মিডিয়া প্রচার ও নতুন কাস্টমার আকর্ষণ",
    val targetDuration: VideoDurationOption = VideoDurationOption.SEC_30,
    val videoStyle: VideoStyleOption = VideoStyleOption.FOOD_PROMO,
    val voiceStyle: VoiceStyleOption = VoiceStyleOption.BENGALI_FEMALE_PROMO,
    val musicPreference: MusicPreferenceOption = MusicPreferenceOption.BENGALI_ACOUSTIC,
    val customMusicUri: String? = null,
    val musicVolume: Float = 0.35f,
    val voiceVolume: Float = 0.95f,
    val isMusicEnabled: Boolean = true,
    val additionalInstructions: String = "মুর্শিদাবাদের সেরা অথেনটিক স্বাদের ওপর জোর দিন, হোম ডেলিভারি উল্লেখ করুন।",
    val useOnlyUserMedia: Boolean = false,
    val scenes: List<SceneItem> = emptyList(),
    val fullBengaliScript: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
) {
    val totalCalculatedDurationSeconds: Int
        get() = if (scenes.isEmpty()) targetDuration.seconds else scenes.sumOf { it.durationSeconds }
}
