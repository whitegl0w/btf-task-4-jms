package com.example.bfttaskjms

import com.example.bfttaskjms.jms.PersonJmsErrorHandler
import com.example.bfttaskjms.personRepository.Person
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.support.converter.MappingJackson2MessageConverter
import org.springframework.jms.support.converter.MessageConverter
import org.springframework.jms.support.converter.MessageType
import javax.jms.ConnectionFactory
import javax.jms.Message

@Configuration
@EnableJms
class Configuration {

    @Bean
    fun dataSource() = EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .addScript("sql/schema.sql")
        .build()

    @Bean
    fun jmsListenerContainerFactory(cf: ConnectionFactory, conf: DefaultJmsListenerContainerFactoryConfigurer) =
        DefaultJmsListenerContainerFactory().apply {
            conf.configure(this, cf)
             setErrorHandler(PersonJmsErrorHandler())
        }

    @Bean
    fun jacksonJmsMessageConverter(): MessageConverter {

        return object : MappingJackson2MessageConverter() {
            override fun getJavaTypeForMessage(message: Message) =
                TypeFactory.defaultInstance().constructType(Array<Person>::class.java)
        }.apply {
            setObjectMapper(XmlMapper().registerKotlinModule())
            setTargetType(MessageType.TEXT)
        }
    }
}