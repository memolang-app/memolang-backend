package app.memolang.memolangbackend.controller

import app.memolang.memolangbackend.entity.MemoLangUserEntity
import app.memolang.memolangbackend.repository.MemoLangUserRepository
import app.memolang.memolangbackend.security.JwtUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

const val AUTHENTICATION_BASE_URL = "/api/users"

@RestController
class AuthenticationController(
    private val userRepository: MemoLangUserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val jwtUtils: JwtUtils,
) {

    @PostMapping(AUTHENTICATION_BASE_URL)
    fun register(@RequestBody body: AuthenticationRequestPayload): ResponseEntity<Any> {
        if (userRepository.findByUsername(body.username) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("message" to "username taken"))
        }
        val user = MemoLangUserEntity(username = body.username, passwordHash = passwordEncoder.encode(body.password))
        userRepository.save(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(body.toAuthenticatedUserPayload())
    }

    @PostMapping("$AUTHENTICATION_BASE_URL/login")
    fun login(@RequestBody body: AuthenticationRequestPayload): AuthenticatedUserPayload {
        val unauthorizedException = ResponseStatusException(HttpStatus.UNAUTHORIZED)
        val user = userRepository.findByUsername(body.username) ?: throw unauthorizedException
        if (user.passwordHash != passwordEncoder.encode(body.password)) throw unauthorizedException
        return body.toAuthenticatedUserPayload()
    }

    private fun AuthenticationRequestPayload.toAuthenticatedUserPayload() =
        AuthenticatedUserPayload(
            username = username,
            token = jwtUtils.generateJwtToken(UsernamePasswordAuthenticationToken(username, password))
        )
}

data class AuthenticationRequestPayload(
    val username: String,
    val password: String,
)

data class AuthenticatedUserPayload(
    val username: String,
    val token: String,
)