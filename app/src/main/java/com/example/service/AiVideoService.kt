package com.example.service

import com.example.data.model.SceneItem
import com.example.data.model.VideoStyleOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

data class VisualGenerationResult(
    val visualPromptUsed: String,
    val dominantColors: List<Long>,
    val statusMessageBengali: String,
    val isRealAiApiGenerated: Boolean
)

interface AiVideoService {
    fun isAiVideoApiConnected(): Boolean
    suspend fun generateVisualForScene(
        scene: SceneItem,
        style: VideoStyleOption
    ): Result<VisualGenerationResult>
}

class ModularAiVideoServiceImpl : AiVideoService {

    override fun isAiVideoApiConnected(): Boolean {
        // Transparently returns false unless a dedicated video API (like Runway/Luma/Sora/Vertex) endpoint is configured
        return false
    }

    override suspend fun generateVisualForScene(
        scene: SceneItem,
        style: VideoStyleOption
    ): Result<VisualGenerationResult> = withContext(Dispatchers.Default) {
        delay(600) // Simulated processing

        val palette = when (style) {
            VideoStyleOption.FOOD_PROMO -> listOf(0xFF4C0519, 0xFF881337, 0xFFBE123C)
            VideoStyleOption.PROMOTIONAL -> listOf(0xFF1E1B4B, 0xFF312E81, 0xFF4338CA)
            VideoStyleOption.CINEMATIC -> listOf(0xFF18181B, 0xFF27272A, 0xFF3F3F46)
            VideoStyleOption.FESTIVAL -> listOf(0xFF78350F, 0xFFB45309, 0xFFD97706)
            VideoStyleOption.EMOTIONAL -> listOf(0xFF701A75, 0xFF86198F, 0xFFA21CAF)
            VideoStyleOption.INFORMATIVE -> listOf(0xFF0F172A, 0xFF1E293B, 0xFF334155)
        }

        Result.success(
            VisualGenerationResult(
                visualPromptUsed = scene.visualDescriptionBengali,
                dominantColors = palette,
                statusMessageBengali = if (isAiVideoApiConnected()) {
                    "রিমোট AI ভিডিও সার্ভার দ্বারা জেনারেট করা হয়েছে।"
                } else {
                    "লোকাল সিনেমাটিক কম্পোজিশন ইঞ্জিন সক্রিয় (AI Video API মডিউলার আর্কিটেকচার প্রস্তুত)।"
                },
                isRealAiApiGenerated = isAiVideoApiConnected()
            )
        )
    }
}
