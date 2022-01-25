package com.fastcampus.housebatch.core.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@EntityListeners(AuditingEntityListener::class)
@Table(name = "lawd")
@Entity
class Lawd(
    var lawdCd: String,

    var lawdDong: String,

    var exist: Boolean,

    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var lawdId: Long? = null,
) {
    //Test
    override fun toString(): String {
        return "Lawd(lawdCd='$lawdCd', lawdDong='$lawdDong', exist=$exist, createdAt=$createdAt, updatedAt=$updatedAt, lawdId=$lawdId)"
    }
}