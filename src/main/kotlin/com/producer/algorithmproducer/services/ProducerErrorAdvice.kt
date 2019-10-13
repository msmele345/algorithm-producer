package com.producer.algorithmproducer.services

import org.springframework.integration.core.MessagingTemplate
import org.springframework.integration.handler.advice.AbstractRequestHandlerAdvice
import org.springframework.integration.transformer.MessageTransformationException
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import java.io.IOException


@Service
class ProducerErrorAdvice(
    val messagingTemplate: MessagingTemplate,
    val errorQueue: MessageChannel
) : AbstractRequestHandlerAdvice() {
    override fun doInvoke(callback: ExecutionCallback, target: Any?, message: Message<*>): Any? {

        return try {
            callback.execute()
        } catch (ex: MessageTransformationException) {
            ex.cause?.let { transformationCause ->
                when (transformationCause) {
                    is IOException -> {
                        val errorMessage = MessageBuilder
                            .withPayload(message.payload)
                            .copyHeadersIfAbsent(message.headers)
                            .setHeader("errorMessage", ex.cause)
                            .build()
                        messagingTemplate.send(errorQueue, errorMessage)
                    }
                }
            }
            null
        }

    }


}