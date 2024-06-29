package ru.surf.learn2invest.noui.dependency_injection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.surf.learn2invest.noui.network_components.util.Const
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkDIModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder().baseUrl(Const.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()
        ).build()


}