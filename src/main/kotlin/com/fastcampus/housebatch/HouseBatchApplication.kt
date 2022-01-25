package com.fastcampus.housebatch

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableBatchProcessing
@SpringBootApplication
class HouseBatchApplication

fun main(args: Array<String>) {
	runApplication<HouseBatchApplication>(*args)
}