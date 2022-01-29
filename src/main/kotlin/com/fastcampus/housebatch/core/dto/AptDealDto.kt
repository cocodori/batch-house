package com.fastcampus.housebatch.core.dto

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement


/**
 * 아파트 실거래가 API 각각의 거래 정보를 담는 객체
 */
@XmlRootElement(name = "item")
data class AptDealDto(
    @get:XmlElement(name = "거래금액")
    var dealAmount: String? = null,

    @get:XmlElement(name = "건축년도")
    var builtYear: Int? = null,

    @get:XmlElement(name = "년")
    var year: Int? = null,

    @get:XmlElement(name = "법정동")
    var dong: String? = null,

    @get:XmlElement(name = "아파트")
    var aptName: String? = null,

    @get:XmlElement(name = "월")
    var month: Int? = null,

    @get:XmlElement(name = "일")
    var day: Int? = null,

    @get:XmlElement(name = "전용면적")
    var exclusiveArea: Double? = null,

    @get:XmlElement(name = "지번")
    var jibun: String? = null,

    @get:XmlElement(name = "지역코드")
    var regionCode: String? = null,

    @get:XmlElement(name = "층")
    var floor: Int? = null,

    @get:XmlElement(name = "해제사유발생일")
    var dealCanceledDate: String? = null,

    @get:XmlElement(name = "해제여부")
    var dealCanceled: String? = null
) {
    fun getDealDate(): LocalDate =
        LocalDate.of(year!!, month!!, day!!)

    fun getDealAmountReplace() = dealAmount!!.replace(",".toRegex(), "").trim().toLong()

    fun isDealCanceled() =
        "O" == dealCanceled

    fun dealCanceledDateStrToLocalDate() = if (dealCanceledDate?.isBlank() == true) null
        else LocalDate.parse(dealCanceledDate?.trim(), DateTimeFormatter.ofPattern("yy.MM.dd"))

    fun getJibunNotNull() = jibun ?: ""
}