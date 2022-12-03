package app.memolang.memolangbackend.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class MemoLangUserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(unique = true)
    var username: String? = null,
    @Column(name = "password_hash")
    var passwordHash: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is MemoLangUserEntity) return false
        return other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
