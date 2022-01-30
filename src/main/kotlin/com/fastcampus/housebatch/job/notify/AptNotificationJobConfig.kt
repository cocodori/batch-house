package com.fastcampus.housebatch.job.notify

import com.fastcampus.housebatch.core.dto.NotificationDto
import com.fastcampus.housebatch.core.entity.AptNotification
import com.fastcampus.housebatch.core.repository.AptNotificationRepository
import com.fastcampus.housebatch.core.repository.LawdRepository
import com.fastcampus.housebatch.core.service.AptDealService
import com.fastcampus.housebatch.job.validator.DealDateParameterValidator
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.data.RepositoryItemReader
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort
import java.lang.RuntimeException
import java.time.LocalDate
import java.util.*

@Configuration
class AptNotificationJobConfig(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
) {
    @Bean
    fun aptNotificationJob(
        aptNotificationStep: Step
    ): Job =
        jobBuilderFactory["aptNotificationJob"]
            .incrementer(RunIdIncrementer())
            .validator(DealDateParameterValidator() )
            .start(aptNotificationStep)
            .build()

    @JobScope
    @Bean
    fun aptNotificationStep(
        aptNotificationRepositoryItemReader: RepositoryItemReader<AptNotification>,
        aptNotificationProcessor: ItemProcessor<AptNotification, NotificationDto>,
        aptNotificationWriter: ItemWriter<NotificationDto>
    ): Step =
        stepBuilderFactory["aptNotificationStep"]
            .chunk<AptNotification, NotificationDto>(10)
            .reader(aptNotificationRepositoryItemReader)
            .processor(aptNotificationProcessor)
            .writer(aptNotificationWriter)
            .build()

    @StepScope
    @Bean
    fun aptNotificationRepositoryItemReader(
        aptNotificationRepository: AptNotificationRepository
    ): RepositoryItemReader<AptNotification> {
        return RepositoryItemReaderBuilder<AptNotification>()
            .name("aptNotificationRepositoryItemReader")
            .repository(aptNotificationRepository)
            .methodName("findByEnabledIsTrue")
            .pageSize(10)
            .sorts(Collections.singletonMap("aptNotificationId", Sort.Direction.DESC))
            .build()
    }

    @StepScope
    @Bean
    fun aptNotificationProcessor(
        @Value("#{jobParameters['dealDate']}")
        dealDate: String,
        lawdRepository: LawdRepository,
        aptDealService: AptDealService
    ): ItemProcessor<AptNotification, NotificationDto> =
        ItemProcessor { aptNotification ->
            val aptDtos =
                aptDealService.findByGuLawdCdAndDealDate(aptNotification.guLawdCd, LocalDate.parse(dealDate))

            if (aptDtos.isEmpty())
                return@ItemProcessor null

            val guName = (lawdRepository.findByLawdCd(aptNotification.guLawdCd + "00000")
                ?: throw RuntimeException("Not Found LawdCd"))
                .lawdDong

            NotificationDto(
                aptNotification.email,
                guName,
                count = aptDtos.size,
                aptDtos
            )
        }

    @StepScope
    @Bean
    fun aptNotificationWriter(): ItemWriter<NotificationDto> =
        ItemWriter {
            it.forEach { println(it.toMessage()) }
        }

}