package com.example.data.model

data class ExportedVideo(
    val id: String,
    val title: String,
    val filePath: String,
    val fileSizeBytes: Long,
    val durationSeconds: Int,
    val createdAtMillis: Long,
    val thumbnailPath: String? = null
)
