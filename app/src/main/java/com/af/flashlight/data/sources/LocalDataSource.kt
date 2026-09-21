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
            Language("en", R.string.english, R.mipmap.ic_flag_us),
            Language("es", R.string.spanish, R.mipmap.ic_flag_es),
            Language("hi", R.string.hindi, R.mipmap.ic_flag_in),
            Language("ko", R.string.korean, R.mipmap.ic_flag_kr),
            Language("ja", R.string.japanese, R.mipmap.ic_flag_jp),
            Language("de", R.string.german, R.mipmap.ic_flag_de),
            Language("pt", R.string.portuguese, R.mipmap.ic_flag_pt),
            Language("fr", R.string.french, R.mipmap.ic_flag_fr),
            Language("it", R.string.italian, R.mipmap.ic_flag_it),
            Language("in", R.string.indonesian, R.mipmap.ic_flag_id),
            Language("vi", R.string.vietnamese, R.mipmap.ic_flag_vn),
            Language("ru", R.string.russian, R.mipmap.ic_flag_ru),
            Language("tr", R.string.turkish, R.mipmap.ic_flag_tr),
            Language("zh-TW", R.string.chinese, R.mipmap.ic_flag_cn)
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
