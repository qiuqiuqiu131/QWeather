package com.qiuqiuqiu.weatherPredicate

import android.content.Context
import com.qiuqiuqiu.weatherPredicate.manager.ILocalDataManager
import com.qiuqiuqiu.weatherPredicate.manager.ILocationWeatherManager
import com.qiuqiuqiu.weatherPredicate.manager.ISearchCityManager
import com.qiuqiuqiu.weatherPredicate.manager.LocalDataManager
import com.qiuqiuqiu.weatherPredicate.manager.LocationWeatherManager
import com.qiuqiuqiu.weatherPredicate.manager.SearchCityManager
import com.qiuqiuqiu.weatherPredicate.service.ILocationService
import com.qiuqiuqiu.weatherPredicate.service.IQWeatherService
import com.qiuqiuqiu.weatherPredicate.service.LocationService
import com.qiuqiuqiu.weatherPredicate.service.QWeatherService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindLocationWeatherManager(impl: LocationWeatherManager): ILocationWeatherManager

    @Binds
    @Singleton
    abstract fun bindSearchCityManager(impl: SearchCityManager): ISearchCityManager

    @Binds
    @Singleton
    abstract fun bindLocalDataManager(impl: LocalDataManager): ILocalDataManager

    @Binds
    @Singleton
    abstract fun bindLocationService(impl: LocationService): ILocationService

    @Binds
    @Singleton
    abstract fun bindQWeatherService(impl: QWeatherService): IQWeatherService
}

@Module
@InstallIn(SingletonComponent::class)
object ContextModule {
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }
}

