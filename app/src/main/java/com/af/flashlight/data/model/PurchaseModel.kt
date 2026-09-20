package com.af.flashlight.data.model

data class PurchaseModel(
    val id:String,
    val pack: Int,
    val packDes: Int,
    val textStatus:Int,
    val isMostPopular: Boolean,
    val isBestValue: Boolean,
    var price: String,
    var period: Int,
    var isSelected: Boolean = false
)
