package com.example.login_app.domain.face_detection

import android.content.Context
import android.graphics.Bitmap
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MLKitFaceDetector(private val context: Context) {

    private val highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .build()
    private val highAccuracyFaceDetector = FaceDetection.getClient(highAccuracyOpts)

    suspend fun getCroppedFaceFromBitmap(bitmap: Bitmap): Result<Bitmap> =
        withContext(Dispatchers.IO) {
            try {
                val faces = Tasks.await(
                    highAccuracyFaceDetector.process(InputImage.fromBitmap(bitmap, 0))
                )
                if (faces.isEmpty()) {
                    return@withContext Result.failure(Exception("No se detectó ningún rostro"))
                }

                // Tomar la cara más grande
                val largestFace = faces.maxByOrNull {
                    it.boundingBox.width() * it.boundingBox.height()
                } ?: return@withContext Result.failure(Exception("No se detectó rostro"))

                val rect = largestFace.boundingBox

                // Añadir un margen del 20% alrededor de la cara
                val marginX = (rect.width() * 0.2f).toInt()
                val marginY = (rect.height() * 0.2f).toInt()

                val left = (rect.left - marginX).coerceAtLeast(0)
                val top = (rect.top - marginY).coerceAtLeast(0)
                val right = (rect.right + marginX).coerceAtMost(bitmap.width)
                val bottom = (rect.bottom + marginY).coerceAtMost(bitmap.height)

                val cropped = Bitmap.createBitmap(
                    bitmap, left, top, right - left, bottom - top
                )
                Result.success(cropped)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}