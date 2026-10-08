package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VoiceStyleOption
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
fun ScriptVoiceStudioScreen(
    uiState: StudioUiState,
    onScriptChange: (String) -> Unit,
    onVoiceStyleChange: (VoiceStyleOption) -> Unit,
    onPreviewVoice: (String) -> Unit,
    onStopVoice: () -> Unit,
    onRegenerateScript: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVoicePlaying by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioMaroonSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioCrimsonPrimary)
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
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = "Script & Voice",
                                    tint = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "স্ক্রিপ্ট ও ভয়েসওভার স্টুডিও",
                                color = StudioTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "প্রমোশনাল ও সিনেমাটিক বাংলা ভয়েস জেনারেটর",
                                color = StudioGoldAccent,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Voice Model Selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceDark),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "বাংলা এআই ভয়েস মডেল নির্বাচন করুন:",
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
                                color = if (isSelected) StudioCrimsonSecondary else StudioMaroonCard,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) StudioGoldPrimary else StudioBorder
                                ),
                                modifier = Modifier
                                    .clickable { onVoiceStyleChange(voice) }
                                    .testTag("btn_voice_${voice.name}")
                            ) {
                                Text(
                                    text = voice.displayNameBengali,
                                    color = if (isSelected) Color.White else StudioTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    // Voice preview button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val textToSpeak = uiState.project.fullBengaliScript.ifBlank {
                                    uiState.project.scenes.joinToString(" ") { it.voiceOverBengali }
                                }
                                onPreviewVoice(textToSpeak)
                                isVoicePlaying = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioGoldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_preview_full_voice")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ভয়েস টেস্ট করুন", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                onStopVoice()
                                isVoicePlaying = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_stop_voice")
                        ) {
                            Icon(imageVector = Icons.Default.Stop, contentDescription = null, tint = Color.Red)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("থামান", color = StudioTextPrimary)
                        }
                    }
                }
            }
        }

        // Editable Bengali Script
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceDark),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
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
                        Text(
                            text = "সম্পূর্ণ বাংলা স্ক্রিপ্ট (সম্পাদনাযোগ্য):",
                            color = StudioGoldPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        IconButton(onClick = onRegenerateScript) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Regenerate",
                                tint = StudioCrimsonSecondary
                            )
                        }
                    }

                    OutlinedTextField(
                        value = uiState.project.fullBengaliScript,
                        onValueChange = onScriptChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .testTag("input_full_script"),
                        colors = outlinedTextFieldColors()
                    )

                    Text(
                        text = "💡 টিপস: আপনি নিজের মতো বাক্য যোগ বা পরিবর্তন করতে পারেন। প্রতিটি দৃশ্যে এই স্ক্রিপ্ট সুবিন্যস্তভাবে উচ্চারিত হবে।",
                        color = StudioTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
