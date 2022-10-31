package com.example.bfttaskjms

import com.example.bfttaskjms.personRepository.Person
import com.example.bfttaskjms.personRepository.PersonRepository
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/person", produces=[MediaType.APPLICATION_JSON_VALUE])
class PersonRestController (
    private val repository: PersonRepository
) {
    @GetMapping
    fun getAllPersons() =
        repository.getAll()

    @GetMapping("/name/{name}")
    fun getPersonByName(@PathVariable name: String) =
        repository.findByName(name)

    @GetMapping("/lastName/{lastName}")
    fun getPersonByLastName(@PathVariable lastName: String) =
        repository.findByLastName(lastName)

    @GetMapping("/id/{id}")
    fun getPersonById(@PathVariable id: Long) =
        repository.findById(id) ?: throw PersonNotFoundException(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addPerson(@RequestBody newPerson: Person, response: HttpServletResponse) {
        val id = repository.add(newPerson)
        response.addHeader("Location", "/person/id/$id")
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updatePerson(@RequestBody newPerson: Person, @PathVariable id: Long) {
        val ok = repository.update(id, newPerson)
        if (!ok) throw PersonNotFoundException(id)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePerson(@PathVariable id: Long) {
        val ok = repository.delete(id)
        if (!ok) throw PersonNotFoundException(id)
    }
}

internal class PersonNotFoundException(id: Long) : RuntimeException("Couldn't find person id=$id")

@ControllerAdvice
internal class PersonAdvice {
    @ResponseBody
    @ExceptionHandler(PersonNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun personNotFoundHandler(ex: PersonNotFoundException) =
        ex.message
}