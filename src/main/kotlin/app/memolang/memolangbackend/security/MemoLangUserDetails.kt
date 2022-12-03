package app.memolang.memolangbackend.security

import app.memolang.memolangbackend.entity.MemoLangUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class MemoLangUserDetails(private val userEntity: MemoLangUser): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    override fun getPassword(): String = userEntity.passwordHash!!

    override fun getUsername(): String = userEntity.username!!

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
