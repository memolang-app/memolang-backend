package app.memolang.memolangbackend.controller

import app.memolang.memolangbackend.BaseIntegrationTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `Test registering a new user`() {
        successfullyCreateUser("foo", "bar")
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
    fun `Creating two users with the same username fails`() {
        successfullyCreateUser("foo", "bar")
        val response = restTemplate.postForEntity(
            AUTHENTICATION_BASE_URL,
            mapOf(
                "username" to "foo",
                "password" to "baz",
            ),
            Any::class.java
        )
        Assertions.assertEquals(HttpStatus.CONFLICT, response.statusCode)
    }

    private fun successfullyCreateUser(username: String, password: String) {
        val response = restTemplate.postForEntity(
            AUTHENTICATION_BASE_URL,
            mapOf(
                "username" to username,
                "password" to password,
            ),
            AuthenticatedUserPayload::class.java
        )
        Assertions.assertEquals(HttpStatus.CREATED, response.statusCode)
        Assertions.assertNotEquals(response.body!!.token, "")
        Assertions.assertEquals(response.body!!.username, username)
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
