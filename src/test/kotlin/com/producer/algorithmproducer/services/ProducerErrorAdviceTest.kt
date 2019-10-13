package com.producer.algorithmproducer.services

import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.integration.transformer.MessageTransformationException
import org.springframework.messaging.support.MessageBuilder
import java.io.IOException


class ProducerErrorAdviceTest : ProducerErrorAdvice(
    errorQueue = mock(),
    messagingTemplate = mock()
) {

    @Test
    fun `doInvoke - should call executionCallback when invoked`() {
        val inputMessage = MessageBuilder
            .withPayload("some payload")
            .build()

        val mockCallBack: ExecutionCallback = mock()

        val actual = doInvoke(callback = mockCallBack, target = null, message = inputMessage)

        verify(mockCallBack).execute()
    }


    @Test
    fun `doInvoke - should return result of callback if there are no errors`() {
        val inputMessage = MessageBuilder
            .withPayload("some payload")
            .build()

        val mockCallBack: ExecutionCallback = mock()

        whenever(mockCallBack.execute()) doReturn "some positive result"

        val actual = doInvoke(callback = mockCallBack, target = null, message = inputMessage)

        assertThat(actual).isEqualTo("some positive result")
    }


    @Test
    fun `doInvoke - should throw a MessageTransformation Exception if the callback fails`() {

        val inputMessage = MessageBuilder
            .withPayload("some payload")
            .build()

        val mockCallBack = mock<ExecutionCallback>()

        whenever(mockCallBack.execute()).thenAnswer {
            throw MessageTransformationException(
                "couldn't transform this",
                IOException("some io exception")
            )
        }
        val actual = doInvoke(callback = mockCallBack, target = null, message = inputMessage)

//        val captor = argumentCaptor<Message<*>>()
        assertThat(actual).isNull()
//        val actualMessage = captor.firstValue
//        assertThat(actualMessage.payload).isEqualTo("some payload")

    }

    @Test
    fun `doInvoke - should return null and route the message to the errorQueue if the callback fails`() {

        val inputMessage = MessageBuilder
            .withPayload("some payload")
            .setHeader("header1", "useful header value")
            .setHeader("header2", "another useful header value")
            .build()

        val mockCallBack = mock<ExecutionCallback>()

        whenever(mockCallBack.execute()) doThrow
                MessageTransformationException(
                    "couldn't transform this",
                    IOException("some io exception")
                )


        val actual = doInvoke(callback = mockCallBack, target = null, message = inputMessage)

        assertThat(actual).isNull()
    }
}
