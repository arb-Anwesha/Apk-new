package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.BrandRepository
import com.example.data.repository.VideoLibraryRepository
import com.example.service.AndroidBengaliVoiceServiceImpl
import com.example.service.AndroidMusicServiceImpl
import com.example.service.BengaliAiScriptServiceImpl
import com.example.service.BengaliCaptionServiceImpl
import com.example.service.ModularAiVideoServiceImpl
import com.example.service.NativeMediaCompositionServiceImpl
import com.example.ui.MainViewModel
import com.example.ui.StudioTab
import com.example.ui.screens.AiVideoStudioScreen
import com.example.ui.screens.BrandSettingsScreen
import com.example.ui.screens.MyMediaStudioScreen
import com.example.ui.screens.MyVideosScreen
import com.example.ui.screens.PreviewExportScreen
import com.example.ui.screens.ScriptVoiceStudioScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioBackgroundDark
import com.example.ui.theme.StudioCrimsonPrimary
import com.example.ui.theme.StudioCrimsonSecondary
import com.example.ui.theme.StudioGoldAccent
import com.example.ui.theme.StudioGoldPrimary
import com.example.ui.theme.StudioMaroonSurface
import com.example.ui.theme.StudioSurfaceDark
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val brandRepo = remember { BrandRepository(context) }
            val videoLibRepo = remember { VideoLibraryRepository(context) }
            val aiScriptService = remember { BengaliAiScriptServiceImpl() }
            val voiceService = remember { AndroidBengaliVoiceServiceImpl(context) }
            val aiVideoService = remember { ModularAiVideoServiceImpl() }
            val captionService = remember { BengaliCaptionServiceImpl() }
            val mediaCompService = remember { NativeMediaCompositionServiceImpl(context, captionService) }
            val musicService = remember { AndroidMusicServiceImpl(context) }

            val viewModel: MainViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MainViewModel(
                            brandRepository = brandRepo,
                            videoLibraryRepository = videoLibRepo,
                            aiScriptService = aiScriptService,
                            voiceService = voiceService,
                            aiVideoService = aiVideoService,
                            captionService = captionService,
                            mediaCompositionService = mediaCompService,
                            musicService = musicService
                        ) as T
                    }
                }
            )

            MyApplicationTheme(darkTheme = true) {
                MainStudioApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainStudioApp(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Hardware back button navigation
    BackHandler(enabled = uiState.currentTab != StudioTab.AI_STUDIO) {
        viewModel.selectTab(StudioTab.AI_STUDIO)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    androidx.compose.foundation.layout.Column {
                        Text(
                            text = "অন্বেষার ভিডিও স্টুডিও",
                            color = StudioTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.brand.businessName,
                            color = StudioGoldAccent,
                            fontSize = 12.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StudioMaroonSurface,
                    titleContentColor = StudioTextPrimary
                )
            )
        },
        bottomBar = {
            StudioBottomBar(
                currentTab = uiState.currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = StudioBackgroundDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = uiState.currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "StudioTabTransition"
            ) { tab ->
                when (tab) {
                    StudioTab.AI_STUDIO -> {
                        AiVideoStudioScreen(
                            uiState = uiState,
                            onTopicChange = { viewModel.updateProjectTopic(it) },
                            onPurposeChange = { viewModel.updateProjectPurpose(it) },
                            onDurationChange = { viewModel.updateProjectDuration(it) },
                            onStyleChange = { viewModel.updateProjectStyle(it) },
                            onVoiceChange = { viewModel.updateVoiceStyle(it) },
                            onInstructionsChange = { viewModel.updateAdditionalInstructions(it) },
                            onGenerateClick = { viewModel.generateAiScript() },
                            onPreviewVoice = { viewModel.previewVoice(it) },
                            onNavigateTab = { viewModel.selectTab(it) },
                            onUpdateScene = { idx, scene -> viewModel.updateScene(idx, scene) }
                        )
                    }
                    StudioTab.MY_MEDIA -> {
                        MyMediaStudioScreen(
                            uiState = uiState,
                            onAddMedia = { uris, isVideo -> viewModel.addMediaUris(uris, isVideo) },
                            onRemoveScene = { viewModel.removeScene(it) },
                            onUpdateScene = { idx, scene -> viewModel.updateScene(idx, scene) },
                            onNavigateTab = { viewModel.selectTab(it) }
                        )
                    }
                    StudioTab.SCRIPT_VOICE -> {
                        ScriptVoiceStudioScreen(
                            uiState = uiState,
                            onScriptChange = { viewModel.updateFullScript(it) },
                            onVoiceStyleChange = { viewModel.updateVoiceStyle(it) },
                            onPreviewVoice = { viewModel.previewVoice(it) },
                            onStopVoice = { viewModel.stopVoice() },
                            onRegenerateScript = { viewModel.generateAiScript() }
                        )
                    }
                    StudioTab.PREVIEW_EXPORT -> {
                        PreviewExportScreen(
                            uiState = uiState,
                            onTogglePlay = { viewModel.togglePreviewPlayback() },
                            onSeek = { viewModel.seekPreview(it) },
                            onToggleMute = { viewModel.toggleMute() },
                            onMusicVolumeChange = { viewModel.setMusicVolume(it) },
                            onVoiceVolumeChange = { viewModel.setVoiceVolume(it) },
                            onToggleMusic = { viewModel.toggleMusicEnabled() },
                            onExportVideo = { viewModel.exportVideo() },
                            onShareVideo = { ctx, video -> viewModel.shareVideo(ctx, video) },
                            onNavigateTab = { viewModel.selectTab(it) }
                        )
                    }
                    StudioTab.MY_VIDEOS -> {
                        MyVideosScreen(
                            uiState = uiState,
                            onShareVideo = { ctx, video -> viewModel.shareVideo(ctx, video) },
                            onDeleteVideo = { viewModel.deleteVideo(it) },
                            onRenameVideo = { path, name -> viewModel.renameVideo(path, name) }
                        )
                    }
                    StudioTab.BRAND_SETTINGS -> {
                        BrandSettingsScreen(
                            uiState = uiState,
                            onSaveProfile = { viewModel.saveBrandProfile(it) },
                            onUpdateLogoUri = { viewModel.updateBrandLogoUri(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudioBottomBar(
    currentTab: StudioTab,
    onTabSelected: (StudioTab) -> Unit
) {
    NavigationBar(
        containerColor = StudioSurfaceDark,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(StudioTab.AI_STUDIO, "AI ভিডিও", Icons.Default.AutoAwesome),
            Triple(StudioTab.MY_MEDIA, "মিডিয়া", Icons.Default.Collections),
            Triple(StudioTab.SCRIPT_VOICE, "স্ক্রিপ্ট", Icons.Default.RecordVoiceOver),
            Triple(StudioTab.PREVIEW_EXPORT, "প্রিভিউ", Icons.Default.PlayCircle),
            Triple(StudioTab.MY_VIDEOS, "ভিডিওসমূহ", Icons.Default.VideoLibrary),
            Triple(StudioTab.BRAND_SETTINGS, "ব্র্যান্ড", Icons.Default.Business)
        )

        items.forEach { (tab, label, icon) ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = StudioGoldPrimary,
                    selectedTextColor = StudioGoldPrimary,
                    indicatorColor = StudioMaroonSurface,
                    unselectedIconColor = StudioTextSecondary,
                    unselectedTextColor = StudioTextSecondary
                ),
                modifier = Modifier.testTag("tab_${tab.name}")
            )
        }
    }
}
