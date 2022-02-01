package com.fastcampus.housebatch.job.lawd

import com.fastcampus.housebatch.core.service.LawdService
import com.fastcampus.housebatch.job.BatchTestConfig
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.kotlin.any
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ContextConfiguration(classes = [LawdInsertJobConfig::class, BatchTestConfig::class])
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@SpringBatchTest
@SpringBootTest
internal class LawdInsertJobConfigTest {
    @Autowired
    lateinit var jobLauncherTestUtils: JobLauncherTestUtils
    @MockBean
    lateinit var lawdService: LawdService

    @Test
    fun success() {
        //whem
        val jobParameters = JobParameters(mapOf("filePath" to JobParameter("TEST_LAWD_CODE.txt")))
        val execution = jobLauncherTestUtils.launchJob(jobParameters)

        //then
        assertEquals(ExitStatus.COMPLETED, execution.exitStatus)
        Mockito.verify(lawdService, times(3)).upsert(any())
    }

    @Test
    fun failed_whenFileNotFound() {
        //when, then
        val jobParameters = JobParameters(mapOf("filePath" to JobParameter("NOT_EXIST_FILE.txt")))

        assertThrows(JobParametersInvalidException::class.java) { jobLauncherTestUtils.launchJob(jobParameters) }
        verify(lawdService, never()).upsert(any())


    }
}