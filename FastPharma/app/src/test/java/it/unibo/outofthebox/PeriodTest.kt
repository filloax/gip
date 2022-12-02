package it.unibo.outofthebox

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

fun main() {
    val testDate = LocalDate.of(2024, 12, 10)
    val now = LocalDate.now()
    val period = Period.between(now, testDate)
    println(testDate)
    println(now)
    println("years: ${period.years}, months: ${period.months}, days: ${period.days}")

    println(DateTimeFormatter.ofPattern("MM-YYYY").format(testDate))
}