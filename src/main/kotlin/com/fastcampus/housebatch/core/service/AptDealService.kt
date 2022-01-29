package com.fastcampus.housebatch.core.service

import com.fastcampus.housebatch.core.dto.AptDealDto
import com.fastcampus.housebatch.core.entity.Apt
import com.fastcampus.housebatch.core.entity.AptDeal
import com.fastcampus.housebatch.core.repository.AptDealRepository
import com.fastcampus.housebatch.core.repository.AptRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * AptDealDto에 있는 값을, Apt, AptDeal 엔티티로 저장한다.
 */
@Service
class AptDealService(
    private val aptRepository: AptRepository,
    private val aptDealRepository: AptDealRepository
) {
    @Transactional
    fun upsert(dto: AptDealDto) {
        val apt = getAptOrNew(dto)
        saveAptDeal(apt, dto)
    }

    private fun saveAptDeal(
        apt: Apt,
        dto: AptDealDto
    ) {
        val aptDeal =
            aptDealRepository.findAptDealByAptAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
                apt, dto.exclusiveArea!!, dto.getDealDate(), dto.getDealAmountReplace(), dto.floor!!
            ) ?: AptDeal.of(dto, apt)
        aptDeal.apt = apt
        aptDeal.dealCanceled = dto.isDealCanceled()
        aptDeal.dealCanceledDate = dto.dealCanceledDateStrToLocalDate()
        aptDealRepository.save(aptDeal)
    }

    private fun getAptOrNew(dto: AptDealDto): Apt {
        val apt = aptRepository.findAptByAptNameAndJibun(dto.aptName!!, dto.jibun!!)
            ?: Apt.of(dto)
        aptRepository.save(apt)
        return apt
    }
}