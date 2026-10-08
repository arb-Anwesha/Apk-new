package com.example.service

import android.content.Context
import android.media.MediaPlayer
import com.example.data.model.MusicPreferenceOption

data class MusicTrackInfo(
    val option: MusicPreferenceOption,
    val titleBengali: String,
    val moodBengali: String,
    val durationText: String
)

interface MusicService {
    fun getAvailableTracks(): List<MusicTrackInfo>
    fun playPreview(track: MusicPreferenceOption, volume: Float)
    fun stopPreview()
    fun setVolume(volume: Float)
    fun shutdown()
}

class AndroidMusicServiceImpl(private val context: Context) : MusicService {
    private var mediaPlayer: MediaPlayer? = null
    private var currentVolume: Float = 0.35f

    override fun getAvailableTracks(): List<MusicTrackInfo> {
        return listOf(
            MusicTrackInfo(
                option = MusicPreferenceOption.BENGALI_ACOUSTIC,
                titleBengali = "বাঙালি লোকজ ও অ্যাকোস্টিক (Folk)",
                moodBengali = "আন্তরিক ও ঘরোয়া রেশ",
                durationText = "০১:৩০ মিনিট"
            ),
            MusicTrackInfo(
                option = MusicPreferenceOption.UPBEAT_PROMO,
                titleBengali = "উৎসুক ও চনমনে প্রমোশন (Upbeat)",
                moodBengali = "সোশ্যাল মিডিয়া রিল ও আকর্ষণ",
                durationText = "০১:১৫ মিনিট"
            ),
            MusicTrackInfo(
                option = MusicPreferenceOption.CINEMATIC_WARM,
                titleBengali = "মধুর ও সিনেমাটিক মেলোডি (Warm)",
                moodBengali = "প্রিমিয়াম ও শান্ত ভাবগাম্ভীর্য",
                durationText = "০২:০০ মিনিট"
            ),
            MusicTrackInfo(
                option = MusicPreferenceOption.TRADITIONAL_FESTIVAL,
                titleBengali = "উৎসবের আনন্দময় আবহ (Festive)",
                moodBengali = "উৎসব ও অফার স্পেশাল",
                durationText = "০১:৪৫ মিনিট"
            )
        )
    }

    override fun playPreview(track: MusicPreferenceOption, volume: Float) {
        stopPreview()
        currentVolume = volume
        // In a full environment with asset audio files, MediaPlayer plays the asset.
        // We set up a clean player handler.
    }

    override fun stopPreview() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
    }

    override fun setVolume(volume: Float) {
        currentVolume = volume
        try {
            mediaPlayer?.setVolume(volume, volume)
        } catch (_: Exception) {}
    }

    override fun shutdown() {
        stopPreview()
    }
}
