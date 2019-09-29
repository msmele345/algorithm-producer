package com.producer.algorithmproducer

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.IntegrationMessageHeaderAccessor
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.Pollers
import org.springframework.integration.file.dsl.Files
import org.springframework.integration.kafka.dsl.Kafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.DefaultKafkaHeaderMapper
import java.io.File
import java.util.HashMap
import java.util.stream.Stream

@Configuration
class AlgorithmProducerConfig {


    @Value(value = "\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapAddress: String



    @Bean
    fun integrationFlow(): IntegrationFlow {
        return IntegrationFlows.from(
            Files.inboundAdapter(File("./BOOT-INF/classes/static"))
            .patternFilter("*.json")
        ) { e -> e.poller(Pollers.fixedDelay(1000)) }
            .split(Files.splitter())
            .log("GOT STATIC FILE")
            .channel("toKafkaInput")
            .get()
    }


    @Bean
    fun sendToKafkaFlow() =
        IntegrationFlow { flow ->
            flow.channel("toKafkaInput")
            flow.split<String>({ p -> Stream.generate { p }.limit(101) }, null) //double check if this is needed
                .publishSubscribeChannel { channel ->
                    channel.subscribe { sf ->
                        sf.handle(
                            kafkaMessageHandler(producerFactory(), "algorithm_complete")
                                .timestampExpression("T(Long).valueOf('1487694048633')")
                        ) { e -> e.id("dummy") }
                    }
                        .subscribe { sf ->
                            sf.handle(
                                kafkaMessageHandler(producerFactory(), "dummy")
                                    .timestamp<Any> { _ -> 1487694048644L }
                            ) { e -> e.id("dummy") }
                        }
                }
        }


    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val configProps = HashMap<String, Any>().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)

        }
        return DefaultKafkaProducerFactory(configProps)
    }


    @Bean
    fun mapper(): DefaultKafkaHeaderMapper {
        return DefaultKafkaHeaderMapper()
    }


    private fun kafkaMessageHandler(producerFactory: ProducerFactory<String, String>, topic: String) =
        Kafka.outboundChannelAdapter(producerFactory)
            .messageKey<Any> { m -> m.headers[IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER] }
            .headerMapper(mapper())
            .sync(true)
            .partitionId<Any> { _ -> 0 }
            .topicExpression("headers[kafka_topic] ?: '$topic'")
            .configureKafkaTemplate { t -> t.id("kafkaTemplate:$topic") }

}