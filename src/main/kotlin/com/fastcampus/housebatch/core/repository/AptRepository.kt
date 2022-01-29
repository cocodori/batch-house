package com.fastcampus.housebatch.core.repository

import com.fastcampus.housebatch.core.entity.Apt
import org.springframework.data.jpa.repository.JpaRepository

interface AptRepository: JpaRepository<Apt, Long> {
    fun findAptByAptNameAndJibun(aptName: String, jibnun: String): Apt?
}