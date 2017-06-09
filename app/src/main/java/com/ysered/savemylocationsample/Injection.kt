package com.ysered.savemylocationsample

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.persistence.room.RoomDatabase
import android.location.Geocoder
import dagger.*
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Subcomponent
interface ViewModelSubComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): ViewModelSubComponent
    }

    fun mapViewModel(): MapViewModel
}

@Module(subcomponents = arrayOf(ViewModelSubComponent::class))
class AppModule {
    @Singleton @Provides
    fun providesGeoCoder(application: Application): Geocoder
            = Geocoder(application.applicationContext, Locale.getDefault())

    @Singleton @Provides
    fun providesLocationUpdatesLiveDate(application: Application): LocationUpdatesLiveData
            = LocationUpdatesLiveData(application.applicationContext)

    @Singleton @Provides
    fun providesDatabase(application: Application): RoomDatabase.Builder<AppDatabase>
            = AppDatabase.get(application.applicationContext)

    @Singleton @Provides
    fun provideViewModelFactory(viewModelSubComponentBuilder: ViewModelSubComponent.Builder)
            : ViewModelProvider.Factory
            = MyAppViewModelFactory(viewModelSubComponentBuilder.build())
}

@Singleton
private class MyAppViewModelFactory
@Inject constructor(private val viewModelSubComponent: ViewModelSubComponent)
    : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == MapViewModel::class.java) {
            viewModelSubComponent.mapViewModel() as T
        } else {
            throw RuntimeException("Unknown model class: ${modelClass.simpleName}")
        }
    }
}

@Singleton
@Component(modules = arrayOf(
        AndroidInjectionModule::class,
        AppModule::class,
        MapActivityModule::class
))
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
    fun inject(myApp: MyApp)
}

@Module
abstract class MapActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeMapActivity(): MapActivity
}