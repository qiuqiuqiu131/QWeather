package com.qiuqiuqiu.weatherPredicate.service
import javax.inject.Inject
import com.qiuqiuqiu.weatherPredicate.BuildConfig
import android.content.Context
import com.qiuqiuqiu.weatherPredicate.network.TianApiCities
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
interface ApiKeyProvider {
    val key: String
}

class BuildConfigApiKeyProvider @Inject constructor() : ApiKeyProvider {
    override val key: String
        get() = BuildConfig.TIAN_API_KEY
}
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://apis.tianapi.com/"
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            // 对于 GET 请求，BASIC 已会打印请求行（含 URL 与 query）
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
            else HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTianApi(retrofit: Retrofit): TianApiCities {
        return retrofit.create(TianApiCities::class.java)
    }


    @Provides
    @Singleton
    fun provideApiKeyProvider(): ApiKeyProvider {
        return BuildConfigApiKeyProvider()
    }
}