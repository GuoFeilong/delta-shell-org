package com.delta.helper.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.DrawableRes
import java.io.IOException

/**
 * 通过 MediaStore 写入系统相册（Android 10+ 无需存储权限）。
 */
object GalleryImageSaver {
    fun saveDrawableToPictures(
        context: Context,
        @DrawableRes drawableResId: Int,
        displayName: String,
        albumDir: String = "画质怪兽",
    ): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return false
        val bitmap = BitmapFactory.decodeResource(context.resources, drawableResId) ?: return false
        return try {
            saveBitmapToPictures(context, bitmap, displayName, albumDir)
        } finally {
            bitmap.recycle()
        }
    }

    private fun saveBitmapToPictures(
        context: Context,
        bitmap: Bitmap,
        displayName: String,
        albumDir: String,
    ): Boolean {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$albumDir")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(collection, values) ?: return false
        return try {
            resolver.openOutputStream(uri)?.use { output ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 92, output)) {
                    throw IOException("bitmap compress failed")
                }
            } ?: throw IOException("openOutputStream failed")

            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            true
        } catch (_: Exception) {
            resolver.delete(uri, null, null)
            false
        }
    }
}
