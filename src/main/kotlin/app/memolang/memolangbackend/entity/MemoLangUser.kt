package app.memolang.memolangbackend.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class MemoLangUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var username: String? = null,
    var passwordHash: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is MemoLangUser) return false
        return other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
