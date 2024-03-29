package com.producer.algorithmproducer

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.integration.IntegrationMessageHeaderAccessor
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.core.MessagingTemplate
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.Pollers
import org.springframework.integration.file.dsl.Files
import org.springframework.integration.kafka.dsl.Kafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.DefaultKafkaHeaderMapper
import java.io.File
import java.util.*
import java.util.stream.Stream

@Configuration
@ComponentScan
class AlgorithmProducerConfig {


    @Value(value = "\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapAddress: String

    @Bean
    fun errorQueue(): DirectChannel {
        return DirectChannel()
    }


    @Bean
    fun messagingTemplate(): MessagingTemplate {
        return MessagingTemplate()
    }

    @Bean
    fun mapper(): DefaultKafkaHeaderMapper {
        return DefaultKafkaHeaderMapper()
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val configProps = HashMap<String, Any>().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.LINGER_MS_CONFIG, 1)

        }
        return DefaultKafkaProducerFactory(configProps)
    }

    //inbound adapter. Reads from static files and publishes messages to kafka input channel
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
    fun loadToKafkaFlow(): IntegrationFlow {
        return IntegrationFlows
            .from("toKafkaInput")
            .log("starting KAFKA FLOW")
            .split<String>({ p -> Stream.generate { p }.limit(2) }, null) //check the size again
            .publishSubscribeChannel { channel ->
                channel.subscribe { sf ->
                    sf.handle(
                        kafkaMessageHandler(producerFactory(), "algorithm_complete")
                            .timestampExpression("T(Long).valueOf('1487694048633')")
                    ) { e -> e.id("kafkaProducer1") }
                }
                channel.subscribe { sf ->
                    sf.handle(
                        kafkaMessageHandler(producerFactory(), "dummy")
                            .timestamp<Any> { _ -> 1487694048644L }
                    ) { e -> e.id("kafkaProducer2") }
                }
            }
            .log("FINISHED WITH PUB SUB")
            .get()
    }

    //sends messages received from the ProducingChannel towards a topic.
    private fun kafkaMessageHandler(producerFactory: ProducerFactory<String, String>, topic: String) =
        Kafka.outboundChannelAdapter(producerFactory)
            .messageKey<Any> { m -> m.headers[IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER].toString() }
            .headerMapper(mapper())
            .sync(true)
            .partitionId<Any> { _ -> 0 }
            .topicExpression("headers[kafka_topic] ?: '$topic'")
            .configureKafkaTemplate { t -> t.id("kafkaTemplate:$topic") }
            .sendFailureChannel(errorQueue())
}
