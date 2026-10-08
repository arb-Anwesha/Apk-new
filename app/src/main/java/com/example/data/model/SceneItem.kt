package com.example.data.model

import java.util.UUID

enum class TransitionType(val displayNameBengali: String) {
    CROSSFADE("ক্রসফেড (Crossfade)"),
    ZOOM_IN("জুম ইন (Zoom In)"),
    SLIDE_LEFT("স্লাইড (Slide Left)"),
    DISSOLVE("ডিসলভ (Dissolve)")
}

data class SceneItem(
    val id: String = UUID.randomUUID().toString(),
    val index: Int = 1,
    val titleBengali: String = "দৃশ্য ১",
    val durationSeconds: Int = 6,
    val visualDescriptionBengali: String = "",
    val voiceOverBengali: String = "",
    val captionBengali: String = "",
    val captionPosition: String = "নিচে (Bottom)",
    val captionFontSizeSp: Float = 22f,
    val mediaUri: String? = null,
    val mediaType: String = "AI_GENERATED", // AI_GENERATED, IMAGE, VIDEO
    val backgroundThemeColor: Long = 0xFF3F0A1A,
    val transition: TransitionType = TransitionType.CROSSFADE
)
