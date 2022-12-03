package app.memolang.memolangbackend

import app.memolang.memolangbackend.controller.AUTHENTICATION_BASE_URL
import app.memolang.memolangbackend.controller.AuthenticatedUserPayload
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus

typealias Token = String

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseIntegrationTest {
    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var repositories: List<CrudRepository<*, *>>

    @BeforeEach
    fun clearDb() {
        repositories.forEach { it.deleteAll() }
    }

    protected fun successfullyCreateUser(
        username: String = "foo",
        password: String = "bar",
    ): Token {
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
