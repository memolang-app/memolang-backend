package app.memolang.memolangbackend.mail

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
class OtpMailSender(private val javaMailSender: JavaMailSender) {
    fun sendOtp(receiver: String, otp: String) {
        val message = SimpleMailMessage()
        message.from = "info@memolang.app"
        message.setTo(receiver)
        message.subject = "Memolang Email Verification"
        message.text = body(otp)
        javaMailSender.send(message)
    }

    private fun body(otp: String) = "Hey! Your verification code is $otp."
}
