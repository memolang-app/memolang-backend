package app.memolang.memolangbackend.controller

import app.memolang.memolangbackend.entity.MemoLangUserEntity
import app.memolang.memolangbackend.entity.OtpEntity
import app.memolang.memolangbackend.mail.OtpMailSender
import app.memolang.memolangbackend.repository.MemoLangUserRepository
import app.memolang.memolangbackend.repository.OtpRepository
import app.memolang.memolangbackend.security.JwtUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import javax.transaction.Transactional
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import kotlin.random.Random
import kotlin.random.nextInt

const val AUTHENTICATION_BASE_URL = "/api/users"
const val LOGIN_URL = "$AUTHENTICATION_BASE_URL/login"
const val OTP_URL = "$AUTHENTICATION_BASE_URL/otp"

@RestController
class AuthenticationController(
    private val userRepository: MemoLangUserRepository,
    private val otpRepository: OtpRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val jwtUtils: JwtUtils,
    private val otpMailSender: OtpMailSender,
) {

    @PostMapping(AUTHENTICATION_BASE_URL)
    @Transactional
    fun register(@RequestBody body: RegistrationRequestPayload): ResponseEntity<Any> {
        if (userRepository.findByUsername(body.username) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("message" to "username taken"))
        }
        val otpRecord = otpRepository.findByClaimedEmail(body.username)
        if (otpRecord == null || otpRecord.used || otpRecord.code != body.otp) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("message" to "wrong otp")
        }
        otpRecord.used = true
        otpRepository.save(otpRecord)
        val user = MemoLangUserEntity(username = body.username, passwordHash = passwordEncoder.encode(body.password))
        userRepository.save(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(body.toAuthenticatedUserPayload())
    }

    @PostMapping(LOGIN_URL)
    @Transactional
    fun login(@RequestBody body: LoginRequestPayload): AuthenticatedUserPayload {
        val unauthorizedException = ResponseStatusException(HttpStatus.UNAUTHORIZED)
        val user = userRepository.findByUsername(body.username) ?: throw unauthorizedException
        if (!passwordEncoder.matches(body.password, user.passwordHash)) throw unauthorizedException
        return body.toAuthenticatedUserPayload()
    }

    @PostMapping(OTP_URL)
    @Transactional
    fun sendOtp(@RequestBody body: OtpRequestPayload): ResponseEntity<Any> {
        if (userRepository.findByUsername(body.claimedEmail) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("message" to "username taken"))
        }
        val otpNumber = Random.nextInt(10000..99999)
        val otpRecord = otpRepository.findByClaimedEmail(body.claimedEmail)
            ?: OtpEntity(claimedEmail = body.claimedEmail)
        otpRecord.code = otpNumber.toString()
        otpRecord.used = false
        otpRepository.save(otpRecord)
        otpMailSender.sendOtp(body.claimedEmail, otpNumber.toString())
        return ResponseEntity.ok().build()
    }

    private fun PayloadWithUsername.toAuthenticatedUserPayload() =
        AuthenticatedUserPayload(
            username = username,
            token = jwtUtils.generateJwtToken(username)
        )
}

interface PayloadWithUsername {
    val username: String
}

data class OtpRequestPayload(
    @Email @NotNull @NotBlank
    val claimedEmail: String
)

data class LoginRequestPayload(
    @NotBlank
    override val username: String,
    @NotBlank
    val password: String,
) : PayloadWithUsername

data class RegistrationRequestPayload(
    @NotBlank
    override val username: String,
    @NotBlank
    val password: String,
    @NotBlank
    val otp: String,
) : PayloadWithUsername

data class AuthenticatedUserPayload(
    val username: String,
    val token: String,
)
