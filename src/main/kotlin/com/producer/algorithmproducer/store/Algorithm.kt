package com.producer.algorithmproducer.store

import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header

data class Algorithm (
    val algorithmId: Int = 0,
    val name: String = "",
    val codeSnippet: String = "",
    val category: String = Category.EASY.value
)


enum class Category(val value: String) {
    HARD("HARD"),
    EASY("EASY"),
    MEDIUM("MEDIUM"),
    EXTREME_PROGRAMMING("Extreme Programming")
}

interface Gate {
    fun exchange(@Header(KafkaHeaders.TOPIC) topic: String, out: String)
}
