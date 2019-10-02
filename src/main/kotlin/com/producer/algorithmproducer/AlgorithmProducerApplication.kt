package com.producer.algorithmproducer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class AlgorithmProducerApplication

fun main(args: Array<String>) {
	runApplication<AlgorithmProducerApplication>(*args)
}
