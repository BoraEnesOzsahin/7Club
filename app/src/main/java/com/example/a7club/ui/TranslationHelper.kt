package com.example.a7club.utils // Kendi paket ismine dikkat et

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

object TranslationHelper {
    // Türkçe -> İngilizce Çevirmen Ayarları
    private val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.TURKISH)
        .setTargetLanguage(TranslateLanguage.ENGLISH)
        .build()

    private val turkishEnglishTranslator = Translation.getClient(options)
    private var isModelDownloaded = false

    // Modeli İndir (İlk açılışta bir kez çalışır)
    suspend fun downloadModelIfNeeded() {
        if (!isModelDownloaded) {
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()
            try {
                turkishEnglishTranslator.downloadModelIfNeeded(conditions).await()
                isModelDownloaded = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Çeviri Fonksiyonu
    suspend fun translate(text: String): String {
        return try {
            // Model inik değilse önce indirmeyi dene
            if (!isModelDownloaded) downloadModelIfNeeded()

            // Çeviriyi yap
            turkishEnglishTranslator.translate(text).await()
        } catch (e: Exception) {
            // Hata olursa (internet yoksa vs.) orijinal metni döndür
            text
        }
    }
}