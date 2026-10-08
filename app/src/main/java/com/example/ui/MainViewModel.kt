package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BrandProfile
import com.example.data.model.ExportedVideo
import com.example.data.model.SceneItem
import com.example.data.model.TransitionType
import com.example.data.model.VideoDurationOption
import com.example.data.model.VideoProject
import com.example.data.model.VideoStyleOption
import com.example.data.model.VoiceStyleOption
import com.example.data.repository.BrandRepository
import com.example.data.repository.VideoLibraryRepository
import com.example.service.AiScriptService
import com.example.service.AiVideoService
import com.example.service.CaptionService
import com.example.service.MediaCompositionService
import com.example.service.MusicService
import com.example.service.VoiceService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class StudioTab(val titleBengali: String) {
    AI_STUDIO("AI জেনারেটর"),
    MY_MEDIA("আমার ছবি/ভিডিও"),
    SCRIPT_VOICE("স্ক্রিপ্ট ও ভয়েস"),
    PREVIEW_EXPORT("ভিডিও প্রিভিউ"),
    MY_VIDEOS("আমার ভিডিও"),
    BRAND_SETTINGS("ব্র্যান্ড সেটিংস")
}

data class StudioUiState(
    val currentTab: StudioTab = StudioTab.AI_STUDIO,
    val project: VideoProject = VideoProject(),
    val brand: BrandProfile = BrandProfile(),
    val exportedVideos: List<ExportedVideo> = emptyList(),
    val isGeneratingScript: Boolean = false,
    val scriptStatusNote: String? = null,
    val isExporting: Boolean = false,
    val exportProgressPercent: Int = 0,
    val exportStatusText: String = "",
    val lastExportedFile: File? = null,
    val isPreviewPlaying: Boolean = false,
    val previewProgressSeconds: Float = 0f,
    val previewVolume: Float = 1.0f,
    val isPreviewMuted: Boolean = false,
    val activePlayingSceneIndex: Int = 0,
    val snackbarMessage: String? = null
)

