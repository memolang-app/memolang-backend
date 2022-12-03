package app.memolang.memolangbackend.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

const val AUTHENTICATION_BASE_URL = "/api/users"

@RestController
class AuthenticationController {
    @PostMapping(AUTHENTICATION_BASE_URL)
    fun register() {

    }

    @PostMapping("$AUTHENTICATION_BASE_URL/login")
    fun login() {

    }
}
