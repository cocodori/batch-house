package com.fastcampus.housebatch.job.validator

import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.JobParametersValidator
import org.springframework.util.StringUtils

const val LAWD_CD = "lawdCd"

class LawdCdParameterValidator: JobParametersValidator {
    override fun validate(parameters: JobParameters?) {
        val lawdCd = parameters?.getString(LAWD_CD)

        if (isInvalid(lawdCd))
            throw JobParametersInvalidException("${LAWD_CD}은 다섯 자리 문자여야 합니다.")
    }

    private fun isInvalid(lawdCd: String?) =
        !StringUtils.hasText(lawdCd) || lawdCd?.length != 5
}