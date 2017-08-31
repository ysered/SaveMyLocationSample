package com.ysered.savemylocationsample.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.ysered.savemylocationsample.MapViewModel
import dagger.Subcomponent
import javax.inject.Inject
import javax.inject.Singleton

@Subcomponent
interface ViewModelSubComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): ViewModelSubComponent
    }
    val mapViewModel: MapViewModel
}

@Singleton
class ViewModelFactory @Inject constructor(private val viewModelSubComponent: ViewModelSubComponent)
    : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == MapViewModel::class.java) {
            viewModelSubComponent.mapViewModel as T
        } else {
            throw RuntimeException("Unknown model class: ${modelClass.simpleName}")
        }
    }
}
