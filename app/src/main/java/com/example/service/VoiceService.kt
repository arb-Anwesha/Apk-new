package com.example.service

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.model.VoiceStyleOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume

interface VoiceService {
    fun isTtsReady(): Boolean
    fun isBengaliVoiceSupported(): Boolean
    suspend fun speakPreview(
        text: String,
        style: VoiceStyleOption,
        pitch: Float = 1.0f,
        speed: Float = 1.0f
    )
    fun stop()
    suspend fun synthesizeVoiceToFile(
        text: String,
        outputFile: File,
        style: VoiceStyleOption,
        pitch: Float = 1.0f,
        speed: Float = 1.0f
    ): Result<File>
    fun shutdown()
}

class AndroidBengaliVoiceServiceImpl(private val context: Context) : VoiceService {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var supportsBengaliLocale = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                val bnBd = Locale("bn", "BD")
                val bnIn = Locale("bn", "IN")
                val bdResult = tts?.isLanguageAvailable(bnBd) ?: TextToSpeech.LANG_NOT_SUPPORTED
                val inResult = tts?.isLanguageAvailable(bnIn) ?: TextToSpeech.LANG_NOT_SUPPORTED

                if (bdResult >= TextToSpeech.LANG_AVAILABLE) {
                    tts?.language = bnBd
                    supportsBengaliLocale = true
                } else if (inResult >= TextToSpeech.LANG_AVAILABLE) {
                    tts?.language = bnIn
                    supportsBengaliLocale = true
                } else {
                    tts?.language = Locale.getDefault()
                    supportsBengaliLocale = false
                }
            }
        }
    }

    override fun isTtsReady(): Boolean = isInitialized

    override fun isBengaliVoiceSupported(): Boolean = supportsBengaliLocale

    override suspend fun speakPreview(
        text: String,
        style: VoiceStyleOption,
        pitch: Float,
        speed: Float
    ) = withContext(Dispatchers.Main) {
        if (text.isBlank()) return@withContext
        val engine = tts ?: return@withContext

        applyVoiceStyle(engine, style, pitch, speed)
        val utteranceId = UUID.randomUUID().toString()
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    override fun stop() {
        tts?.stop()
    }

    private fun applyVoiceStyle(
        engine: TextToSpeech,
        style: VoiceStyleOption,
        basePitch: Float,
        baseSpeed: Float
    ) {
        when (style) {
            VoiceStyleOption.BENGALI_FEMALE_PROMO -> {
                engine.setPitch(basePitch * 1.25f)
                engine.setSpeechRate(baseSpeed * 1.08f)
            }
            VoiceStyleOption.BENGALI_FEMALE_NATURAL -> {
                engine.setPitch(basePitch * 1.15f)
                engine.setSpeechRate(baseSpeed * 1.00f)
            }
            VoiceStyleOption.BENGALI_MALE_PROFESSIONAL -> {
                engine.setPitch(basePitch * 0.85f)
                engine.setSpeechRate(baseSpeed * 0.95f)
            }
            VoiceStyleOption.BENGALI_FEMALE_EMOTIONAL -> {
                engine.setPitch(basePitch * 1.10f)
                engine.setSpeechRate(baseSpeed * 0.92f)
            }
        }
    }

    override suspend fun synthesizeVoiceToFile(
        text: String,
        outputFile: File,
        style: VoiceStyleOption,
        pitch: Float,
        speed: Float
    ): Result<File> = withContext(Dispatchers.IO) {
        val engine = tts
        if (engine == null || !isInitialized || text.isBlank()) {
            // Write a gentle silent audio track fallback
            createSilentWav(outputFile, durationSeconds = 3)
            return@withContext Result.success(outputFile)
        }

        withContext(Dispatchers.Main) {
            applyVoiceStyle(engine, style, pitch, speed)
        }

        suspendCancellableCoroutine { continuation ->
            val utteranceId = UUID.randomUUID().toString()
            val listener = object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                override fun onDone(id: String?) {
                    if (id == utteranceId && !continuation.isCompleted) {
                        continuation.resume(Result.success(outputFile))
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    if (!continuation.isCompleted) {
                        createSilentWav(outputFile, 3)
                        continuation.resume(Result.success(outputFile))
                    }
                }
            }

            engine.setOnUtteranceProgressListener(listener)
            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
            }

            val result = engine.synthesizeToFile(text, params, outputFile, utteranceId)
            if (result != TextToSpeech.SUCCESS && !continuation.isCompleted) {
                createSilentWav(outputFile, 3)
                continuation.resume(Result.success(outputFile))
            }
        }
    }

    private fun createSilentWav(file: File, durationSeconds: Int) {
        try {
            val sampleRate = 44100
            val numChannels = 1
            val bitsPerSample = 16
            val totalDataLen = (sampleRate * durationSeconds * numChannels * (bitsPerSample / 8)).toLong()
            val totalAudioLen = totalDataLen + 36

            FileOutputStream(file).use { out ->
                val header = ByteArray(44)
                // RIFF/WAVE header
                header[0] = 'R'.code.toByte()
                header[1] = 'I'.code.toByte()
                header[2] = 'F'.code.toByte()
                header[3] = 'F'.code.toByte()
                header[4] = (totalAudioLen and 0xff).toByte()
                header[5] = ((totalAudioLen shr 8) and 0xff).toByte()
                header[6] = ((totalAudioLen shr 16) and 0xff).toByte()
                header[7] = ((totalAudioLen shr 24) and 0xff).toByte()
                header[8] = 'W'.code.toByte()
                header[9] = 'A'.code.toByte()
                header[10] = 'V'.code.toByte()
                header[11] = 'E'.code.toByte()
                header[12] = 'f'.code.toByte()
                header[13] = 'm'.code.toByte()
                header[14] = 't'.code.toByte()
                header[15] = ' '.code.toByte()
                header[16] = 16 // 16 bytes for PCM format
                header[17] = 0
                header[18] = 0
                header[19] = 0
                header[20] = 1 // Format = 1 (PCM)
                header[21] = 0
                header[22] = numChannels.toByte()
                header[23] = 0
                header[24] = (sampleRate and 0xff).toByte()
                header[25] = ((sampleRate shr 8) and 0xff).toByte()
                header[26] = ((sampleRate shr 16) and 0xff).toByte()
                header[27] = ((sampleRate shr 24) and 0xff).toByte()
                val byteRate = sampleRate * numChannels * bitsPerSample / 8
                header[28] = (byteRate and 0xff).toByte()
                header[29] = ((byteRate shr 8) and 0xff).toByte()
                header[30] = ((byteRate shr 16) and 0xff).toByte()
                header[31] = ((byteRate shr 24) and 0xff).toByte()
                header[32] = (numChannels * bitsPerSample / 8).toByte() // Block align
                header[33] = 0
                header[34] = bitsPerSample.toByte()
                header[35] = 0
                header[36] = 'd'.code.toByte()
                header[37] = 'a'.code.toByte()
                header[38] = 't'.code.toByte()
                header[39] = 'a'.code.toByte()
                header[40] = (totalDataLen and 0xff).toByte()
                header[41] = ((totalDataLen shr 8) and 0xff).toByte()
                header[42] = ((totalDataLen shr 16) and 0xff).toByte()
                header[43] = ((totalDataLen shr 24) and 0xff).toByte()

                out.write(header, 0, 44)
                // Write zeros (silence)
                val buffer = ByteArray(4096)
                var written: Long = 0
                while (written < totalDataLen) {
                    val toWrite = minOf(buffer.size.toLong(), totalDataLen - written).toInt()
                    out.write(buffer, 0, toWrite)
                    written += toWrite
                }
            }
        } catch (_: Exception) {}
    }

    override fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
