package ru.surf.learn2invest.network_components.util

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.surf.learn2invest.network_components.util.Const.BASE_URL

object CoinRetrofitClient {
    val client: Retrofit = getRetrofitClient()

    private fun getRetrofitClient(): Retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()
        )
        .build()
}

fun Float.round() =
    "\\S*\\.[0]*[0-9]{2}".toRegex().find(this.toBigDecimal().toPlainString())?.value