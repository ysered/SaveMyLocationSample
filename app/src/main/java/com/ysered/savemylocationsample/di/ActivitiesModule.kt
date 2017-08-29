package com.ysered.savemylocationsample.di

import com.ysered.savemylocationsample.MapActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivitiesModule {
    @ContributesAndroidInjector
    abstract fun contributeMapActivity(): MapActivity
}
