package app.memolang.memolangbackend.repository

import app.memolang.memolangbackend.entity.StudySubjectEntity
import org.springframework.data.repository.CrudRepository

interface StudySubjectRepository : CrudRepository<StudySubjectEntity, Long> {
    fun findByOwnerUsername(username: String): List<StudySubjectEntity>
}
