package com.example.login_app.domain.usecase

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object FaceCaptureHelper {

    suspend fun captureImageBytes(context: Context, imageCapture: ImageCapture): ByteArray? =
        suspendCancellableCoroutine { continuation ->
            imageCapture.takePicture(
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        try {
                            val buffer = image.planes[0].buffer
                            val bytes = ByteArray(buffer.remaining())
                            buffer.get(bytes)
                            image.close()
                            continuation.resume(bytes)
                        } catch (e: Exception) {
                            image.close()
                            continuation.resume(null)
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        exception.printStackTrace()
                        continuation.resume(null)
                    }
                }
            )
        }
}