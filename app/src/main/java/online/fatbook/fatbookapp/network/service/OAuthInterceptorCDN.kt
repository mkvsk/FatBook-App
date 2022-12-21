package online.fatbook.fatbookapp.network.service

import okhttp3.Interceptor
import okhttp3.Response

class OAuthInterceptorCDN(private val apiKey: String) :
        Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        builder.addHeader("AccessKey", apiKey)
        builder.addHeader("Content-Type", "application/octet-stream")
        return chain.proceed(builder.build())
    }
}