package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExportedVideo
import com.example.data.model.MusicPreferenceOption
import com.example.ui.StudioTab
import com.example.ui.StudioUiState
import com.example.ui.components.BengaliStudioPlayer
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCrimsonPrimary
import com.example.ui.theme.StudioCrimsonSecondary
import com.example.ui.theme.StudioGoldAccent
import com.example.ui.theme.StudioGoldPrimary
import com.example.ui.theme.StudioMaroonSurface
import com.example.ui.theme.StudioSuccess
import com.example.ui.theme.StudioSurfaceDark
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@Composable
fun PreviewExportScreen(
    uiState: StudioUiState,
    onTogglePlay: () -> Unit,
    onSeek: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onMusicVolumeChange: (Float) -> Unit,
    onVoiceVolumeChange: (Float) -> Unit,
    onToggleMusic: () -> Unit,
    onExportVideo: () -> Unit,
    onShareVideo: (Context, ExportedVideo) -> Unit,
    onNavigateTab: (StudioTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Player Component
        item {
            BengaliStudioPlayer(
                scenes = uiState.project.scenes,
                activeSceneIndex = uiState.activePlayingSceneIndex,
                brand = uiState.brand,
                isPlaying = uiState.isPreviewPlaying,
                currentTimeSeconds = uiState.previewProgressSeconds,
                totalDurationSeconds = uiState.project.totalCalculatedDurationSeconds.toFloat(),
                isMuted = uiState.isPreviewMuted,
                onTogglePlay = onTogglePlay,
                onSeek = onSeek,
                onToggleMute = onToggleMute
            )
        }

        // Audio & Music Settings Mixer
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceDark),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, tint = StudioGoldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "আবহ সঙ্গীত ও সাউন্ড মিক্সার",
                                color = StudioTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Switch(
                            checked = uiState.project.isMusicEnabled,
                            onCheckedChange = { onToggleMusic() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = StudioGoldPrimary,
                                checkedTrackColor = StudioCrimsonSecondary
                            )
                        )
                    }

                    if (uiState.project.isMusicEnabled) {
                        // Music volume slider
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("ব্যাকগ্রাউন্ড মিউজিক ভলিউম", color = StudioTextSecondary, fontSize = 12.sp)
                                Text("${(uiState.project.musicVolume * 100).toInt()}%", color = StudioGoldAccent, fontSize = 12.sp)
                            }
                            Slider(
                                value = uiState.project.musicVolume,
                                onValueChange = onMusicVolumeChange,
                                colors = SliderDefaults.colors(
                                    thumbColor = StudioGoldPrimary,
                                    activeTrackColor = StudioCrimsonSecondary
                                )
                            )
                        }
                    }

                    // Voice volume slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("বাংলা ভয়েসওভার ভলিউম", color = StudioTextSecondary, fontSize = 12.sp)
                            Text("${(uiState.project.voiceVolume * 100).toInt()}%", color = StudioGoldAccent, fontSize = 12.sp)
                        }
                        Slider(
                            value = uiState.project.voiceVolume,
                            onValueChange = onVoiceVolumeChange,
                            colors = SliderDefaults.colors(
                                thumbColor = StudioGoldPrimary,
                                activeTrackColor = StudioCrimsonSecondary
                            )
                        )
                    }
                }
            }
        }

        // Export Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioMaroonSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioCrimsonSecondary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "ফাইনাল MP4 ভিডিও এক্সপোর্ট",
                        color = StudioTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "রেজোলিউশন: 720p/1080p (মোবাইল ও রিলস ফ্রেন্ডলি MP4 ফরম্যাট)",
                        color = StudioGoldAccent,
                        fontSize = 12.sp
                    )

                    // Export button
                    Button(
                        onClick = onExportVideo,
                        enabled = !uiState.isExporting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("btn_export_mp4"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudioCrimsonSecondary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.isExporting) "এক্সপোর্ট হচ্ছে..." else "ভিডিও তৈরি করুন ও MP4 সংরক্ষণ করুন",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Export Progress Bar
                    AnimatedVisibility(visible = uiState.isExporting) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LinearProgressIndicator(
                                progress = { uiState.exportProgressPercent / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = StudioGoldPrimary,
                                trackColor = Color(0xFF3B1E2B)
                            )
                            Text(
                                text = "${uiState.exportProgressPercent}% – ${uiState.exportStatusText}",
                                color = StudioGoldAccent,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Export Success Card
                    AnimatedVisibility(visible = uiState.lastExportedFile != null && !uiState.isExporting) {
                        uiState.lastExportedFile?.let { file ->
                            Surface(
                                color = Color(0xFF132B1A),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StudioSuccess),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = StudioSuccess)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "ভিডিও সফলভাবে এক্সপোর্ট হয়েছে!",
                                            color = StudioSuccess,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Text(
                                        text = "ফাইল: ${file.name} (${(file.length() / 1024)} KB)",
                                        color = StudioTextPrimary,
                                        fontSize = 12.sp
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                val exported = ExportedVideo(
                                                    id = file.nameWithoutExtension,
                                                    title = file.nameWithoutExtension,
                                                    filePath = file.absolutePath,
                                                    fileSizeBytes = file.length(),
                                                    durationSeconds = uiState.project.totalCalculatedDurationSeconds,
                                                    createdAtMillis = System.currentTimeMillis()
                                                )
                                                onShareVideo(context, exported)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = StudioSuccess),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("btn_share_exported_now")
                                        ) {
                                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("শেয়ার করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = { onNavigateTab(StudioTab.MY_VIDEOS) },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("আমার ভিডিও", fontSize = 12.sp, color = StudioTextPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
