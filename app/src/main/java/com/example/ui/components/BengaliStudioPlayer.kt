package com.example.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.BrandProfile
import com.example.data.model.LogoPosition
import com.example.data.model.SceneItem
import com.example.ui.theme.StudioCrimsonPrimary
import com.example.ui.theme.StudioCrimsonSecondary
import com.example.ui.theme.StudioGoldAccent
import com.example.ui.theme.StudioGoldPrimary
import com.example.ui.theme.StudioMaroonSurface
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@Composable
fun BengaliStudioPlayer(
    scenes: List<SceneItem>,
    activeSceneIndex: Int,
    brand: BrandProfile,
    isPlaying: Boolean,
    currentTimeSeconds: Float,
    totalDurationSeconds: Float,
    isMuted: Boolean,
    onTogglePlay: () -> Unit,
    onSeek: (Float) -> Unit,
    onToggleMute: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeScene = scenes.getOrNull(activeSceneIndex) ?: scenes.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF140D17))
            .border(1.dp, Color(0xFF3F2538), RoundedCornerShape(24.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 9:16 Mobile-Friendly Aspect Ratio Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(9f / 16f)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.Black)
                .border(2.dp, StudioGoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .testTag("bengali_video_preview_screen")
        ) {
            if (activeScene != null) {
                // Background or User Image
                if (!activeScene.mediaUri.isNullOrBlank()) {
                    AsyncImage(
                        model = Uri.parse(activeScene.mediaUri),
                        contentDescription = "Scene media",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Rich Cinematic Studio Gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(activeScene.backgroundThemeColor),
                                        Color(0xFF180A12),
                                        Color.Black
                                    )
                                )
                            )
                    ) {
                        // Decorative glowing rings
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .align(Alignment.Center)
                                .border(1.5.dp, StudioGoldAccent.copy(alpha = 0.15f), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(170.dp)
                                .align(Alignment.Center)
                                .border(1.dp, StudioGoldPrimary.copy(alpha = 0.25f), CircleShape)
                        )

                        // Center scene prompt visual note
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                color = StudioCrimsonPrimary.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StudioGoldPrimary)
                            ) {
                                Text(
                                    text = activeScene.titleBengali,
                                    color = StudioGoldAccent,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = activeScene.visualDescriptionBengali.ifBlank { "সিনেমাটিক AI ভিজ্যুয়াল ফ্রেম" },
                                color = StudioTextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 4,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }
                }

                // Brand Watermark Overlay
                if (brand.isWatermarkEnabled) {
                    val alignment = when (brand.logoPosition) {
                        LogoPosition.TOP_LEFT -> Alignment.TopStart
                        LogoPosition.TOP_RIGHT -> Alignment.TopEnd
                        LogoPosition.BOTTOM_LEFT -> Alignment.BottomStart
                        LogoPosition.BOTTOM_RIGHT -> Alignment.BottomEnd
                    }
                    Box(
                        modifier = Modifier
                            .align(alignment)
                            .padding(14.dp)
                            .alpha(brand.logoOpacity)
                    ) {
                        if (!brand.logoUri.isNullOrBlank()) {
                            AsyncImage(
                                model = Uri.parse(brand.logoUri),
                                contentDescription = "Brand Logo",
                                modifier = Modifier
                                    .size((64 * (brand.logoSizePercent / 0.20f)).dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Surface(
                                color = StudioMaroonSurface,
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StudioGoldPrimary)
                            ) {
                                Text(
                                    text = brand.businessName.take(14),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Bengali On-Screen Caption Pill
                val captionText = activeScene.captionBengali.ifBlank { activeScene.titleBengali }
                if (captionText.isNotBlank()) {
                    val captionAlign = when {
                        activeScene.captionPosition.contains("উপরে") -> Alignment.TopCenter
                        activeScene.captionPosition.contains("মাঝখানে") -> Alignment.Center
                        else -> Alignment.BottomCenter
                    }

                    Box(
                        modifier = Modifier
                            .align(captionAlign)
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                    ) {
                        Surface(
                            color = Color(0xDC140810),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, StudioGoldPrimary),
                            shadowElevation = 8.dp
                        ) {
                            Text(
                                text = captionText,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                // Live Scene Tag indicator on top
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isPlaying) Color(0xFF22C55E) else StudioCrimsonSecondary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "দৃশ্য ${activeScene.index}/${scenes.size} (${activeScene.durationSeconds}s)",
                        color = StudioTextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress Slider
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatSeconds(currentTimeSeconds),
                color = StudioGoldAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Slider(
                value = currentTimeSeconds,
                onValueChange = onSeek,
                valueRange = 0f..totalDurationSeconds.coerceAtLeast(1f),
                modifier = Modifier
                    .weight(1f)
                    .testTag("preview_seek_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = StudioGoldPrimary,
                    activeTrackColor = StudioCrimsonSecondary,
                    inactiveTrackColor = Color(0xFF33202E)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formatSeconds(totalDurationSeconds),
                color = StudioTextSecondary,
                fontSize = 12.sp
            )
        }

        // Control Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleMute,
                modifier = Modifier.testTag("btn_toggle_mute")
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                    contentDescription = "Toggle Mute",
                    tint = if (isMuted) Color.Gray else StudioGoldAccent
                )
            }

            // Main Play/Pause Button
            Surface(
                onClick = onTogglePlay,
                shape = CircleShape,
                color = StudioCrimsonSecondary,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .size(54.dp)
                    .testTag("btn_play_pause")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            IconButton(
                onClick = { onSeek(0f) },
                modifier = Modifier.testTag("btn_replay")
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Replay",
                    tint = StudioTextSecondary
                )
            }
        }
    }
}

private fun formatSeconds(seconds: Float): String {
    val totalSec = seconds.toInt()
    val mins = totalSec / 60
    val secs = totalSec % 60
    return String.format(java.util.Locale.US, "%02d:%02d", mins, secs)
}
