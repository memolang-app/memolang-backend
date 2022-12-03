package app.memolang.memolangbackend.security

import app.memolang.memolangbackend.repository.MemoLangUserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class MemoLangUserDetailsService(private val repository: MemoLangUserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = repository.findByUsername(username) ?: throw UsernameNotFoundException(username)
        return MemoLangUserDetails(user)
    }
}
