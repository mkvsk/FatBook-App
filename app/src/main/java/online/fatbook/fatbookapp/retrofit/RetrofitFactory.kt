package online.fatbook.fatbookapp.retrofit

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {
    private const val ADDRESS = "https://api.fatbook.online/"
//    private const val ADDRESS = "http://10.0.2.2:8080/"
    //    private const val ADDRESS = "http://192.168.0.121:8080/"

    // api.fatbook.online
    var TOKEN =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb2JpbGV1enZlcjEzMzkiLCJyb2xlcyI6WyJVU0VSIl0sImlzcyI6Imh0dHA6Ly9hcGkuZmF0Ym9vay5vbmxpbmUvYXBpL2xvZ2luIiwiZXhwIjoxNjcwNDMyMTM1fQ.Bd9S0RwtHfyBbKW2LjkjNNNNOkP0tD3HSDtTCa71HT8"

//    expired token:
//    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb2JpbGV1enZlcjEzMzkiLCJyb2xlcyI6WyJVU0VSIl0sImlzcyI6Imh0dHA6Ly9hcGkuZmF0Ym9vay5vbmxpbmUvYXBpL2xvZ2luIiwiZXhwIjoxNjY3ODI4MDY0fQ.u77VB0QZ8n6daBDJDpAkURV5esN4ACLM86yKBR5yYNw"

    // localhost
//    private var TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb2JpbGV1enZlcjEzMzkiLCJyb2xlcyI6WyJVU0VSIl0sImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9zaWduaW4iLCJleHAiOjE2NjUyMTk1NDV9.KE12GCSebyzLG2pR8b4JreNwVNwb_QI86Kdc1A6y6No"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private var client = OkHttpClient.Builder()
        .addInterceptor(OAuthInterceptor("Bearer", TOKEN))
        .build()

    fun apiServiceClient(): NetworkInfoService {
        return Retrofit.Builder()
            .baseUrl(ADDRESS)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(NetworkInfoService::class.java)
    }

    fun updateJWT(token: String) {
        TOKEN = token
        client = OkHttpClient.Builder()
            .addInterceptor(OAuthInterceptor("Bearer", TOKEN))
            .build()
    }

}