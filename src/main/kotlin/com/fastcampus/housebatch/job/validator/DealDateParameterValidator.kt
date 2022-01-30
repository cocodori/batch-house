package com.fastcampus.housebatch.job.validator

import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.JobParametersValidator
import org.springframework.util.StringUtils
import java.time.LocalDate
import java.time.format.DateTimeParseException

class DealDateParameterValidator: JobParametersValidator {
    companion object {
        const val DEAL_DATE = "dealDate"
    }
    override fun validate(parameters: JobParameters?) {
        val dealDate = parameters?.getString(DEAL_DATE)
        if (!StringUtils.hasText(dealDate))
            throw JobParametersInvalidException("$DEAL_DATE 가 존재하지 않습니다.")

        try {
            LocalDate.parse(dealDate)
        } catch (e: DateTimeParseException) {
            throw JobParametersInvalidException("$DEAL_DATE 는 yyyy-MM-dd여야 합니다.")
        }
    }
}