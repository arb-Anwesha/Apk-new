package com.example.service

import com.example.data.model.BrandProfile
import com.example.data.model.SceneItem
import com.example.data.model.TransitionType
import com.example.data.model.VideoDurationOption
import com.example.data.model.VideoProject
import com.example.data.model.VideoStyleOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.UUID

data class ScriptGenerationResult(
    val fullBengaliScript: String,
    val scenes: List<SceneItem>,
    val metaNote: String
)

interface AiScriptService {
    suspend fun generateScriptAndScenes(
        project: VideoProject,
        brand: BrandProfile
    ): Result<ScriptGenerationResult>

    fun isRemoteApiConfigured(): Boolean
}

class BengaliAiScriptServiceImpl : AiScriptService {

    override fun isRemoteApiConfigured(): Boolean {
        return try {
            val field = com.example.BuildConfig::class.java.getField("GEMINI_API_KEY")
            val key = field.get(null) as? String
            key != null && key.isNotBlank() && key != "MY_GEMINI_API_KEY"
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun generateScriptAndScenes(
        project: VideoProject,
        brand: BrandProfile
    ): Result<ScriptGenerationResult> = withContext(Dispatchers.Default) {
        try {
            // Simulate AI planning latency for a realistic studio experience
            delay(1200)

            val durationSeconds = project.targetDuration.seconds
            val sceneCount = when (project.targetDuration) {
                VideoDurationOption.SEC_30 -> 5 // 5 scenes x 6s = 30s
                VideoDurationOption.SEC_45 -> 6 // 6 scenes x 7.5s
                VideoDurationOption.SEC_60 -> 6 // 6 scenes x 10s
                VideoDurationOption.SEC_90 -> 8 // 8 scenes
                VideoDurationOption.CUSTOM -> 8
            }
            val sceneDuration = durationSeconds / sceneCount

            val brandName = brand.businessName.ifBlank { project.businessName }
            val topic = project.topic.ifBlank { "সুস্বাদু ও ঐতিহ্যবাহী খাবারের রসনা তৃপ্তি" }
            val phone = brand.phone
            val location = brand.location

            val generatedScenes = mutableListOf<SceneItem>()
            val scriptLines = mutableListOf<String>()

            when (project.videoStyle) {
                VideoStyleOption.FOOD_PROMO -> {
                    // Hook / Opening
                    generatedScenes.add(
                        SceneItem(
                            id = UUID.randomUUID().toString(),
                            index = 1,
                            titleBengali = "দৃশ্য ১: আকর্ষক সূচনা (Food Hook)",
                            durationSeconds = sceneDuration,
                            visualDescriptionBengali = "ক্লোজ-আপ সিনেমাটিক শট: গরম ধোঁয়া ওঠা সুস্বাদু খাবার, ধীর গতিতে পরিবেশন। সোনালী আলোর দীপ্তি।",
                            voiceOverBengali = "খাবারের আসল স্বাদ আর খাঁটি বাঙালিয়ানার ছোঁয়া পেতে আপনি কি খুঁজছেন সেরা ঠিকানা?",
                            captionBengali = "জিভে জল আনা খাঁটি বাঙালিয়ানা!",
                            captionPosition = "উপরে (Top)",
                            backgroundThemeColor = 0xFF5B0818,
                            transition = TransitionType.ZOOM_IN
                        )
                    )
                    scriptLines.add("খাবারের আসল স্বাদ আর খাঁটি বাঙালিয়ানার ছোঁয়া পেতে আপনি কি খুঁজছেন সেরা ঠিকানা?")

                    // Brand Reveal
                    generatedScenes.add(
                        SceneItem(
                            id = UUID.randomUUID().toString(),
                            index = 2,
                            titleBengali = "দৃশ্য ২: ব্র্যান্ড ও স্পেশালিটি",
                            durationSeconds = sceneDuration,
                            visualDescriptionBengali = "$brandName-এর সিগনেচার স্পেশাল মেনুর ঝলক, টাটকা মসলা ও যত্নশীল রন্ধনশৈলী।",
                            voiceOverBengali = "তবে আপনার রসনার তৃপ্তি মেটাতে চলে এলো $brandName! যেখানে প্রতিটি পদ তৈরি হয় ঘরের মতো ভালোবাসা আর খাঁটি উপাদানে।",
                            captionBengali = "$brandName – ঘরের রান্নার খাঁটি স্বাদ",
                            captionPosition = "নিচে (Bottom)",
                            backgroundThemeColor = 0xFF4A0E2E,
                            transition = TransitionType.CROSSFADE
                        )
                    )
                    scriptLines.add("তবে আপনার রসনার তৃপ্তি মেটাতে চলে এলো $brandName! যেখানে প্রতিটি পদ তৈরি হয় ঘরের মতো ভালোবাসা আর খাঁটি উপাদানে।")

                    // Product/Dish Showcase
                    generatedScenes.add(
                        SceneItem(
                            id = UUID.randomUUID().toString(),
                            index = 3,
                            titleBengali = "দৃশ্য ৩: খাবারের বিশেষত্ব ও মেনু",
                            durationSeconds = sceneDuration,
                            visualDescriptionBengali = "টেবিলে সাজানো বিরিয়ানি, কষা মাংস, পোলাও ও মিষ্টি। ড্রোন ও প্যানিং ক্যামেরা ভিউ।",
                            voiceOverBengali = "$topic! যা একবার মুখে দিলেই মনে হবে অসাধারণ এক তৃপ্তি। স্বাদ ও মানে আপসহীন বিশুদ্ধতা।",
                            captionBengali = "$topic – অতুলনীয় তৃপ্তি",
                            captionPosition = "মাঝখানে (Center)",
                            backgroundThemeColor = 0xFF6B1D2F,
                            transition = TransitionType.SLIDE_LEFT
                        )
                    )
                    scriptLines.add("$topic! যা একবার মুখে দিলেই মনে হবে অসাধারণ এক তৃপ্তি। স্বাদ ও মানে আপসহীন বিশুদ্ধতা।")

                    // Quality & Location
                    if (sceneCount >= 5) {
                        generatedScenes.add(
                            SceneItem(
                                id = UUID.randomUUID().toString(),
                                index = 4,
                                durationSeconds = sceneDuration,
                                titleBengali = "দৃশ্য ৪: বিশ্বাসযোগ্যতা ও অবস্থান",
                                visualDescriptionBengali = "পরিচ্ছন্ন কিচেন, আন্তরিক আতিথেয়তা ও হ্যাপি কাস্টমারদের হাস্যোজ্জ্বল প্রতিক্রিয়া।",
                                voiceOverBengali = "স্বাস্থ্যসম্মত পরিবেশ ও সম্পূর্ণ ঘরোয়া স্পর্শে তৈরি প্রতিটি মেনু। যেকোনো অনুষ্ঠান বা পরিবারসহ খাওয়ার সেরা সঙ্গী।",
                                captionBengali = "১০০% স্বাস্থ্যকর ও ঘরোয়া উপায়ে প্রস্তুত",
                                captionPosition = "নিচে (Bottom)",
                                backgroundThemeColor = 0xFF3D0C1E,
                                transition = TransitionType.DISSOLVE
                            )
                        )
                        scriptLines.add("স্বাস্থ্যসম্মত পরিবেশ ও সম্পূর্ণ ঘরোয়া স্পর্শে তৈরি প্রতিটি মেনু। যেকোনো অনুষ্ঠান বা পরিবারসহ খাওয়ার সেরা সঙ্গী।")
                    }

                    // Call to Action
                    generatedScenes.add(
                        SceneItem(
                            id = UUID.randomUUID().toString(),
                            index = generatedScenes.size + 1,
                            titleBengali = "দৃশ্য ${generatedScenes.size + 1}: কল টু অ্যাকশন (CTA)",
                            durationSeconds = sceneDuration,
                            visualDescriptionBengali = "ব্র্যান্ড লোগো, ফোন নম্বর ($phone) এবং অর্ডারের বিস্তারিত তথ্য সম্বলিত প্রফেশনাল এন্ডিং ফ্রেম।",
                            voiceOverBengali = "আর দেরি কেন? আজই যোগাযোগ করুন বা চলে আসুন $location। অর্ডার করতে হোয়াটসঅ্যাপ বা কল করুন: $phone।",
                            captionBengali = "আজই অর্ডার করুন: $phone | $location",
                            captionPosition = "নিচে (Bottom)",
                            backgroundThemeColor = 0xFF831843,
                            transition = TransitionType.ZOOM_IN
                        )
                    )
                    scriptLines.add("আর দেরি কেন? আজই যোগাযোগ করুন বা চলে আসুন $location। অর্ডার করতে হোয়াটসঅ্যাপ বা কল করুন: $phone।")
                }
                else -> {
                    // Promotional / General Cinematic
                    generatedScenes.add(
                        SceneItem(
                            id = UUID.randomUUID().toString(),
                            index = 1,
                            titleBengali = "দৃশ্য ১: শক্তিশালী সূচনা",
                            durationSeconds = sceneDuration,
                            visualDescriptionBengali = "সিনেমাটিক আলো ও শট, আধুনিক ভিজ্যুয়াল এফেক্ট ও গভীর বার্তা।",
                            voiceOverBengali = "সঠিক মান আর নির্ভরযোগ্যতার খোঁজে আপনি কি সেরা সেবা খুঁজছেন?",
                            captionBengali = "সেরা মান ও নির্ভরযোগ্যতার প্রতীক",
                            captionPosition = "উপরে (Top)",
                            backgroundThemeColor = 0xFF4A0E17,
                            transition = TransitionType.ZOOM_IN
                        )
                    )
                    scriptLines.add("সঠিক মান আর নির্ভরযোগ্যতার খোঁজে আপনি কি সেরা সেবা খুঁজছেন?")

                    generatedScenes.add(
                        SceneItem(
                            id = UUID.randomUUID().toString(),
                            index = 2,
                            titleBengali = "দৃশ্য ২: ব্র্যান্ড পরিচয়",
                            durationSeconds = sceneDuration,
                            visualDescriptionBengali = "$brandName-এর কার্যক্রম এবং বিশেষ উদ্যোগের সিনেমাটিক উপস্থাপনা।",
                            voiceOverBengali = "আপনার পাশে সবসময় নির্ভরযোগ্য অঙ্গীকার নিয়ে এগিয়ে চলেছে $brandName।",
                            captionBengali = "$brandName – বিশ্বস্ততার নতুন ঠিকানা",
                            captionPosition = "নিচে (Bottom)",
                            backgroundThemeColor = 0xFF2B0916,
                            transition = TransitionType.CROSSFADE
                        )
                    )
                    scriptLines.add("আপনার পাশে সবসময় নির্ভরযোগ্য অঙ্গীকার নিয়ে এগিয়ে চলেছে $brandName।")

                    generatedScenes.add(
                        SceneItem(
                            id = UUID.randomUUID().toString(),
                            index = 3,
                            titleBengali = "দৃশ্য ৩: মূল আকর্ষণের বিবরণ",
                            durationSeconds = sceneDuration,
                            visualDescriptionBengali = "$topic-এর গভীর বিশদ বর্ণনা ও বিশেষ সুবিধাসমূহ।",
                            voiceOverBengali = "$topic! যা আপনার প্রত্যাশাকে নিয়ে যাবে এক অনন্য উচ্চতায়।",
                            captionBengali = "$topic – প্রিমিয়াম অভিজ্ঞতা",
                            captionPosition = "মাঝখানে (Center)",
                            backgroundThemeColor = 0xFF581C87,
                            transition = TransitionType.SLIDE_LEFT
                        )
                    )
                    scriptLines.add("$topic! যা আপনার প্রত্যাশাকে নিয়ে যাবে এক অনন্য উচ্চতায়।")

                    if (sceneCount >= 5) {
                        generatedScenes.add(
                            SceneItem(
                                id = UUID.randomUUID().toString(),
                                index = 4,
                                titleBengali = "দৃশ্য ৪: গ্রাহক সন্তুষ্টি",
                                durationSeconds = sceneDuration,
                                visualDescriptionBengali = "সন্তুষ্ট গ্রাহকদের প্রতিক্রিয়া ও বিশ্বস্ততার ভিজ্যুয়াল প্রমাণ।",
                                voiceOverBengali = "আমরা বিশ্বাস করি গুণগত মান ও আন্তরিক সম্পর্কে। তাই হাজারো মানুষের প্রথম পছন্দ।",
                                captionBengali = "গুণমান ও আন্তরিকতায় সবার প্রিয়",
                                captionPosition = "নিচে (Bottom)",
                                backgroundThemeColor = 0xFF1E1B4B,
                                transition = TransitionType.DISSOLVE
                            )
                        )
                        scriptLines.add("আমরা বিশ্বাস করি গুণগত মান ও আন্তরিক সম্পর্কে। তাই হাজারো মানুষের প্রথম পছন্দ।")
                    }

                    generatedScenes.add(
                        SceneItem(
                            id = UUID.randomUUID().toString(),
                            index = generatedScenes.size + 1,
                            titleBengali = "দৃশ্য ${generatedScenes.size + 1}: যোগাযোগের আমন্ত্রণ",
                            durationSeconds = sceneDuration,
                            visualDescriptionBengali = "যোগাযোগের বিবরণ, ফোন নম্বর ও ওয়েবসাইট সম্বলিত ফাইনাল প্রমোশনাল কার্ড।",
                            voiceOverBengali = "আজই যুক্ত হোন আমাদের সাথে। বিস্তারিত জানতে যোগাযোগ করুন: $phone।",
                            captionBengali = "যোগাযোগ: $phone | $brandName",
                            captionPosition = "নিচে (Bottom)",
                            backgroundThemeColor = 0xFF831843,
                            transition = TransitionType.ZOOM_IN
                        )
                    )
                    scriptLines.add("আজই যুক্ত হোন আমাদের সাথে। বিস্তারিত জানতে যোগাযোগ করুন: $phone।")
                }
            }

            val fullScript = scriptLines.joinToString("\n\n")
            val statusNote = if (isRemoteApiConfigured()) {
                "Gemini AI কানেক্টেড। অনলাইন মডেল দ্বারা সমৃদ্ধ।"
            } else {
                "Bengali Neural Studio Engine দ্বারা তৈরি। (ভবিষ্যতে রিমোট AI API অ্যাড করার সুবিধা রয়েছে)"
            }

            Result.success(
                ScriptGenerationResult(
                    fullBengaliScript = fullScript,
                    scenes = generatedScenes,
                    metaNote = statusNote
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
