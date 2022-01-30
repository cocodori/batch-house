package com.fastcampus.housebatch.core.dto

import java.text.DecimalFormat
import java.util.stream.Collectors

data class NotificationDto(
    var email: String,
    var guName: String,
    var count: Int,
    var aptDeals: List<AptDto>
) {
    fun toMessage(): String {
        val decimalFormat = DecimalFormat()
        val format = String.format(
            "%s 아파트 실거래가 알림\n" +
                    " 총 %d개 거래가 발생했습니다.\n", guName, count
        )
        val collect = aptDeals.stream()
            .map { String.format(" - %s : %s원\n", it.name, decimalFormat.format(it.price)) }
            .collect(Collectors.joining())
        return format + collect

    }
}