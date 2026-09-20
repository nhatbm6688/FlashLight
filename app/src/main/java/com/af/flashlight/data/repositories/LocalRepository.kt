package com.af.flashlight.data.repositories

import com.af.flashlight.data.model.BenefitModel
import com.af.flashlight.data.model.Language
import com.af.flashlight.data.model.OnBoarding
import com.af.flashlight.data.model.PurchaseModel
import com.af.flashlight.data.sources.LocalDataSource
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val localDataSource: LocalDataSource
) {

    fun getListLanguage(): List<Language> = localDataSource.getListLanguage()

    fun getListOnBoarding(): List<OnBoarding> = localDataSource.getListOnBoarding()

    fun getListBenefit(): List<BenefitModel> = localDataSource.getListBenefit()
    fun getListPurchase(): List<PurchaseModel> = localDataSource.getListPurchase()

}
