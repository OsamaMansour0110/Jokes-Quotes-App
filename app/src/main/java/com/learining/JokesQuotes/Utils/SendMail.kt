package com.learining.JokesQuotes.Utils

import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlinx.coroutines.*

fun sendEmail(toEmail: String, code: String) {
    val username = "os.ahmad2909@gmail.com"
    val password =  "oipt bowy mjbt nadu"

    val props = Properties().apply {
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.host", "smtp.gmail.com")
        put("mail.smtp.port", "587")
    }

    val session = Session.getInstance(props, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(username, password)
        }
    })

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(username))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                subject = "Your verification code"
                setText("Your code is: $code")
            }
            Transport.send(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}