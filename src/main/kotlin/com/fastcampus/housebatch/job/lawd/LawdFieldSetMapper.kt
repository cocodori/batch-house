package com.fastcampus.housebatch.job.lawd

import com.fastcampus.housebatch.core.entity.Lawd
import org.springframework.batch.item.file.mapping.FieldSetMapper
import org.springframework.batch.item.file.transform.FieldSet

const val LAWD_CD = "lawdCd"
const val LAWD_DONG = "lawdDong"
const val EXIST = "exist"
const val EXIST_TRUE = "존재"

class LawdFieldSetMapper: FieldSetMapper<Lawd> {
    override fun mapFieldSet(fieldSet: FieldSet): Lawd {
        return Lawd(
            fieldSet .readString(LAWD_CD),
            fieldSet.readString(LAWD_DONG),
            fieldSet.readBoolean(EXIST, EXIST_TRUE)
        )
    }
}