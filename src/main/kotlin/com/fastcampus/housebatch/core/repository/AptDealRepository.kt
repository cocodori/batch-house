package com.fastcampus.housebatch.core.repository

import com.fastcampus.housebatch.core.entity.Apt
import com.fastcampus.housebatch.core.entity.AptDeal
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface AptDealRepository: JpaRepository<AptDeal, Long> {
    fun findAptDealByAptAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
        apt: Apt,
        exclusiveArea: Double,
        dealDate: LocalDate,
        dealAmount: Long,
        floor: Int
    ): AptDeal?

    @Query("SELECT ad from AptDeal ad JOIN FETCH ad.apt WHERE ad.dealCanceled = 0 AND ad.dealDate =:dealDate")
    fun findByDealCanceledIsFalseAndDealDateEquals(dealDate: LocalDate): List<AptDeal>
}