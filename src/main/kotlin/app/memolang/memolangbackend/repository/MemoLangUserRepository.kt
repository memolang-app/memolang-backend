package app.memolang.memolangbackend.repository

import app.memolang.memolangbackend.entity.MemoLangUser
import org.springframework.data.repository.CrudRepository

interface MemoLangUserRepository: CrudRepository<MemoLangUser, Long> {
    fun findByUsername(username: String): MemoLangUser?
}
