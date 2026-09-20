package com.af.flashlight.data.sources

import com.af.flashlight.R
import com.af.flashlight.data.model.BenefitModel
import com.af.flashlight.data.model.Language
import com.af.flashlight.data.model.OnBoarding
import com.af.flashlight.data.model.PurchaseModel
import com.af.flashlight.utils.Constant
import javax.inject.Inject

class LocalDataSource @Inject constructor() {
    fun getListLanguage(): List<Language> {
        return listOf(
            Language("en", R.string.english),
            Language("es", R.string.spanish),
            Language("hi", R.string.hindi),
            Language("ko", R.string.korean),
            Language("ja", R.string.japanese),
            Language("de", R.string.german),
            Language("pt", R.string.portuguese),
            Language("fr", R.string.french),
            Language("it", R.string.italian),
            Language("in", R.string.indonesian),
            Language("ru", R.string.russian),
            Language("tr", R.string.turkish),
            Language("zh-TW", R.string.chinese_traditional),
            Language("ar", R.string.arabic),
            Language("vi", R.string.vietnamese),
            Language("th", R.string.thai),
            Language("fil", R.string.filipino),
            Language("ms", R.string.malay),
            Language("pl", R.string.polish),
            Language("bn", R.string.bengali),
            Language("ta", R.string.tamil),
            Language("te", R.string.telugu),
            Language("ro", R.string.romanian),
            Language("cs", R.string.czech),
            Language("el", R.string.greek)
        )
    }

    fun getListOnBoarding(): List<OnBoarding> {
        return listOf(
            OnBoarding(R.mipmap.bg_onboarding_1, R.string.title_onboarding_1, R.string.des_onboarding_1),
            OnBoarding(R.mipmap.bg_onboarding_2, R.string.title_onboarding_2, R.string.des_onboarding_2),
            OnBoarding(R.mipmap.bg_onboarding_3, R.string.title_onboarding_3, R.string.des_onboarding_3),
            OnBoarding(R.mipmap.bg_onboarding_4, R.string.title_onboarding_4, R.string.des_onboarding_4)
        )
    }

    fun getListBenefit(): List<BenefitModel> {
        return listOf(
            BenefitModel(R.mipmap.ic_bnf_1, R.mipmap.ic_bnf_1_large, R.string.unlimited_viewing),
            BenefitModel(R.mipmap.ic_bnf_4, R.mipmap.ic_bnf_4_large, R.string.vip_dramas),
            BenefitModel(R.mipmap.ic_bnf_2, R.mipmap.ic_bnf_2_large, R.string.hd_1080),
            BenefitModel(R.mipmap.ic_bnf_3, R.mipmap.ic_bnf_3_large, R.string.ad_free)
        )
    }

    fun getListPurchase(): List<PurchaseModel> {
        return listOf(
            PurchaseModel(
                Constant.MONTHLY_IAP,
                R.string.monthly,
                R.string.monthly_des,
                R.string.most_popular,
                isMostPopular = true,
                isBestValue = false,
                price = "$7.99",
                R.string.month,
                isSelected = true
            ),
            PurchaseModel(
                Constant.WEEKLY_IAP,
                R.string.weekly,
                R.string.weekly_des,
                R.string.weekly,
                isMostPopular = false,
                isBestValue = false,
                price = "$2.99",
                R.string.week,
                isSelected = false
            ),
            PurchaseModel(
                Constant.YEARLY_IAP,
                R.string.yearly,
                R.string.yearly_des,
                R.string.best_value,
                isMostPopular = false,
                isBestValue = true,
                price = "$39.99",
                R.string.year,
                isSelected = false
            )
        )
    }

}
