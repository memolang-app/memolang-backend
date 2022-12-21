package app.memolang.memolangbackend

import app.memolang.memolangbackend.controller.AUTHENTICATION_BASE_URL
import app.memolang.memolangbackend.controller.AuthenticatedUserPayload
import app.memolang.memolangbackend.controller.LOGIN_OTP_URL
import app.memolang.memolangbackend.controller.REGISTRATION_OTP_URL
import app.memolang.memolangbackend.mail.OtpMailSender
import app.memolang.memolangbackend.repository.MemoLangUserRepository
import app.memolang.memolangbackend.repository.OtpRepository
import app.memolang.memolangbackend.repository.StudySubjectRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus

typealias Token = String

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseIntegrationTest {
    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var subjectRepository: StudySubjectRepository

    @Autowired
    private lateinit var userRepository: MemoLangUserRepository

    @Autowired
    private lateinit var otpRepository: OtpRepository

    @MockkBean
    private lateinit var mockedOtpSender: OtpMailSender

    @BeforeEach
    fun clearDb() {
        subjectRepository.deleteAll()
        userRepository.deleteAll()
        otpRepository.deleteAll()
    }

    protected fun successfullySendOtpRequest(username: String, forRegistration: Boolean): String {
        var sentOtp: String? = null
        every { mockedOtpSender.sendOtp(username, any()) } answers { sentOtp = secondArg() }
        val otpResponse = restTemplate.postForEntity(
            if (forRegistration) REGISTRATION_OTP_URL else LOGIN_OTP_URL,
            mapOf("claimedEmail" to username),
            Any::class.java
        )
        otpResponse.statusCode shouldBe HttpStatus.OK
        sentOtp shouldNotBe null
        return sentOtp!!
    }

    protected fun successfullyCreateUser(
        username: String = "foo@bar.com",
        password: String = "bar",
    ): Token {
        val sentOtp = successfullySendOtpRequest(username, forRegistration = true)
        val response = restTemplate.postForEntity(
            AUTHENTICATION_BASE_URL,
            mapOf(
                "username" to username,
                "password" to password,
                "otp" to sentOtp
            ),
            AuthenticatedUserPayload::class.java
        )
        Assertions.assertEquals(HttpStatus.CREATED, response.statusCode)
        Assertions.assertNotEquals(response.body!!.token, "")
        Assertions.assertEquals(response.body!!.username, username)
        return response.body!!.token
    }

    protected fun <T> request(token: Token, body: T): HttpEntity<T> {
        val headers = HttpHeaders().apply { set("Authorization", "Bearer $token") }
        return HttpEntity(body, headers)
    }

    protected fun request(token: Token): HttpEntity<Any> {
        val headers = HttpHeaders().apply { set("Authorization", "Bearer $token") }
        return HttpEntity(headers)
    }
}
