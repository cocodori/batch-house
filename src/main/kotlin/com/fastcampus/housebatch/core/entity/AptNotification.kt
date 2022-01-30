package com.fastcampus.housebatch.core.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@EntityListeners(AuditingEntityListener::class)
@Table(name="apt_notification")
@Entity
class AptNotification(

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var guLawdCd: String,

    @Column(nullable = false)
    var enabled: Boolean,

    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var aptNotificationId: Long
)