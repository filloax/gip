package it.unibo.outofthebox

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import org.w3c.dom.Text
import java.time.Duration
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class NFCResult : AppCompatActivity() {
    private var expirationDate: LocalDate? = null

    private fun processTag(intent: Intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            Log.i("OutOfTheBox", "NDEF DISCOVERED")
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
                val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
                messages.forEach {
                    Log.i("OutOfTheBox", "Read message $it")
                    val msg = String(it.records[0].payload)
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                    Log.i("OutOfTheBox", "Message contains $msg")

                    val date = ParseTag.getDate(msg)
                    if (date != null) {
                        expirationDate = date
                        return
                    }
                }
            }
        }

        expirationDate = null
    }

    private fun processDate(date: LocalDate) {
        val now = LocalDate.now()
        val timeLeft = Period.between(now, date)
        val timeStr = if (timeLeft.years > 0 || timeLeft.months >= 1) {
            "${-timeLeft.months -timeLeft.years*12} mesi"
        } else {
            "${-timeLeft.days} gg"
        }

        findViewById<TextView>(R.id.expTimeLeft).text = timeStr
        findViewById<TextView>(R.id.expDate).text = date.format(DateTimeFormatter.ofPattern("MM-YYYY"))

        val background = findViewById<View>(R.id.background)
        val infoText = findViewById<TextView>(R.id.expInfo)
        if (date.isBefore(now)) {
            background.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.exp_expired))
            infoText.text = resources.getString(R.string.expir_date_expired)
            infoText.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        } else if (timeLeft.years == 0) {
            background.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.exp_notsellable))
            infoText.text = resources.getString(R.string.expir_date_late)
            infoText.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        } else if (timeLeft.months <= 1) {
            background.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.exp_warning))
            infoText.text = resources.getString(R.string.expir_date_warming)
            infoText.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        } else {
            background.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.exp_good))
            infoText.text = ""
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        Log.i("OutOfTheBox", "NFCResult intent: $intent")
        processTag(intent);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfcresult)

        Log.i("OutOfTheBox", "NFCResult activity intent: $intent")
        processTag(intent);

        if (expirationDate != null) {
            processDate(expirationDate!!)
        } else {
            val background = findViewById<View>(R.id.background)
            val infoText = findViewById<TextView>(R.id.expInfo)
            background.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.exp_wrongformat))
            infoText.text = resources.getString(R.string.expir_date_error)
            infoText.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        }
    }
}