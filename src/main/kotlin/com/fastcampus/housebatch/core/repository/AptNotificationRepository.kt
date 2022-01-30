package com.fastcampus.housebatch.core.repository

import com.fastcampus.housebatch.core.entity.AptNotification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface AptNotificationRepository: JpaRepository<AptNotification, Long> {
    fun findByEnabledIsTrue(pageable: Pageable): Page<AptNotification>
}