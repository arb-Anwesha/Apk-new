package com.example

import com.example.data.model.BrandProfile
import com.example.data.model.VideoDurationOption
import com.example.data.model.VideoProject
import com.example.data.model.VideoStyleOption
import com.example.service.BengaliAiScriptServiceImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun defaultBrandProfile_hasCorrectBengaliDetails() {
        val brand = BrandProfile()
        assertEquals("অন্বেষার রসনা বিলাস", brand.businessName)
        assertEquals("বাঙালিয়ানার চেনা স্বাদ, ঘরের রান্নায় সেরা স্বাদ।", brand.tagline)
        assertEquals("বহরমপুর, মুর্শিদাবাদ, পশ্চিমবঙ্গ", brand.location)
        assertEquals("8509360327", brand.phone)
        assertEquals("anweshar-roshona-bilas.ai.studio", brand.website)
        assertTrue(brand.isWatermarkEnabled)
    }

    @Test
    fun bengaliAiScriptService_generatesValidScenesAndScript() = runBlocking {
        val service = BengaliAiScriptServiceImpl()
        val brand = BrandProfile()
        val project = VideoProject(
            topic = "খাঁটি ঘরোয়া বিরিয়ানি",
            targetDuration = VideoDurationOption.SEC_30,
            videoStyle = VideoStyleOption.FOOD_PROMO
        )

        val result = service.generateScriptAndScenes(project, brand)
        assertTrue(result.isSuccess)

        val data = result.getOrNull()
        assertNotNull(data)
        assertTrue(data!!.scenes.isNotEmpty())
        assertTrue(data.scenes.size >= 4)
        assertTrue(data.fullBengaliScript.contains("অন্বেষার রসনা বিলাস"))
        assertTrue(data.scenes.any { it.voiceOverBengali.isNotBlank() })
    }

    @Test
    fun videoProject_durationCalculationIsAccurate() {
        val project = VideoProject(targetDuration = VideoDurationOption.SEC_30)
        assertEquals(30, project.totalCalculatedDurationSeconds)
    }
}
