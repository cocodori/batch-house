package com.fastcampus.housebatch.core.repository

import com.fastcampus.housebatch.core.entity.Lawd
import org.springframework.data.jpa.repository.JpaRepository

interface LawdRepository: JpaRepository<Lawd, Long> {
     fun findByLawdCd(lawdCd: String): Lawd?
}