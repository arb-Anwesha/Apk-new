package com.example.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.ExportedVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File

class VideoLibraryRepository(private val context: Context) {
    private val videoDir: File = File(context.filesDir, "videos").apply {
        if (!exists()) mkdirs()
    }

    private val _videos = MutableStateFlow<List<ExportedVideo>>(emptyList())
    val videos: StateFlow<List<ExportedVideo>> = _videos.asStateFlow()

    suspend fun refreshVideos() = withContext(Dispatchers.IO) {
        val list = mutableListOf<ExportedVideo>()
        val files = videoDir.listFiles { file -> file.extension.lowercase() == "mp4" } ?: emptyArray()
        
        files.sortedByDescending { it.lastModified() }.forEach { file ->
            val id = file.nameWithoutExtension
            val size = file.length()
            val date = file.lastModified()
            // Estimate duration or default
            val estimatedDuration = 30
            list.add(
                ExportedVideo(
                    id = id,
                    title = file.nameWithoutExtension.replace('_', ' '),
                    filePath = file.absolutePath,
                    fileSizeBytes = size,
                    durationSeconds = estimatedDuration,
                    createdAtMillis = date,
                    thumbnailPath = null
                )
            )
        }
        _videos.value = list
    }

    suspend fun deleteVideo(filePath: String): Boolean = withContext(Dispatchers.IO) {
        val file = File(filePath)
        val deleted = if (file.exists()) file.delete() else false
        refreshVideos()
        deleted
    }

    suspend fun renameVideo(filePath: String, newName: String): Boolean = withContext(Dispatchers.IO) {
        val file = File(filePath)
        if (!file.exists()) return@withContext false
        val sanitized = newName.trim().replace("[^a-zA-Z0-9\\u0980-\\u09FF _-]".toRegex(), "_")
        val newFile = File(file.parentFile, "$sanitized.mp4")
        val success = file.renameTo(newFile)
        refreshVideos()
        success
    }

    fun getShareableUri(video: ExportedVideo): Uri {
        val file = File(video.filePath)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun getVideoDirectory(): File = videoDir
}
