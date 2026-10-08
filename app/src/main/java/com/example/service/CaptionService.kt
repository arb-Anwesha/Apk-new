package com.example.service

import com.example.data.model.SceneItem

data class TimedCaption(
    val sceneIndex: Int,
    val textBengali: String,
    val startSecond: Int,
    val endSecond: Int,
    val position: CaptionPosition,
    val fontSizeSp: Float,
    val isCta: Boolean = false
)

enum class CaptionPosition(val displayNameBengali: String) {
    TOP("উপরে (Top)"),
    CENTER("মাঝখানে (Center)"),
    BOTTOM("নিচে (Bottom)")
}

interface CaptionService {
    fun generateTimedCaptions(scenes: List<SceneItem>): List<TimedCaption>
    fun formatBengaliText(rawText: String): String
}

class BengaliCaptionServiceImpl : CaptionService {

    override fun generateTimedCaptions(scenes: List<SceneItem>): List<TimedCaption> {
        val result = mutableListOf<TimedCaption>()
        var currentSecond = 0

        scenes.forEachIndexed { index, scene ->
            val start = currentSecond
            val end = currentSecond + scene.durationSeconds
            currentSecond = end

            val pos = when {
                scene.captionPosition.contains("উপরে", ignoreCase = true) -> CaptionPosition.TOP
                scene.captionPosition.contains("মাঝখানে", ignoreCase = true) -> CaptionPosition.CENTER
                else -> CaptionPosition.BOTTOM
            }

            val isLastScene = index == scenes.size - 1

            result.add(
                TimedCaption(
                    sceneIndex = scene.index,
                    textBengali = formatBengaliText(scene.captionBengali.ifBlank { scene.titleBengali }),
                    startSecond = start,
                    endSecond = end,
                    position = pos,
                    fontSizeSp = scene.captionFontSizeSp,
                    isCta = isLastScene
                )
            )
        }
        return result
    }

    override fun formatBengaliText(rawText: String): String {
        return rawText.trim()
            .replace("\\s+".toRegex(), " ")
    }
}
