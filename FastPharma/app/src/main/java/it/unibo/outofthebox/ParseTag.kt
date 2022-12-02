package it.unibo.outofthebox

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ParseTag {
    fun getDate(msg: String): LocalDate? {
        val datePattern = Regex("\\d+-\\d{2}-\\d{2}(?!\\d)")
        datePattern.find(msg)?.let {
            return LocalDate.parse(it.value, DateTimeFormatter.ISO_LOCAL_DATE)
        }
        return null
    }
}