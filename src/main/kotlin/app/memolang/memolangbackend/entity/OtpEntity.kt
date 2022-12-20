package app.memolang.memolangbackend.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(
    indexes = [
        Index(columnList = "claimed_email"),
    ]
)
class OtpEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "claimed_email", nullable = false, unique = true)
    var claimedEmail: String? = null,
    @Column(nullable = false)
    var code: String? = null,
    var used: Boolean = false,
)
