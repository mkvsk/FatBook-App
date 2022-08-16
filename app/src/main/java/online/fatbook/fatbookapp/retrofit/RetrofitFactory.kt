package online.fatbook.fatbookapp.retrofit

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {
    private const val ADDRESS = "https://api.fatbook.online/"
//    private const val ADDRESS = "http://10.0.2.2:8080/"
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    fun apiServiceClient(): NetworkInfoService {
        return Retrofit.Builder()
            .baseUrl(ADDRESS)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(NetworkInfoService::class.java)
    }
}