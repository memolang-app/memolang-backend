package app.memolang.memolangbackend.entity

import org.hibernate.annotations.CreationTimestamp
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class StudySubjectEntity(
    @Id
    @GeneratedValue
    var id: Long? = null,
    var name: String? = null,
    var ownerUsername: String? = null,
    @CreationTimestamp
    var createdAt: ZonedDateTime? = null,
)
