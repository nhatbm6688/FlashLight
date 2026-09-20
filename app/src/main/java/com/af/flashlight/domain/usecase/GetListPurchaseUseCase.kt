package com.af.flashlight.domain.usecase

import com.af.flashlight.data.model.PurchaseModel
import com.af.flashlight.data.repositories.LocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetListPurchaseUseCase @Inject constructor(private val localRepository: LocalRepository) :
    UseCase<GetListPurchaseUseCase.Param, List<PurchaseModel>>() {

    open class Param : UseCase.Param()

    override suspend fun execute(param: Param): List<PurchaseModel> = withContext(Dispatchers.IO) {
        localRepository.getListPurchase()
    }
}
