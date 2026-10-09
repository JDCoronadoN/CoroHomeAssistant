package com.example.login_app.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitClient {

    private const val BASE_URL = "https://API.corosolution.home/"

    // Interceptor para ver las peticiones HTTP en el Logcat
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Genera un OkHttpClient que ignora las validaciones estrictas de certificados SSL.
     * Esto resuelve el error "Trust anchor for certification path not found".
     */
    private fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            // TrustManager personalizado que no valida la cadena de certificados
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )

            // Configurar contexto SSL
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            val sslSocketFactory = sslContext.socketFactory

            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier(HostnameVerifier { _, _ -> true }) // Acepta cualquier hostname
                .build()
        } catch (e: Exception) {
            throw RuntimeException("Error al configurar OkHttpClient inseguro", e)
        }
    }

    val rostroApiService: RostroApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getUnsafeOkHttpClient()) // Usa el cliente que permite el certificado local
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RostroApiService::class.java)
    }
}