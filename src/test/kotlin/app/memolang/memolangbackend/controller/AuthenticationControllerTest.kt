package app.memolang.memolangbackend.controller

import app.memolang.memolangbackend.BaseIntegrationTest
import app.memolang.memolangbackend.entity.FlashCardEntity
import app.memolang.memolangbackend.entity.StudySubjectEntity
import app.memolang.memolangbackend.repository.FlashCardRepository
import app.memolang.memolangbackend.repository.StudySubjectRepository
import app.memolang.memolangbackend.shouldBe
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class AuthenticationControllerTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var flashCardRepository: FlashCardRepository

    @Autowired
    private lateinit var subjectRepository: StudySubjectRepository

    @Test
    fun `Test registering a new user`() {
        successfullyCreateUser()
    }

    @Test
    fun `test deleting user`() {
        val token = successfullyCreateUser("foo@bar.com", "bar")
        subjectRepository.save(
            StudySubjectEntity(
                name = "Spanish",
                ownerUsername = "foo@bar.com",
                flashCards = mutableListOf(FlashCardEntity(question = "hablar", answer = "to speak")),
            )
        )
        subjectRepository.count() shouldBe 1
        flashCardRepository.count() shouldBe 1
        restTemplate.exchange(
            AUTHENTICATION_BASE_URL,
            HttpMethod.DELETE,
            request(token),
            Any::class.java
        ).statusCode shouldBe HttpStatus.NO_CONTENT
        subjectRepository.count() shouldBe 0
        flashCardRepository.count() shouldBe 0
    }

    @Test
    fun `Test login`() {
        successfullyCreateUser("foo", "bar")
        successfullyLogin("foo", "bar")
        val response = restTemplate.postForEntity(
            LOGIN_URL,
            mapOf(
                "username" to "foo",
                "password" to "baz",
            ),
            Any::class.java
        )
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `Test resetting password`() {
        successfullyCreateUser("foo@bar.com", "1234")
        val otp = successfullySendOtpRequest("foo@bar.com", forRegistration = false)
        val response = restTemplate.exchange(
            AUTHENTICATION_BASE_URL,
            HttpMethod.PUT,
            HttpEntity(
                mapOf(
                    "username" to "foo@bar.com",
                    "password" to "456",
                    "otp" to otp,
                )
            ),
            Any::class.java
        )
        response.statusCode shouldBe HttpStatus.OK
        successfullyLogin("foo@bar.com", "456")
    }

    @Test
    fun `Creating two users with the same username fails`() {
        successfullyCreateUser("foo@bar.com", "bar")
        val response = restTemplate.postForEntity(
            AUTHENTICATION_BASE_URL,
            mapOf(
                "username" to "foo@bar.com",
                "password" to "baz",
                "otp" to "123"
            ),
            Any::class.java
        )
        Assertions.assertEquals(HttpStatus.CONFLICT, response.statusCode)
    }

    private fun successfullyLogin(username: String, password: String) {
        val response = restTemplate.postForEntity(
            LOGIN_URL,
            mapOf(
                "username" to username,
                "password" to password,
            ),
            AuthenticatedUserPayload::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertNotEquals(response.body!!.token, "")
        Assertions.assertEquals(response.body!!.username, username)
    }
}
