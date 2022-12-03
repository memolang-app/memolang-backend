package app.memolang.memolangbackend.repository

import app.memolang.memolangbackend.entity.MemoLangUserEntity
import org.springframework.data.repository.CrudRepository

interface MemoLangUserRepository : CrudRepository<MemoLangUserEntity, Long> {
    fun findByUsername(username: String): MemoLangUserEntity?
}
