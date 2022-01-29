package com.fastcampus.housebatch.job.apt

import com.fastcampus.housebatch.adapter.ApartmentApiResource
import com.fastcampus.housebatch.core.dto.AptDealDto
import com.fastcampus.housebatch.core.repository.LawdRepository
import com.fastcampus.housebatch.core.service.AptDealService
import com.fastcampus.housebatch.job.validator.FilePathParameterValidator
import com.fastcampus.housebatch.job.validator.LawdCdParameterValidator
import com.fastcampus.housebatch.job.validator.YearMonthParameterValidator
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersValidator
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.CompositeJobParametersValidator
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.xml.StaxEventItemReader
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import java.time.YearMonth

@Configuration
class AptDealInsertJobConfig(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val apartmentApiResource: ApartmentApiResource,
) {
    @Bean
    fun aptDealInsertJob(
        aptDealInsertStep: Step,
        guLawdCdStep: Step,
    ): Job {
        return jobBuilderFactory["aptDealInsertJob"]
            .incrementer(RunIdIncrementer())
            .validator(aptDealJobParametersValidator())
            .start(guLawdCdStep)
            .on("CONTINUABLE").to(aptDealInsertStep).next(guLawdCdStep)
            .from(guLawdCdStep)
            .on("*").end()
            .end()
            .build()
    }

    private fun aptDealJobParametersValidator(): JobParametersValidator {
        val validator = CompositeJobParametersValidator()
        validator.setValidators(listOf(YearMonthParameterValidator()))

        return validator
    }

    @JobScope
    @Bean
    fun guLawdCdStep(guLawdCdTasklet: Tasklet): Step {
        return stepBuilderFactory["guLawdCdStep"]
            .tasklet(guLawdCdTasklet)
            .build()
    }

    @StepScope
    @Bean
    fun guLawdCdTasklet(
        lawdRepository: LawdRepository
    ): Tasklet = GuLawdTasklet(lawdRepository)

    @JobScope
    @Bean
    fun stepContextPrintStep(
        contextPrintTasklet: Tasklet
    ): Step {
        return stepBuilderFactory["stepContextPrintStep"]
            .tasklet(contextPrintTasklet)
            .build()
    }

    @StepScope
    @Bean
    fun contextPrintTasklet(
        @Value("#{jobExecutionContext['guLawdCd']}") guLawdCd: String
    ): Tasklet =
        Tasklet { contribution, chunkContext ->

            println("[contextPrintStep] guLawdCd = $guLawdCd")

            RepeatStatus.FINISHED
        }

    @JobScope
    @Bean
    fun aptDealInsertStep(
        aptDealResourceReader: StaxEventItemReader<AptDealDto>,
        aptDealWriter: ItemWriter<AptDealDto>
    ): Step {
        return stepBuilderFactory["aptDealInsertStep"]
            .chunk<AptDealDto, AptDealDto>(10)
            .reader(aptDealResourceReader)
            .writer(aptDealWriter)
            .build()
    }

    @StepScope
    @Bean
    fun aptDealResourceReader(
        @Value("#{jobParameters['yearMonth']}")
        yearMonth: String,
        @Value("#{jobExecutionContext['guLawdCd']}")
        guLawdCd: String,
        jaxb2Marshaller: Jaxb2Marshaller
    ): StaxEventItemReader<AptDealDto> {
        return StaxEventItemReaderBuilder<AptDealDto>()
            .name("aptDealResourceReader")
            .resource(apartmentApiResource.getResource(guLawdCd, YearMonth.parse(yearMonth)))
            .addFragmentRootElements("item") //각 데이터들의 root 지정
            .unmarshaller(jaxb2Marshaller)
            .build()
    }

    @StepScope
    @Bean
    fun aptDealDtoMarshaller(): Jaxb2Marshaller {
        val jaxb2Marshaller = Jaxb2Marshaller()
        jaxb2Marshaller.setClassesToBeBound(AptDealDto::class.java) //언마샬링할 dto 객체 지정

        return jaxb2Marshaller
    }

    @StepScope
    @Bean
    fun aptDealWriter(aptDealService: AptDealService): ItemWriter<AptDealDto> {
        return ItemWriter { items ->
            items.forEach { aptDealService.upsert(it) }

            println("================= COMMIT ===================")
        }
    }
}