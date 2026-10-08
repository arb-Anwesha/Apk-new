package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.BrandProfile
import com.example.data.model.LogoPosition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BrandRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("brand_settings_prefs", Context.MODE_PRIVATE)

    private val _brandProfile = MutableStateFlow(loadProfile())
    val brandProfile: StateFlow<BrandProfile> = _brandProfile.asStateFlow()

    private fun loadProfile(): BrandProfile {
        val positionStr = prefs.getString("logo_position", LogoPosition.TOP_RIGHT.name)
        val position = try {
            LogoPosition.valueOf(positionStr ?: LogoPosition.TOP_RIGHT.name)
        } catch (_: Exception) {
            LogoPosition.TOP_RIGHT
        }

        return BrandProfile(
            businessName = prefs.getString("business_name", "অন্বেষার রসনা বিলাস") ?: "অন্বেষার রসনা বিলাস",
            tagline = prefs.getString("tagline", "বাঙালিয়ানার চেনা স্বাদ, ঘরের রান্নায় সেরা স্বাদ।")
                ?: "বাঙালিয়ানার চেনা স্বাদ, ঘরের রান্নায় সেরা স্বাদ।",
            location = prefs.getString("location", "বহরমপুর, মুর্শিদাবাদ, পশ্চিমবঙ্গ")
                ?: "বহরমপুর, মুর্শিদাবাদ, পশ্চিমবঙ্গ",
            phone = prefs.getString("phone", "8509360327") ?: "8509360327",
            website = prefs.getString("website", "anweshar-roshona-bilas.ai.studio")
                ?: "anweshar-roshona-bilas.ai.studio",
            logoUri = prefs.getString("logo_uri", null),
            logoPosition = position,
            logoSizePercent = prefs.getFloat("logo_size", 0.20f),
            logoOpacity = prefs.getFloat("logo_opacity", 0.90f),
            isWatermarkEnabled = prefs.getBoolean("watermark_enabled", true)
        )
    }

    fun saveProfile(profile: BrandProfile) {
        prefs.edit()
            .putString("business_name", profile.businessName)
            .putString("tagline", profile.tagline)
            .putString("location", profile.location)
            .putString("phone", profile.phone)
            .putString("website", profile.website)
            .putString("logo_uri", profile.logoUri)
            .putString("logo_position", profile.logoPosition.name)
            .putFloat("logo_size", profile.logoSizePercent)
            .putFloat("logo_opacity", profile.logoOpacity)
            .putBoolean("watermark_enabled", profile.isWatermarkEnabled)
            .apply()
        _brandProfile.value = profile
    }

    fun updateLogoUri(uriString: String?) {
        val current = _brandProfile.value
        val updated = current.copy(logoUri = uriString)
        saveProfile(updated)
    }
}
