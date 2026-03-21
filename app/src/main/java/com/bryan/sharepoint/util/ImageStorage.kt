package com.bryan.sharepoint.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImageStorage {

    /**
     * 将外部URI图片复制到应用私有目录
     * @return 私有目录中的文件路径，失败返回null
     */
    fun copyToPrivateStorage(context: Context, sourceUri: Uri): String? {
        return try {
            val imagesDir = File(context.filesDir, "images").apply {
                if (!exists()) mkdirs()
            }
            
            val fileName = "${UUID.randomUUID()}.jpg"
            val destFile = File(imagesDir, fileName)
            
            context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                FileOutputStream(destFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 删除私有目录中的图片
     */
    fun deleteImage(imagePath: String) {
        try {
            File(imagePath).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取私有目录图片文件
     */
    fun getImageFile(imagePath: String): File? {
        val file = File(imagePath)
        return if (file.exists()) file else null
    }

    /**
     * 清空所有私有图片（谨慎使用）
     */
    fun clearAllImages(context: Context) {
        try {
            val imagesDir = File(context.filesDir, "images")
            imagesDir.listFiles()?.forEach { it.delete() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
