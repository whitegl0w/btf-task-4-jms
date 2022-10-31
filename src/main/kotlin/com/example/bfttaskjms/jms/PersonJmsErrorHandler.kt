package com.example.bfttaskjms.jms

import com.fasterxml.jackson.databind.DatabindException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.ErrorHandler

@Component
class PersonJmsErrorHandler : ErrorHandler {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(PersonJmsErrorHandler::class.java)
    }

    override fun handleError(t: Throwable) {
        if (t is DatabindException)
            logger.info("JMS service error: ${t.message}")
        else
            throw t
    }

}