package it.unibo.outofthebox

import android.nfc.NdefMessage
import android.util.Log
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

    fun parseTagMedicine(tagMessage: NdefMessage): TagMedicine? {
        val datePatternLong = Regex("\\d+-\\d{2}-\\d{2}(?!\\d)")
        val datePattern = Regex("\\d+-\\d{2}")
        var date: LocalDate? = null

        val namePattern = Regex("NAME=\"(.*)\"")
        var name: String? = null

        tagMessage.records.forEach {
            val msg = String(it.payload)

            // Check date
            val dateMatch = datePattern.find(msg)

            if (dateMatch != null) {
                val dateString = datePatternLong.find(msg)?.value
                    ?: "${dateMatch.value}-01"
                date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
            }

            // Check name
            val nameMatch = namePattern.find(msg)
            if (nameMatch != null) {
                name = nameMatch.groupValues[1]
            }
        }

        if (date == null) {
            Log.d("OutOfTheBox", "date not valid: $tagMessage")
            return null
        }

        return TagMedicine(date!!, name ?: "")
    }

    fun parseTagCloset(tagMessage: NdefMessage): TagCloset? {
        val closetPattern = Regex("FPD.*CLOSET=\"(\\w+)\"")
        var closetId: String? = null

        tagMessage.records.forEach {
            val msg = String(it.payload)
            closetPattern.find(msg)?.let { match ->
                closetId = match.groupValues[1]
            }
        }

        if (closetId == null) {
            Log.d("OutOfTheBox", "closet tag not valid: $tagMessage")
            return null
        }

        return TagCloset(closetId!!)
    }
}

data class TagMedicine (
    val expirationDate: LocalDate,
    val name: String,
)

data class TagCloset (
    val id: String,
)