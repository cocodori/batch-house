package com.fastcampus.housebatch.adapter

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import java.net.MalformedURLException
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * 아파트 실거래가 API를 호출하기 위한 파라미터
 * 1. serviceKey - API 인증 키
 * 2. LAWD_CD - 법정동 코드 앞 다섯 자리(guLawdCd)
 * 3. DEAL_YMD - 거래가 발생한 년월
 */
@Component
class ApartmentApiResource(
    @Value("\${spring.external.apartment-api.path}")
    private val path: String,
    @Value("\${spring.external.apartment-api.service-key}")
    private val serviceKey: String
) {

    fun getResource(lawdCd: String, yearMonth: YearMonth): UrlResource {
        val url = String.format(
            "%s?serviceKey=%s&LAWD_CD=%s&DEAL_YMD=%s",
            path,
            serviceKey,
            lawdCd,
            yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM"))
        )

        println("[ApartmentApiResource] url: $url")

        return try {
            UrlResource(url)
        } catch (e: MalformedURLException) {
            throw IllegalArgumentException("Failed to crated UrlResource")
        }
    }

}