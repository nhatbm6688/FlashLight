package com.af.flashlight.domain.usecase

import com.af.flashlight.data.model.BenefitModel
import com.af.flashlight.data.repositories.LocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetListBenefitUseCase @Inject constructor(private val localRepository: LocalRepository) :
    UseCase<GetListBenefitUseCase.Param, List<BenefitModel>>() {

    open class Param : UseCase.Param()

    override suspend fun execute(param: Param): List<BenefitModel> = withContext(Dispatchers.IO) {
        localRepository.getListBenefit()
    }
}
