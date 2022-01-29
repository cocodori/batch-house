package com.fastcampus.housebatch.core.entity

import com.fastcampus.housebatch.core.dto.AptDealDto
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "apt")
@EntityListeners(AuditingEntityListener::class)
class Apt(
    @Column(nullable = false)
    var aptName: String,

    @Column(nullable = false)
    var jibun: String,

    @Column(nullable = false)
    var dong: String,

    @Column(nullable = false)
    var guLawdCd: String,

    @Column(nullable = false)
    var builtYear: Int,

    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var aptId: Long? = null
) {
    companion object {
        fun of(dto: AptDealDto) =
            Apt(
                dto.aptName?.trim()!!,
                dto.getJibunNotNull(),
                dto.dong?.trim()!!,
                dto.regionCode?.trim()!!,
                dto.builtYear!!
            )
    }
}