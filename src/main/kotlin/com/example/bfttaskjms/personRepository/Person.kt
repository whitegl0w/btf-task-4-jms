package com.example.bfttaskjms.personRepository

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class Person(
    var id: Long? = null,
    var name: String,
     @JacksonXmlProperty(localName = "last-name")
    var lastName: String,
)