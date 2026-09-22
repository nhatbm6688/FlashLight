package com.af.flashlight.component.flashalert.di

import com.af.flashlight.manager.FlashlightManager
import com.af.flashlight.utils.SpManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface FlashAlertEntryPoint {
    fun flashlightManager(): FlashlightManager
    fun spManager(): SpManager
}
