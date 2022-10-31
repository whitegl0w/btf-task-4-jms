package com.example.bfttaskjms.jms

import com.example.bfttaskjms.personRepository.Person
import com.example.bfttaskjms.personRepository.PersonRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Component
class PersonJmsListener(
    private val repository: PersonRepository
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(PersonRepository::class.java)
    }

    @JmsListener(destination = "\${jms.queue}")
    fun personJmsListener(persons: List<Person>) {
        persons.forEach {
            if (repository.findByLastNameAndName(it).isEmpty()) {
                repository.add(it)
                logger.info("Added Person (${it.name} ${it.lastName}})")
            } else
                logger.info("Ignoring Person (${it.name} ${it.lastName}})")
        }
    }
}