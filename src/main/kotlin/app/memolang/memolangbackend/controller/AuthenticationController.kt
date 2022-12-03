package app.memolang.memolangbackend.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/api/users")
class AuthenticationController {
    @PostMapping
    fun register() {

    }

    @PostMapping("/login")
    fun login() {

    }
}
