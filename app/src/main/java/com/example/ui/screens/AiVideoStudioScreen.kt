package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SceneItem
import com.example.data.model.VideoDurationOption
import com.example.data.model.VideoStyleOption
import com.example.data.model.VoiceStyleOption
import com.example.ui.StudioTab
import com.example.ui.StudioUiState
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCrimsonPrimary
import com.example.ui.theme.StudioCrimsonSecondary
import com.example.ui.theme.StudioGoldAccent
import com.example.ui.theme.StudioGoldPrimary
import com.example.ui.theme.StudioMaroonCard
import com.example.ui.theme.StudioMaroonSurface
import com.example.ui.theme.StudioSurfaceDark
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiVideoStudioScreen(
    uiState: StudioUiState,
    onTopicChange: (String) -> Unit,
    onPurposeChange: (String) -> Unit,
    onDurationChange: (VideoDurationOption) -> Unit,
    onStyleChange: (VideoStyleOption) -> Unit,
    onVoiceChange: (VoiceStyleOption) -> Unit,
    onInstructionsChange: (String) -> Unit,
    onGenerateClick: () -> Unit,
    onPreviewVoice: (String) -> Unit,
    onNavigateTab: (StudioTab) -> Unit,
    onUpdateScene: (Int, SceneItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingSceneIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioMaroonSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioCrimsonPrimary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = StudioCrimsonSecondary,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Video",
                                    tint = StudioGoldAccent
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "AI দিয়ে প্রফেশনাল বাংলা ভিডিও",
                                color = StudioTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "প্রম্পট লিখুন → স্ক্রিপ্ট ও দৃশ্য তৈরি → এমপি৪ এক্সপোর্ট",
                                color = StudioGoldAccent,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Form Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceDark),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "১. ভিডিওর মূল বিষয় ও উদ্দেশ্য",
                        color = StudioGoldPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = uiState.project.topic,
                        onValueChange = onTopicChange,
                        label = { Text("ভিডিওর বিষয় / প্রোডাক্ট / খাবার") },
                        placeholder = { Text("যেমন: ঐতিহ্যবাহী বিরিয়ানি ও ঘরোয়া খাবার") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_video_topic"),
                        colors = outlinedTextFieldColors()
                    )

                    OutlinedTextField(
                        value = uiState.project.videoPurpose,
                        onValueChange = onPurposeChange,
                        label = { Text("ভিডিওর উদ্দেশ্য (Goal)") },
                        placeholder = { Text("যেমন: ফেসবুক ও ইনস্টাগ্রামে হোম ডেলিভারি প্রচার") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_video_purpose"),
                        colors = outlinedTextFieldColors()
                    )

                    // Video Duration (Minimum 30s)
                    Text(
                        text = "২. ভিডিওর সময়সীমা (Duration - সর্বনিম্ন ৩০ সেকেন্ড)",
                        color = StudioGoldPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VideoDurationOption.values().forEach { option ->
                            val isSelected = uiState.project.targetDuration == option
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) StudioCrimsonSecondary else StudioMaroonCard,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) StudioGoldPrimary else StudioBorder
                                ),
                                modifier = Modifier
                                    .clickable { onDurationChange(option) }
                                    .testTag("btn_duration_${option.seconds}")
                            ) {
                                Text(
                                    text = option.displayNameBengali,
                                    color = if (isSelected) Color.White else StudioTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    // Video Style
                    Text(
                        text = "৩. ভিডিওর ধরণ (Video Style)",
                        color = StudioGoldPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VideoStyleOption.values().forEach { style ->
                            val isSelected = uiState.project.videoStyle == style
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) StudioCrimsonPrimary else StudioMaroonCard,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) StudioGoldPrimary else StudioBorder
                                ),
                                modifier = Modifier
                                    .clickable { onStyleChange(style) }
                                    .testTag("btn_style_${style.name}")
                            ) {
                                Text(
                                    text = style.displayNameBengali,
                                    color = if (isSelected) Color.White else StudioTextSecondary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    // Voice Style
                    Text(
                        text = "৪. বাংলা ভয়েসওভার ধরণ (Voice Style)",
                        color = StudioGoldPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VoiceStyleOption.values().forEach { voice ->
                            val isSelected = uiState.project.voiceStyle == voice
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) StudioCrimsonPrimary else StudioMaroonCard,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) StudioGoldPrimary else StudioBorder
                                ),
                                modifier = Modifier.clickable { onVoiceChange(voice) }
                            ) {
                                Text(
                                    text = voice.displayNameBengali,
                                    color = if (isSelected) Color.White else StudioTextSecondary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.project.additionalInstructions,
                        onValueChange = onInstructionsChange,
                        label = { Text("অতিরিক্ত নির্দেশনা (ঐচ্ছিক)") },
                        placeholder = { Text("যেমন: অর্ডারে ১০% ছাড় ও বহরমপুর লোকেশন উল্লেখ করুন") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors()
                    )

                    // Generate Button
                    Button(
                        onClick = onGenerateClick,
                        enabled = !uiState.isGeneratingScript,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_generate_ai_video"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudioCrimsonSecondary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (uiState.isGeneratingScript) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("এআই দৃশ্য তৈরি করছে...", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI দিয়ে ভিডিও ও দৃশ্য তৈরি করুন", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (uiState.scriptStatusNote != null) {
                        Text(
                            text = "ℹ ${uiState.scriptStatusNote}",
                            color = StudioGoldAccent,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Generated Scenes Plan Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "পরিকল্পিত দৃশ্য ও সিনসমূহ (${uiState.project.scenes.size}টি দৃশ্য)",
                        color = StudioTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "মোট সময়: ${uiState.project.totalCalculatedDurationSeconds} সেকেন্ড",
                        color = StudioGoldAccent,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = { onNavigateTab(StudioTab.PREVIEW_EXPORT) },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioGoldPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_goto_preview")
                ) {
                    Text("প্রিভিউ দেখুন", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Black)
                }
            }
        }

        // Scene List
        itemsIndexed(uiState.project.scenes) { index, scene ->
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioMaroonCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scene_card_$index")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = StudioCrimsonPrimary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${scene.titleBengali} (${scene.durationSeconds}s)",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Row {
                            IconButton(
                                onClick = { onPreviewVoice(scene.voiceOverBengali) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Preview voice",
                                    tint = StudioGoldAccent
                                )
                            }
                            IconButton(
                                onClick = {
                                    editingSceneIndex = if (editingSceneIndex == index) null else index
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit scene",
                                    tint = StudioTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "🎥 ভিজ্যুয়াল: ${scene.visualDescriptionBengali}",
                        color = StudioTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "🎙 ভয়েসওভার: \"${scene.voiceOverBengali}\"",
                        color = StudioGoldAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "📝 অন-স্ক্রিন ক্যাপশন: ${scene.captionBengali}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Inline Editor for the Scene
                    AnimatedVisibility(visible = editingSceneIndex == index) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("দৃশ্য সম্পাদনা করুন:", color = StudioGoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                            OutlinedTextField(
                                value = scene.voiceOverBengali,
                                onValueChange = { onUpdateScene(index, scene.copy(voiceOverBengali = it)) },
                                label = { Text("ভয়েসওভার টেক্সট") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = outlinedTextFieldColors()
                            )

                            OutlinedTextField(
                                value = scene.captionBengali,
                                onValueChange = { onUpdateScene(index, scene.copy(captionBengali = it)) },
                                label = { Text("অন-স্ক্রিন বাংলা ক্যাপশন") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = outlinedTextFieldColors()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = StudioGoldPrimary,
    unfocusedBorderColor = StudioBorder,
    focusedLabelColor = StudioGoldPrimary,
    unfocusedLabelColor = StudioTextSecondary,
    focusedTextColor = StudioTextPrimary,
    unfocusedTextColor = StudioTextPrimary
)
