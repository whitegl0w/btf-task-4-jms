package com.example.bfttaskjms.personRepository

import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
@Primary
class PersonRepositoryJdbcImpl (
    private val jdbcTemplate: JdbcTemplate
) : PersonRepository {

    val personRowMapper = RowMapper<Person> { rs: ResultSet, _: Int ->
        Person(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("lastName")
        )
    }

    override fun getAll(): List<Person> {
        return jdbcTemplate.query(
            "select * from Person",
            personRowMapper
        )
    }

    override fun findByName(name: String): List<Person> {
        return jdbcTemplate.query(
            "select * from Person where name = ?",
            personRowMapper,
            name
        )
    }

    override fun findByLastName(lastName: String): List<Person> {
        return jdbcTemplate.query(
            "select * from Person where lastName = ?",
            personRowMapper,
            lastName
        )
    }

    override fun findByLastNameAndName(person: Person): List<Person> {
        return jdbcTemplate.query(
            "select * from Person where name = ? and lastName = ?",
            personRowMapper,
            person.name,
            person.lastName,
        )
    }

    override fun findById(id: Long): Person? {
        return jdbcTemplate.query(
            "select * from Person where id = ?",
            personRowMapper,
            id
        ).getOrNull(0)
    }

    override fun add(newPerson: Person): Long {
        return with(SimpleJdbcInsert(jdbcTemplate)) {
            withTableName("Person")
            setGeneratedKeyName("id")
            executeAndReturnKey(
                mapOf(
                    "name" to newPerson.name,
                    "lastName" to newPerson.lastName,
                )
            ) as Long
        }
    }

    override fun update(id: Long, newPerson: Person): Boolean {
        return 0 != jdbcTemplate.update(
            "update Person set name = ?, lastName = ? where id = ?",
            newPerson.name,
            newPerson.lastName,
            id
        )
    }

    override fun delete(id: Long): Boolean {
        return 0 != jdbcTemplate.update(
            "delete Person where id = ?",
            id
        )
    }
}