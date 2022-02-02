package com.fastcampus.housebatch.job.notify

import com.fastcampus.housebatch.adapter.FakeSendService
import com.fastcampus.housebatch.core.dto.AptDto
import com.fastcampus.housebatch.core.entity.AptDeal
import com.fastcampus.housebatch.core.entity.AptNotification
import com.fastcampus.housebatch.core.entity.Lawd
import com.fastcampus.housebatch.core.repository.AptNotificationRepository
import com.fastcampus.housebatch.core.repository.LawdRepository
import com.fastcampus.housebatch.core.service.AptDealService
import com.fastcampus.housebatch.job.BatchTestConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.JobParameters
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest
@SpringBatchTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@ContextConfiguration(classes = [AptNotificationJobConfig::class, BatchTestConfig::class])
@EnableJpaRepositories(basePackageClasses = [AptNotificationRepository::class] )
@EntityScan(basePackageClasses = [AptDeal::class])
internal class AptNotificationJobConfigTest {
    @Autowired
    lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    lateinit var aptNotificationRepository: AptNotificationRepository

    @MockBean
    lateinit var aptDealService: AptDealService

    @MockBean
    lateinit var lawdRepository: LawdRepository

    @MockBean
    lateinit var fakeSendService: FakeSendService

    @BeforeEach
    fun tearDown() {
        aptNotificationRepository.deleteAll()
    }

    @Test
    fun success() {
        //given
        val dealDate = LocalDate.now().minusDays(1)
        givenAptNotification()
        givenLawdCd()
        givenAptDeal()

        //when
        val jobParameters = JobParameters(mapOf("dealDate" to JobParameter(dealDate.toString())))
        val execution = jobLauncherTestUtils.launchJob(jobParameters)

        //then
        assertEquals(ExitStatus.COMPLETED, execution.exitStatus)
        verify(fakeSendService, times(1)).send(eq("abc@gmail.com"), any())
    }

    private fun givenAptDeal() {
        Mockito.`when`(aptDealService.findByGuLawdCdAndDealDate("11110", LocalDate.now().minusDays(1)))
            .thenReturn(listOf(
                AptDto("TT아파트", 200000000),
                AptDto("WW아파트", 300000000)
            ))
    }

    private fun givenAptNotification() {
        val aptNotification = AptNotification(
            email = "abc@gmail.com",
            guLawdCd = "11110",
            enabled = true
        )
        aptNotificationRepository.save(aptNotification)
    }

    private fun givenLawdCd() {
        val lawd = Lawd(
            lawdCd = "1111000000",
            lawdDong = "경기도 성남시 분당구",
            exist = true,
            lawdId = 1
        )
        Mockito.`when`(lawdRepository.findByLawdCd("1111000000"))
            .thenReturn(lawd)
    }

}