package app.memolang.memolangbackend.controller

import app.memolang.memolangbackend.BaseIntegrationTest
import app.memolang.memolangbackend.shouldBe
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class AuthenticationControllerTest : BaseIntegrationTest() {

    @Test
    fun `Test registering a new user`() {
        successfullyCreateUser()
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
