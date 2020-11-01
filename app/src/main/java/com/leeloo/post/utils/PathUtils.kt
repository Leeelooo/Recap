package com.leeloo.post.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object PathUtils {
    fun getPath(context: Context, uri: Uri): Uri {
        val inputStream = context.contentResolver.openInputStream(uri)
        val cacheFile = File(context.cacheDir, "cache.jpg")
        val outputStream = FileOutputStream(cacheFile)
        inputStream?.copyTo(outputStream)
        return Uri.parse("file://" + cacheFile.path)
    }
}