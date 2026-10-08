package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.BrandProfile
import com.example.data.model.LogoPosition
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

@Composable
fun BrandSettingsScreen(
    uiState: StudioUiState,
    onSaveProfile: (BrandProfile) -> Unit,
    onUpdateLogoUri: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var businessName by remember(uiState.brand) { mutableStateOf(uiState.brand.businessName) }
    var tagline by remember(uiState.brand) { mutableStateOf(uiState.brand.tagline) }
    var location by remember(uiState.brand) { mutableStateOf(uiState.brand.location) }
    var phone by remember(uiState.brand) { mutableStateOf(uiState.brand.phone) }
    var website by remember(uiState.brand) { mutableStateOf(uiState.brand.website) }
    var logoPosition by remember(uiState.brand) { mutableStateOf(uiState.brand.logoPosition) }
    var logoSize by remember(uiState.brand) { mutableStateOf(uiState.brand.logoSizePercent) }
    var logoOpacity by remember(uiState.brand) { mutableStateOf(uiState.brand.logoOpacity) }
    var watermarkEnabled by remember(uiState.brand) { mutableStateOf(uiState.brand.isWatermarkEnabled) }

    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onUpdateLogoUri(uri.toString())
        }
    }

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
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = StudioCrimsonSecondary,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ব্র্যান্ড ও বিজনেস সেটিংস",
                            color = StudioTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "প্রতিটি ভিডিওতে স্বয়ংক্রিয় লোগো ও ব্র্যান্ড তথ্য",
                            color = StudioGoldAccent,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Logo Upload & Watermark Settings
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
                            text = "ব্র্যান্ড লোগো ও ওয়াটারমার্ক",
                            color = StudioGoldPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Switch(
                            checked = watermarkEnabled,
                            onCheckedChange = { watermarkEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = StudioGoldPrimary,
                                checkedTrackColor = StudioCrimsonSecondary
                            )
                        )
                    }

                    // Logo preview row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF280B18))
                                .border(1.5.dp, StudioGoldPrimary, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!uiState.brand.logoUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = Uri.parse(uiState.brand.logoUri),
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .alpha(logoOpacity),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Text(
                                    text = "লোগো\nনেই",
                                    color = StudioTextSecondary,
                                    fontSize = 11.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    logoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StudioCrimsonSecondary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_upload_logo")
                            ) {
                                Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("লোগো আপলোড করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            if (!uiState.brand.logoUri.isNullOrBlank()) {
                                OutlinedButton(
                                    onClick = { onUpdateLogoUri(null) },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("লোগো মুছুন", color = Color.Red, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Logo Position
                    Text("লোগো পজিশন (ভিডিওর ওপর অবস্থান):", color = StudioTextSecondary, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LogoPosition.values().forEach { pos ->
                            val isSel = logoPosition == pos
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) StudioCrimsonSecondary else StudioMaroonCard,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSel) StudioGoldPrimary else StudioBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { logoPosition = pos }
                            ) {
                                Text(
                                    text = pos.displayNameBengali,
                                    color = if (isSel) Color.White else StudioTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    // Logo Opacity slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("লোগোর দৃশ্যমানতা (Opacity)", color = StudioTextSecondary, fontSize = 12.sp)
                            Text("${(logoOpacity * 100).toInt()}%", color = StudioGoldAccent, fontSize = 12.sp)
                        }
                        Slider(
                            value = logoOpacity,
                            onValueChange = { logoOpacity = it },
                            valueRange = 0.2f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = StudioGoldPrimary,
                                activeTrackColor = StudioCrimsonSecondary
                            )
                        )
                    }
                }
            }
        }

        // Business Information
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
                        text = "ব্র্যান্ড ও যোগাযোগের তথ্য (স্বয়ংক্রিয় CTA)",
                        color = StudioGoldPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("প্রতিষ্ঠানের নাম (Business Name)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_brand_name"),
                        colors = outlinedTextFieldColors()
                    )

                    OutlinedTextField(
                        value = tagline,
                        onValueChange = { tagline = it },
                        label = { Text("ট্যাগলাইন (Tagline)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors()
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("ঠিকানা / অবস্থান (Location)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors()
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("ফোন / হোয়াটসঅ্যাপ (Phone / WhatsApp)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors()
                    )

                    OutlinedTextField(
                        value = website,
                        onValueChange = { website = it },
                        label = { Text("ওয়েবসাইট / সোশ্যাল লিংক (Website)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors()
                    )

                    // Save Brand Profile Button
                    Button(
                        onClick = {
                            val updated = uiState.brand.copy(
                                businessName = businessName,
                                tagline = tagline,
                                location = location,
                                phone = phone,
                                website = website,
                                logoPosition = logoPosition,
                                logoSizePercent = logoSize,
                                logoOpacity = logoOpacity,
                                isWatermarkEnabled = watermarkEnabled
                            )
                            onSaveProfile(updated)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_save_brand"),
                        colors = ButtonDefaults.buttonColors(containerColor = StudioGoldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ব্র্যান্ড সেটিংস সংরক্ষণ করুন", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