class MainViewModel(
    private val brandRepository: BrandRepository,
    private val videoLibraryRepository: VideoLibraryRepository,
    private val aiScriptService: AiScriptService,
    private val voiceService: VoiceService,
    private val aiVideoService: AiVideoService,
    private val captionService: CaptionService,
    private val mediaCompositionService: MediaCompositionService,
    private val musicService: MusicService
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudioUiState())
    val uiState: StateFlow<StudioUiState> = _uiState.asStateFlow()

    private var previewPlaybackJob: Job? = null

    init {
        viewModelScope.launch {
            brandRepository.brandProfile.collect { profile ->
                _uiState.value = _uiState.value.copy(
                    brand = profile,
                    project = _uiState.value.project.copy(businessName = profile.businessName)
                )
            }
        }
        viewModelScope.launch {
            videoLibraryRepository.refreshVideos()
            videoLibraryRepository.videos.collect { list ->
                _uiState.value = _uiState.value.copy(exportedVideos = list)
            }
        }
        // Initialize with default rich scenes for instant preview
        generateDefaultInitialScenes()
    }

    private fun generateDefaultInitialScenes() {
        viewModelScope.launch {
            val result = aiScriptService.generateScriptAndScenes(
                _uiState.value.project,
                _uiState.value.brand
            )
            result.onSuccess { data ->
                _uiState.value = _uiState.value.copy(
                    project = _uiState.value.project.copy(
                        scenes = data.scenes,
                        fullBengaliScript = data.fullBengaliScript
                    ),
                    scriptStatusNote = data.metaNote
                )
            }
        }
    }

    fun selectTab(tab: StudioTab) {
        if (tab != StudioTab.PREVIEW_EXPORT) {
            pausePreview()
        }
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun updateProjectTopic(topic: String) {
        _uiState.value = _uiState.value.copy(
            project = _uiState.value.project.copy(topic = topic)
        )
    }

    fun updateProjectPurpose(purpose: String) {
        _uiState.value = _uiState.value.copy(
            project = _uiState.value.project.copy(videoPurpose = purpose)
        )
    }

    fun updateProjectDuration(duration: VideoDurationOption) {
        _uiState.value = _uiState.value.copy(
            project = _uiState.value.project.copy(targetDuration = duration)
        )
    }

    fun updateProjectStyle(style: VideoStyleOption) {
        _uiState.value = _uiState.value.copy(
            project = _uiState.value.project.copy(videoStyle = style)
        )
    }

    fun updateVoiceStyle(voice: VoiceStyleOption) {
        _uiState.value = _uiState.value.copy(
            project = _uiState.value.project.copy(voiceStyle = voice)
        )
    }

    fun updateAdditionalInstructions(instructions: String) {
        _uiState.value = _uiState.value.copy(
            project = _uiState.value.project.copy(additionalInstructions = instructions)
        )
    }

    fun generateAiScript() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isGeneratingScript = true,
                scriptStatusNote = "বাঙালি সিনেমাটিক এআই স্ক্রিপ্ট ও দৃশ্য পরিকল্পনা তৈরি হচ্ছে..."
            )
            val result = aiScriptService.generateScriptAndScenes(
                _uiState.value.project,
                _uiState.value.brand
            )
            result.onSuccess { data ->
                _uiState.value = _uiState.value.copy(
                    isGeneratingScript = false,
                    project = _uiState.value.project.copy(
                        scenes = data.scenes,
                        fullBengaliScript = data.fullBengaliScript
                    ),
                    scriptStatusNote = data.metaNote,
                    snackbarMessage = "স্ক্রিপ্ট ও ${data.scenes.size}টি দৃশ্য সফলভাবে তৈরি হয়েছে!"
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isGeneratingScript = false,
                    snackbarMessage = "ত্রুটি: ${err.message}"
                )
            }
        }
    }

    fun updateFullScript(newScript: String) {
        _uiState.value = _uiState.value.copy(
            project = _uiState.value.project.copy(fullBengaliScript = newScript)
        )
    }

    fun updateScene(index: Int, updatedScene: SceneItem) {
        val currentScenes = _uiState.value.project.scenes.toMutableList()
        if (index in currentScenes.indices) {
            currentScenes[index] = updatedScene
            _uiState.value = _uiState.value.copy(
                project = _uiState.value.project.copy(scenes = currentScenes)
            )
        }
    }

    fun addMediaUris(uris: List<Uri>, isVideo: Boolean) {
        val currentScenes = _uiState.value.project.scenes.toMutableList()
        uris.forEachIndexed { i, uri ->
            val sceneIndex = currentScenes.size + 1
            currentScenes.add(
                SceneItem(
                    id = UUID.randomUUID().toString(),
                    index = sceneIndex,
                    titleBengali = "দৃশ্য $sceneIndex (ইউজার মিডিয়া)",
                    durationSeconds = 6,
                    visualDescriptionBengali = "ব্যবহারকারীর আপলোড করা মিডিয়া ডিসপ্লে",
                    voiceOverBengali = "${_uiState.value.brand.businessName}-এর বিশেষ আকর্ষণ",
                    captionBengali = "${_uiState.value.brand.businessName} – সেরা স্বাদ ও মান",
                    mediaUri = uri.toString(),
                    mediaType = if (isVideo) "VIDEO" else "IMAGE",
                    backgroundThemeColor = 0xFF4A0E1A,
                    transition = TransitionType.CROSSFADE
                )
            )
        }
        _uiState.value = _uiState.value.copy(
            project = _uiState.value.project.copy(
                scenes = currentScenes,
                useOnlyUserMedia = true
            ),
            snackbarMessage = "${uris.size}টি ছবি/ভিডিও সফলভাবে যুক্ত হয়েছে!"
        )
    }

    fun removeScene(index: Int) {
        val currentScenes = _uiState.value.project.scenes.toMutableList()
        if (index in currentScenes.indices && currentScenes.size > 1) {
            currentScenes.removeAt(index)
            // re-index
            val reIndexed = currentScenes.mapIndexed { idx, s -> s.copy(index = idx + 1) }
            _uiState.value = _uiState.value.copy(
                project = _uiState.value.project.copy(scenes = reIndexed)
            )
        }
    }

    fun previewVoice(text: String) {
        viewModelScope.launch {
            voiceService.speakPreview(
                text = text,
                style = _uiState.value.project.voiceStyle
            )
        }
    }

    fun stopVoice() {
        voiceService.stop()
    }

    // Video Preview Player controls
    fun togglePreviewPlayback() {
        if (_uiState.value.isPreviewPlaying) {
            pausePreview()
        } else {
            startPreview()
        }
    }

    fun startPreview() {
        val totalSec = _uiState.value.project.totalCalculatedDurationSeconds.toFloat()
        if (totalSec <= 0f) return

        _uiState.value = _uiState.value.copy(isPreviewPlaying = true)
        previewPlaybackJob?.cancel()
        previewPlaybackJob = viewModelScope.launch {
            // Trigger voice for the first scene
            val scenes = _uiState.value.project.scenes
            var lastSpokenSceneIdx = -1

            while (isActive && _uiState.value.isPreviewPlaying) {
                delay(100)
                val current = _uiState.value.previewProgressSeconds + 0.1f
                if (current >= totalSec) {
                    _uiState.value = _uiState.value.copy(
                        isPreviewPlaying = false,
                        previewProgressSeconds = 0f,
                        activePlayingSceneIndex = 0
                    )
                    break
                }

                // Calculate which scene is active
                var accumulated = 0
                var activeIdx = 0
                for ((idx, scene) in scenes.withIndex()) {
                    accumulated += scene.durationSeconds
                    if (current <= accumulated) {
                        activeIdx = idx
                        break
                    }
                }

                if (activeIdx != lastSpokenSceneIdx && !_uiState.value.isPreviewMuted) {
                    lastSpokenSceneIdx = activeIdx
                    val activeScene = scenes.getOrNull(activeIdx)
                    if (activeScene != null && activeScene.voiceOverBengali.isNotBlank()) {
                        voiceService.speakPreview(activeScene.voiceOverBengali, _uiState.value.project.voiceStyle)
                    }
                }

                _uiState.value = _uiState.value.copy(
                    previewProgressSeconds = current,
                    activePlayingSceneIndex = activeIdx
                )
            }
        }
    }

    fun pausePreview() {
        previewPlaybackJob?.cancel()
        voiceService.stop()
        _uiState.value = _uiState.value.copy(isPreviewPlaying = false)
    }

    fun seekPreview(seconds: Float) {
        val totalSec = _uiState.value.project.totalCalculatedDurationSeconds.toFloat()
        val clamped = seconds.coerceIn(0f, totalSec)
        _uiState.value = _uiState.value.copy(previewProgressSeconds = clamped)
    }

    fun toggleMute() {
        val currentMute = _uiState.value.isPreviewMuted
        _uiState.value = _uiState.value.copy(isPreviewMuted = !currentMute)
        if (!currentMute) {
            voiceService.stop()
        }
    }

    fun setMusicVolume(vol: Float) {
        _uiState.value = _uiState.value.copy(
            project = _uiState.value.project.copy(musicVolume = vol)
        )
    }

    fun setVoiceVolume(vol: Float) {
        _uiState.value = _uiState.value.copy(
            project = _uiState.value.project.copy(voiceVolume = vol)
        )
    }

    fun toggleMusicEnabled() {
        val cur = _uiState.value.project.isMusicEnabled
        _uiState.value = _uiState.value.copy(
            project = _uiState.value.project.copy(isMusicEnabled = !cur)
        )
    }

    // Export Video
    fun exportVideo() {
        viewModelScope.launch {
            pausePreview()
            _uiState.value = _uiState.value.copy(
                isExporting = true,
                exportProgressPercent = 5,
                exportStatusText = "ভিডিও এক্সপোর্ট প্রক্রিয়া প্রস্তুত হচ্ছে..."
            )

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Anweshar_Studio_$timeStamp.mp4"
            val targetDir = videoLibraryRepository.getVideoDirectory()
            val outputFile = File(targetDir, fileName)

            val result = mediaCompositionService.composeAndExportVideo(
                project = _uiState.value.project,
                brand = _uiState.value.brand,
                outputFile = outputFile,
                onProgress = { percent, status ->
                    _uiState.value = _uiState.value.copy(
                        exportProgressPercent = percent,
                        exportStatusText = status
                    )
                }
            )

            result.onSuccess { file ->
                videoLibraryRepository.refreshVideos()
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    lastExportedFile = file,
                    snackbarMessage = "ভিডিও সফলভাবে এক্সপোর্ট সম্পন্ন হয়েছে! ফাইল: ${file.name}"
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    snackbarMessage = "এক্সপোর্ট ব্যর্থ হয়েছে: ${err.message}"
                )
            }
        }
    }

    // Brand management
    fun saveBrandProfile(profile: BrandProfile) {
        brandRepository.saveProfile(profile)
        _uiState.value = _uiState.value.copy(
            brand = profile,
            snackbarMessage = "ব্র্যান্ড সেটিংস সংরক্ষিত হয়েছে!"
        )
    }

    fun updateBrandLogoUri(uriString: String?) {
        brandRepository.updateLogoUri(uriString)
        _uiState.value = _uiState.value.copy(
            snackbarMessage = if (uriString != null) "লোগো সফলভাবে আপলোড করা হয়েছে!" else "লোগো সরানো হয়েছে।"
        )
    }

    // Video Library actions
    fun deleteVideo(filePath: String) {
        viewModelScope.launch {
            val success = videoLibraryRepository.deleteVideo(filePath)
            if (success) {
                _uiState.value = _uiState.value.copy(snackbarMessage = "ভিডিও মুছে ফেলা হয়েছে।")
            }
        }
    }

    fun renameVideo(filePath: String, newName: String) {
        viewModelScope.launch {
            val success = videoLibraryRepository.renameVideo(filePath, newName)
            if (success) {
                _uiState.value = _uiState.value.copy(snackbarMessage = "ভিডিওর নাম পরিবর্তন করা হয়েছে।")
            }
        }
    }

    fun shareVideo(context: Context, video: ExportedVideo) {
        try {
            val uri = videoLibraryRepository.getShareableUri(video)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "video/mp4"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, video.title)
                putExtra(Intent.EXTRA_TEXT, "তৈরি হয়েছে অন্বেষার ভিডিও স্টুডিও দিয়ে: ${video.title}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "ভিডিওটি শেয়ার করুন"))
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(snackbarMessage = "শেয়ার করতে ত্রুটি: ${e.message}")
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    override fun onCleared() {
        super.onCleared()
        previewPlaybackJob?.cancel()
        voiceService.shutdown()
        musicService.shutdown()
    }
}
