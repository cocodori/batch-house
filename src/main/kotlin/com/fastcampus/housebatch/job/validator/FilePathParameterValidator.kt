package com.fastcampus.housebatch.job.validator

import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.JobParametersValidator
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.util.StringUtils

class FilePathParameterValidator: JobParametersValidator {

    override fun validate(parameters: JobParameters?) {
        val filePath = parameters?.getString("filePath")
        if (!StringUtils.hasText(filePath)) {
            throw JobParametersInvalidException("filePath가 빈 문자열이거나 존재하지 않습니다.")
        }

        val resource: Resource = ClassPathResource(filePath!!)
        if (!resource.exists())
            throw JobParametersInvalidException("파일이 경로에 존재하지 않습니다.")
    }
}