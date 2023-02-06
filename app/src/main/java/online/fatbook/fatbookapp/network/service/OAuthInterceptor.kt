package online.fatbook.fatbookapp.network.service

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class OAuthInterceptor(private val tokenType: String, private val accessToken: String, private val username: String) :
        Interceptor {

    companion object {
        private const val TAG = "OAuthInterceptor"
    }

//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//
//        val builder = request.newBuilder()
//        try {
//            builder.addHeader("username", username)
//        } catch (_: Exception) {}
//        builder.addHeader("Authorization", "$tokenType $accessToken").build()
//        return chain.proceed(builder.build())
//    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val builder = request.newBuilder()
        try {
            builder.addHeader("username", username)
        } catch (_: Exception) {}
        builder.addHeader("Authorization", "$tokenType $accessToken").build()
        val proceed = chain.proceed(builder.build())

        when {
            proceed.code == 403 -> {
                Log.d(TAG, "TOKEN = $accessToken")
                Log.d(TAG, "token expired")
                return proceed
            }
            else -> {
                Log.d(TAG, "token OK")
                return proceed
            }
        }
    }

//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//
//        val builder = request.newBuilder()
//        try {
//            builder.addHeader("app-name", AppInfo.appName)
//            builder.addHeader("app-version", AppInfo.appVersion)
//            builder.addHeader("device-type", AppInfo.deviceType)
//            builder.addHeader("device-id", AppInfo.deviceId)
//            builder.addHeader("os-version", AppInfo.osVersion)
//            builder.addHeader("os-type", AppInfo.osType)
//            builder.addHeader("accept-language", AppInfo.acceptLanguage)
//        } catch (e: Exception) {}
//        builder.addHeader("Authorization", "$tokenType $accessToken")
//        return chain.proceed(builder.build())
//    }

//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//
//        return if (!request.url.toString().contains(NetConfig.token_url) &&
//            NetConfig.apiKey.isNotEmpty()
//        ) {
//            val builder = request.newBuilder()
//            try {
//                builder.addHeader("app-name", AppInfo.appName)
//                builder.addHeader("app-version", AppInfo.appVersion)
//                builder.addHeader("device-type", AppInfo.deviceType)
//                builder.addHeader("device-id", AppInfo.deviceId)
//                builder.addHeader("os-version", AppInfo.osVersion)
//                builder.addHeader("os-type", AppInfo.osType)
//                builder.addHeader("accept-language", AppInfo.acceptLanguage)
//            } catch (e: Exception) {}
//            builder.addHeader("Authorization", "$tokenType $accessToken")
//            chain.proceed(builder.build())
//        } else {
//            chain.proceed(request)
//        }
//    }
}