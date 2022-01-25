package com.fastcampus.housebatch.core.service

import com.fastcampus.housebatch.core.entity.Lawd
import com.fastcampus.housebatch.core.repository.LawdRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LawdService(
    private val lawdRepository: LawdRepository
) {
    @Transactional
    fun upsert(lawd: Lawd) {
        //데이터가 존재하면 수정, 없으면 생성
        val saved = lawdRepository.findByLawdCd(lawd.lawdCd)
            ?: Lawd(lawd.lawdCd, lawd.lawdDong, lawd.exist)

        lawdRepository.save(saved)
    }
}