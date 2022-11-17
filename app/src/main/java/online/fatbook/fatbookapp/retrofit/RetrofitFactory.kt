package online.fatbook.fatbookapp.retrofit

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {
    private const val ADDRESS_API_SERVICE = "https://api.fatbook.online/"
//    private const val ADDRESS = "http://10.0.2.2:8080/"
    //    private const val ADDRESS = "http://192.168.0.121:8080/"

//    private const val ADDRESS_IMG_SERVICE = "https://img.fatbook.online/"
    private const val ADDRESS_IMG_SERVICE = "http://10.0.2.2:8080/"

    // api.fatbook.online
    private var TOKEN_API_SERVICE =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb2JpbGV1enZlcjEzMzkiLCJyb2xlcyI6WyJVU0VSIl0sImlzcyI6Imh0dHA6Ly9hcGkuZmF0Ym9vay5vbmxpbmUvYXBpL2xvZ2luIiwiZXhwIjoxNjcwNDMyMTM1fQ.Bd9S0RwtHfyBbKW2LjkjNNNNOkP0tD3HSDtTCa71HT8"

//    expired token:
//    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb2JpbGV1enZlcjEzMzkiLCJyb2xlcyI6WyJVU0VSIl0sImlzcyI6Imh0dHA6Ly9hcGkuZmF0Ym9vay5vbmxpbmUvYXBpL2xvZ2luIiwiZXhwIjoxNjY3ODI4MDY0fQ.u77VB0QZ8n6daBDJDpAkURV5esN4ACLM86yKBR5yYNw"

    // localhost
//    private var TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb2JpbGV1enZlcjEzMzkiLCJyb2xlcyI6WyJVU0VSIl0sImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9zaWduaW4iLCJleHAiOjE2NjUyMTk1NDV9.KE12GCSebyzLG2pR8b4JreNwVNwb_QI86Kdc1A6y6No"

    private var TOKEN_IMG_SERVICE =
            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqd3R1c2VyIiwiZXhwIjoxNzAwMjE4NDMxLCJpYXQiOjE2Njg2ODI0MzF9.H6s_gtnBVpDu9Kw7LlBmcHlElHcxpV8akEDJJq--jlU"

    private val gson = GsonBuilder()
            .setLenient()
            .create()

    private val gson1 = GsonBuilder()
            .setLenient()
            .create()

    private var clientApi = OkHttpClient.Builder()
            .addInterceptor(OAuthInterceptor("Bearer", TOKEN_API_SERVICE))
            .build()

    private var clientImg = OkHttpClient.Builder()
            .addInterceptor(OAuthInterceptor("Bearer", TOKEN_IMG_SERVICE))
            .build()

    fun apiService(): NetworkInfoService {
        return Retrofit.Builder()
                .baseUrl(ADDRESS_API_SERVICE)
                .client(clientApi)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(NetworkInfoService::class.java)
    }

    fun imgService(): NetworkInfoService {
        return Retrofit.Builder()
                .baseUrl(ADDRESS_IMG_SERVICE)
                .client(clientImg)
                .addConverterFactory(GsonConverterFactory.create(gson1))
                .build()
                .create(NetworkInfoService::class.java)
    }

    fun updateJWT(token: String) {
        TOKEN_API_SERVICE = token
        clientApi = OkHttpClient.Builder()
                .addInterceptor(OAuthInterceptor("Bearer", TOKEN_API_SERVICE))
                .build()
    }

}