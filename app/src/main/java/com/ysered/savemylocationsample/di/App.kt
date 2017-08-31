package com.ysered.savemylocationsample.di

import android.app.Application
import android.arch.lifecycle.ViewModelProvider
import android.content.SharedPreferences
import android.location.Geocoder
import android.preference.PreferenceManager
import com.ysered.savemylocationsample.AddressResolver
import com.ysered.savemylocationsample.AddressResolverImpl
import com.ysered.savemylocationsample.LocationUpdatesLiveData
import com.ysered.savemylocationsample.MyApp
import com.ysered.savemylocationsample.database.AppDatabase
import com.ysered.savemylocationsample.database.MyLocationDao
import com.ysered.savemylocationsample.util.MapCameraPreferences
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import java.util.*
import javax.inject.Singleton


@Singleton
@Component(modules = arrayOf(
        AndroidInjectionModule::class,
        AppModule::class,
        ActivitiesModule::class
))
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun setApplication(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(myApp: MyApp)
}

@Module(subcomponents = arrayOf(ViewModelSubComponent::class))
class AppModule {
    @Singleton
    @Provides
    fun providesGeoCoder(application: Application): Geocoder
            = Geocoder(application.applicationContext, Locale.getDefault())

    @Singleton
    @Provides
    fun providesAddressResolver(geocoder: Geocoder): AddressResolver
            = AddressResolverImpl(geocoder)

    @Singleton
    @Provides
    fun providesLocationUpdatesLiveDate(application: Application): LocationUpdatesLiveData
            = LocationUpdatesLiveData(application.applicationContext)

    @Singleton
    @Provides
    fun providesDatabase(application: Application): AppDatabase
            = AppDatabase.create(application.applicationContext)

    @Singleton
    @Provides
    fun providesMyLocationDao(appDatabase: AppDatabase): MyLocationDao
            = appDatabase.myLocationDao

    @Singleton
    @Provides
    fun providesSharedPreferences(application: Application): SharedPreferences
            = PreferenceManager.getDefaultSharedPreferences(application.applicationContext)

    @Singleton
    @Provides
    fun providesCameraPrefs(sharedPreferences: SharedPreferences): MapCameraPreferences
            = MapCameraPreferences(sharedPreferences)

    @Singleton
    @Provides
    fun provideViewModelFactory(viewModelSubComponentBuilder: ViewModelSubComponent.Builder): ViewModelProvider.Factory
            = ViewModelFactory(viewModelSubComponentBuilder.build())
}
