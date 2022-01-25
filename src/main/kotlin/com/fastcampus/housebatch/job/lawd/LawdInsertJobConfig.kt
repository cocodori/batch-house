package com.fastcampus.housebatch.job.lawd

import com.fastcampus.housebatch.core.entity.Lawd
import com.fastcampus.housebatch.core.service.LawdService
import com.fastcampus.housebatch.job.validator.FilePathParameterValidator
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

@Configuration
class LawdInsertJobConfig(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val lawdService: LawdService
) {
    @Bean
    fun lawdInsertJob(lawdInsertStep: Step): Job {
        return jobBuilderFactory["lawdInsertJob"]
            .incrementer(RunIdIncrementer())
            .validator(FilePathParameterValidator())
            .start(lawdInsertStep)
            .build()
    }

    @JobScope
    @Bean
    fun lawdInsertStep(
        lawdFileItemReader: FlatFileItemReader<Lawd>,
        lawdItemWriter: ItemWriter<Lawd>
    ): Step {
        return stepBuilderFactory["lawdInsertStep"]
            .chunk<Lawd, Lawd>(1000)
            .reader(lawdFileItemReader)
            .writer(lawdItemWriter)
            .build()
    }

    @StepScope
    @Bean
    fun lawdFileItemReader(
        @Value("#{jobParameters['filePath']}") filePath: String
    ): FlatFileItemReader<Lawd> {
        return FlatFileItemReaderBuilder<Lawd>()
            .name("lawdFileItemReader")
            .delimited()
            .delimiter("\t")
            .names(LAWD_CD, LAWD_DONG, EXIST)
            .linesToSkip(1)
            .fieldSetMapper(LawdFieldSetMapper())
            .resource(ClassPathResource(filePath))
            .build()
    }

    @StepScope
    @Bean
     fun lawdItemWriter(): ItemWriter<Lawd> {
        return ItemWriter<Lawd> { items ->
            items.forEach { lawdService.upsert(it) }
        }
    }
}