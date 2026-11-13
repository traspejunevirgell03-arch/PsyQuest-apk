package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageEncodingUtils {

  suspend fun encodeUriToCompressedBase64(
    context: Context,
    uri: Uri,
    maxDimensionPx: Int = 720,
    maxRawBytes: Int = 220_000,
  ): String? =
    withContext(Dispatchers.IO) {
      runCatching {
          val resolver = context.applicationContext.contentResolver
          val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
          resolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, bounds)
          }

          val maxSide = max(bounds.outWidth, bounds.outHeight).coerceAtLeast(1)
          var sampleSize = 1
          while (maxSide / sampleSize > maxDimensionPx * 2) {
            sampleSize *= 2
          }

          val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
          val decoded =
            resolver.openInputStream(uri)?.use { stream ->
              BitmapFactory.decodeStream(stream, null, decodeOptions)
            } ?: return@runCatching null

          val scaled = scaleBitmapIfNeeded(decoded, maxDimensionPx)
          val output = ByteArrayOutputStream()
          var quality = 86
          do {
            output.reset()
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, output)
            quality -= 12
          } while (output.size() > maxRawBytes && quality >= 30)

          if (scaled !== decoded) decoded.recycle()
          scaled.recycle()

          Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
        }
        .getOrNull()
    }

  fun decodeBase64ToImageBitmap(base64: String?): ImageBitmap? {
    if (base64.isNullOrBlank()) return null
    return runCatching {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
      }
      .getOrNull()
  }

  private fun scaleBitmapIfNeeded(bitmap: Bitmap, maxDimension: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    if (width <= maxDimension && height <= maxDimension) return bitmap
    val ratio = maxDimension.toFloat() / max(width, height).toFloat()
    val newW = (width * ratio).roundToInt().coerceAtLeast(1)
    val newH = (height * ratio).roundToInt().coerceAtLeast(1)
    return Bitmap.createScaledBitmap(bitmap, newW, newH, true)
  }
}
