package online.fatbook.fatbookapp.network.service

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import online.fatbook.fatbookapp.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {
    private const val ADDRESS_IMG_SERVICE = "https://img.fatbook.online/"
    //    private const val ADDRESS_IMG_SERVICE = "http://10.0.2.2:8080/"

    private var TOKEN_IMG_SERVICE =
            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqd3R1c2VyIiwiZXhwIjoxNzAwMjE4NDMxLCJpYXQiOjE2Njg2ODI0MzF9.H6s_gtnBVpDu9Kw7LlBmcHlElHcxpV8akEDJJq--jlU"

    private const val URL_API_SERVICE = BuildConfig.API_SERVICE_URL
    private var TOKEN_API_SERVICE = BuildConfig.API_SERVICE_TOKEN

    private const val URL_CDN_SERVICE = "https://storage.bunnycdn.com/fatbook/"
    private const val TOKEN_CDN_SERVICE = "0489e116-274b-4ef3-becd03432c07-d848-416c"

    private val gson = GsonBuilder()
            .setLenient()
            .create()

    private var clientApi = OkHttpClient.Builder()
            .addInterceptor(OAuthInterceptor("Bearer", TOKEN_API_SERVICE, ""))
            .build()

    private var clientImg = OkHttpClient.Builder()
            .addInterceptor(OAuthInterceptor("Bearer", TOKEN_IMG_SERVICE, ""))
            .build()

    private var cdnClient = OkHttpClient.Builder()
            .addInterceptor(OAuthInterceptorCDN(TOKEN_CDN_SERVICE))
            .build()

    fun apiService(): NetworkInfoService {
        return Retrofit.Builder()
                .baseUrl(URL_API_SERVICE)
                .client(clientApi)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(NetworkInfoService::class.java)
    }

    fun imgService(): NetworkInfoService {
        return Retrofit.Builder()
                .baseUrl(ADDRESS_IMG_SERVICE)
                .client(clientImg)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(NetworkInfoService::class.java)
    }

    fun cdnService(): NetworkInfoService {
        return Retrofit.Builder()
                .baseUrl(URL_CDN_SERVICE)
                .client(cdnClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(NetworkInfoService::class.java)
    }

    fun updateJWT(token: String, username: String) {
        TOKEN_API_SERVICE = token
        clientApi = OkHttpClient.Builder()
                .addInterceptor(OAuthInterceptor("Bearer", TOKEN_API_SERVICE, username))
                .build()
    }

}