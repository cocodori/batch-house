package com.fastcampus.housebatch.job.apt

import com.fastcampus.housebatch.adapter.ApartmentApiResource
import com.fastcampus.housebatch.core.repository.LawdRepository
import com.fastcampus.housebatch.core.service.AptDealService
import com.fastcampus.housebatch.job.BatchTestConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBatchTest
@SpringBootTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@ContextConfiguration(classes = [AptDealInsertJobConfig::class, BatchTestConfig::class])
internal class AptDealInsertJobConfigTest {

    @Autowired
    lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @MockBean
    lateinit var aptDealService: AptDealService

    @MockBean
    lateinit var lawdRepository: LawdRepository

    @MockBean
    lateinit var apartmentApiResource: ApartmentApiResource

    @Test
    fun success() {
        //given
        Mockito.`when`(lawdRepository.findDistinctGuLawdCd()).thenReturn(listOf("41135", "41136"))
        Mockito.`when`(apartmentApiResource.getResource(any(), any())).thenReturn(ClassPathResource("test-api-response.xml"))

        //when
        val jobParameters = JobParameters(mapOf("yearMonth" to JobParameter("2021-07")))
        val execution = jobLauncherTestUtils.launchJob(jobParameters)

        //then
        Assertions.assertEquals(ExitStatus.COMPLETED, execution.exitStatus)
        verify(aptDealService, times(6)).upsert(any())

    }

    @Test
    fun fail_whenYearMonthNotExist() {
        //given
        Mockito.`when`(lawdRepository.findDistinctGuLawdCd()).thenReturn(listOf("41135"))
        Mockito.`when`(apartmentApiResource.getResource(any(), any())).thenReturn(ClassPathResource("test-api-response.xml"))

        //when
        //then
        Assertions.assertThrows(JobParametersInvalidException::class.java) { jobLauncherTestUtils.launchJob() }
        verify(aptDealService, never()).upsert(any())



    }
}