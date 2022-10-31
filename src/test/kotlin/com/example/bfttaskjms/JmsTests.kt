package com.example.bfttaskjms

import com.example.bfttaskjms.jms.PersonJmsListener
import com.example.bfttaskjms.personRepository.Person
import com.example.bfttaskjms.personRepository.PersonRepository
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.jms.core.JmsTemplate
import java.time.Duration

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JmsTests {

    @Autowired
    private lateinit var jmsTemplate: JmsTemplate

    @MockBean
    private lateinit var repository: PersonRepository

    @SpyBean
    private lateinit var jmsListener: PersonJmsListener

    @Value("\${jms.queue}")
    private lateinit var queueName: String

    @Value("\${test.jms.wait-listener-ms}")
    private var listenerWaitTime: Long = 0


    /** Данные для теста, которые будут вставляться */
    private var testPersons = List(2) {
        Person(name = rndStr(10), lastName = rndStr(20))
    }


    /** Проверка на корректность работы JMS Listener (добавление новых) */
    @Test
    fun testInsertingNewPerson() {
        // Имитация отсутствия данных людей в БД
        Mockito
            .`when`(repository.findByLastNameAndName(userAny(Person::class.java)))
            .thenReturn(emptyList())

        // Отправка данных в брокер
        jmsTemplate.convertAndSend(queueName, testPersons)
        waitJmsListenerFinishing(listenerWaitTime)

        // Проверка
        val requestCaptor: ArgumentCaptor<Person> = ArgumentCaptor.forClass(Person::class.java)
        Mockito.verify(repository, Mockito.atLeast(2)).add(capture(requestCaptor))

        assert(requestCaptor.allValues.toSet().containsAll(testPersons))
    }

    /** Проверка на корректность работы JMS Listener (игнор дупликатов) */
    @Test
    fun testIgnoringDuplicatesPerson() {

        // Имитация наличия людей в БД
        Mockito
            .`when`(repository.findByLastNameAndName(userAny(Person::class.java)))
            .thenReturn(listOf(Person(name = "leva", lastName = "tolstoi")))

        Mockito.clearInvocations(repository)

        // Отправка данных в брокер
        jmsTemplate.convertAndSend(queueName, testPersons)
        waitJmsListenerFinishing(listenerWaitTime)

        // Проверка, что он никого не вставил
        val requestCaptor: ArgumentCaptor<Person> = ArgumentCaptor.forClass(Person::class.java)
        Mockito.verify(repository, Mockito.never()).add(capture(requestCaptor))
    }


    /** Ожидание отработки JmsListener
     * @param ms максимальное время ожидания */
    fun waitJmsListenerFinishing(ms: Long) {
        // Ожидание отработки метода
        Mockito.clearInvocations(jmsListener)
        Awaitility.await().atMost(Duration.ofMillis(ms)).untilAsserted {
            Mockito.verify(jmsListener, Mockito.atLeastOnce())
                .personJmsListener(userAny(Array<Person>::class.java))
        }
    }

    // Всякие приведение типов c !!!, чтобы корректно работало в Kotlin
    fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
    fun <T> userAny(type: Class<T>): T = Mockito.any(type)

    // Генерация случайных имен
    companion object {
        private val charPool = ('a'..'z') + ('A'..'Z')
        private fun rndStr(len: Int) =
            (1..len)
                .map { kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
    }
}