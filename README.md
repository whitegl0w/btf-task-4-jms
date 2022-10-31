### JMS сервис

Работает с брокером Apache ActiveMQ Artemis.

Слушает очередь от брокера и читает XML вида

    <persons>
       <person>
          <name>Fedya</name>
          <last-name>Dostoevskii</last-name>
       </person>
    </persons>

### Конфигурация
Для работы в файле `application.properties` необходимо задать настройки artemis
    
    spring.artemis.mode=native
    spring.artemis.host=localhost
    spring.artemis.user=admin
    spring.artemis.password=<password>

А также задать название очереди

    jms.queue=Q.Persons

Для работы тестов также неоходимо выставить свойство `test.jms.wait-listener`, которые указывает максимальный ожидаемый таймаут от отправки данных до отработки JMS-Listener

    test.jms.wait-listener-ms=500