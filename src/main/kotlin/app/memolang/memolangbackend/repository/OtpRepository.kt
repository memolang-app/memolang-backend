package app.memolang.memolangbackend.repository

import app.memolang.memolangbackend.entity.OtpEntity
import org.springframework.data.repository.CrudRepository

interface OtpRepository : CrudRepository<OtpEntity, Long> {
    fun findByClaimedEmail(email: String): OtpEntity?
}
