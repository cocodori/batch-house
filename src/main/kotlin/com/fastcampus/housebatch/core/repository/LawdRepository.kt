package com.fastcampus.housebatch.core.repository

import com.fastcampus.housebatch.core.entity.Lawd
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LawdRepository: JpaRepository<Lawd, Long> {
     fun findByLawdCd(lawdCd: String): Lawd?

     @Query(
          "SELECT DISTINCT SUBSTRING(l.lawdCd, 1, 5) " +
                  "FROM Lawd l " +
                  "WHERE l.exist = 1 AND l.lawdCd NOT LIKE '%00000000'")
     fun findDistinctGuLawdCd(): List<String>
}