package com.fastcampus.housebatch.job.apt

import com.fastcampus.housebatch.adapter.ApartmentApiResource
import com.fastcampus.housebatch.core.dto.AptDealDto
import com.fastcampus.housebatch.job.validator.FilePathParameterValidator
import com.fastcampus.housebatch.job.validator.LawdCdParameterValidator
import com.fastcampus.housebatch.job.validator.YearMonthParameterValidator
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersValidator
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.CompositeJobParametersValidator
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.xml.StaxEventItemReader
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder
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
    private val apartmentApiResource: ApartmentApiResource
) {
    @Bean
    fun aptDealInsertJob(
        aptDealInsertStep: Step
    ): Job {
        return jobBuilderFactory["aptDealInsertJob"]
            .incrementer(RunIdIncrementer())
            .validator(aptDealJobParametersValidator())
            .start(aptDealInsertStep)
            .build()
    }

    private fun aptDealJobParametersValidator(): JobParametersValidator {
        val validator = CompositeJobParametersValidator()
        validator.setValidators(
            listOf(
                YearMonthParameterValidator(),
                LawdCdParameterValidator()
            )
        )

        return validator
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
        @Value("#{jobParameters['lawdCd']}")
        lawdCd: String,
        jaxb2Marshaller: Jaxb2Marshaller
    ): StaxEventItemReader<AptDealDto> {
        return StaxEventItemReaderBuilder<AptDealDto>()
            .name("aptDealResourceReader")
            .resource(apartmentApiResource.getResource(lawdCd, YearMonth.parse(yearMonth)))
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
    fun aptDealWriter(): ItemWriter<AptDealDto> {
        return ItemWriter { items ->
            items.forEach { println(it) }
            println("================= COMMIT ===================")
        }
    }
}