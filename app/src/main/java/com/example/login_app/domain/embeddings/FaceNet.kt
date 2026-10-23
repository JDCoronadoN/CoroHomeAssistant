package com.example.login_app.domain.embeddings

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class FaceNet(private val context: Context) {

    private val interpreter: Interpreter
    private val imgSize = 160 // Tamaño de entrada del modelo FaceNet estándar
    private val embeddingSize = 512 // Ajusta a 512 si tu modelo es FaceNet-512

    init {
        interpreter = Interpreter(loadModelFile())
    }

    suspend fun getFaceEmbedding(image: Bitmap): FloatArray = withContext(Dispatchers.Default) {
        val input = convertBitmapToBuffer(image)
        val output = Array(1) { FloatArray(embeddingSize) }

        interpreter.run(input, output)

        // Normalizar el vector a longitud 1 (L2 Normalization)
        return@withContext l2Normalize(output[0])
    }

    private fun loadModelFile(): ByteBuffer {
        val fileDescriptor = context.assets.openFd("facenet.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }

    private fun convertBitmapToBuffer(image: Bitmap): ByteBuffer {
        // 1. Redimensionar al tamaño esperado por el modelo (160x160)
        val resizedBitmap = image.scale(imgSize, imgSize, true)

        // 2. Crear el buffer de entrada: 1 (batch) * 160 * 160 * 3 (RGB) * 4 bytes (float)
        val inputBuffer = ByteBuffer.allocateDirect(1 * imgSize * imgSize * 3 * 4)
            .order(ByteOrder.nativeOrder())

        val pixels = IntArray(imgSize * imgSize)
        resizedBitmap.getPixels(pixels, 0, imgSize, 0, 0, imgSize, imgSize)

        for (pixel in pixels) {
            val r = ((pixel shr 16) and 0xFF).toFloat()
            val g = ((pixel shr 8) and 0xFF).toFloat()
            val b = (pixel and 0xFF).toFloat()

            // Normalización a [-1, 1] (típica de FaceNet)
            inputBuffer.putFloat((r - 127.5f) / 128.0f)
            inputBuffer.putFloat((g - 127.5f) / 128.0f)
            inputBuffer.putFloat((b - 127.5f) / 128.0f)
        }

        return inputBuffer
    }

    private fun l2Normalize(vector: FloatArray): FloatArray {
        var norm = 0f
        for (v in vector) norm += v * v
        norm = kotlin.math.sqrt(norm)
        if (norm == 0f) return vector
        return FloatArray(vector.size) { vector[it] / norm }
    }
}