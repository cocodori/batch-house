package com.fastcampus.housebatch.core.entity

import com.fastcampus.housebatch.core.dto.AptDealDto
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

@EntityListeners(AuditingEntityListener::class)
@Table(name = "apt_deal")
@Entity
class AptDeal(

    @Column(nullable = false)
    var exclusiveArea: Double,

    @Column(nullable = false)
    var dealDate: LocalDate,

    @Column(nullable = false)
    var dealAmount: Long,

    @Column(nullable = false)
    var floor: Int,

    @Column(nullable = false)
    var dealCanceled: Boolean = false,

    var dealCanceledDate: LocalDate? = null,

    @ManyToOne
    @JoinColumn(name = "apt_id")
    var apt: Apt,

    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     var aptDealId: Long? = null
) {
    companion object {
        fun of(dto: AptDealDto, apt: Apt) = AptDeal(
            dto.exclusiveArea!!,
            dto.getDealDate(),
            dto.getDealAmountReplace(),
            dto.floor!!,
            dto.isDealCanceled(),
            dto.dealCanceledDateStrToLocalDate(),
            apt
        )
    }
}