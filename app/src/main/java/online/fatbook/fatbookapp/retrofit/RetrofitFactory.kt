package online.fatbook.fatbookapp.retrofit

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {
    private const val HTTP = "http://"
    private const val ADDRESS = "10.0.2.2:1339/"

    //    private static final String ADDRESS = "192.168.0.120:8080";
    private const val ENDPOINT = "api/"
    private const val URL_ENDPOINT = HTTP + ADDRESS + ENDPOINT
    const val URL = HTTP + ADDRESS
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    fun apiServiceClient(): NetworkInfoService {
        return Retrofit.Builder()
            .baseUrl(URL_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(NetworkInfoService::class.java)
    }
}