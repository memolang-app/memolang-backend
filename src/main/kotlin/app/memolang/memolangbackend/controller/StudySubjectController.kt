package app.memolang.memolangbackend.controller

import app.memolang.memolangbackend.entity.FlashCardEntity
import app.memolang.memolangbackend.entity.StudySubjectEntity
import app.memolang.memolangbackend.repository.StudySubjectRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.security.Principal

const val STUDY_SUBJECT_BASE_URL = "/api/study-subject"

@RestController
class StudySubjectController(
    private val studySubjectRepository: StudySubjectRepository,
) {
    @PostMapping(STUDY_SUBJECT_BASE_URL)
    @ResponseStatus(HttpStatus.CREATED)
    fun create(principal: Principal, @RequestBody body: CardSetRequestBody): StudySubjectEntity =
        studySubjectRepository.save(
            StudySubjectEntity(
                name = body.name,
                ownerUsername = principal.name,
            )
        )

    @DeleteMapping("$STUDY_SUBJECT_BASE_URL/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(principal: Principal, @PathVariable id: Long) {
        val subject = studySubjectRepository.findById(id).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (subject.ownerUsername != principal.name) throw ResponseStatusException(HttpStatus.FORBIDDEN)
        studySubjectRepository.delete(subject)
    }

    @GetMapping(STUDY_SUBJECT_BASE_URL)
    fun getUserSubjects(principal: Principal): List<StudySubjectEntity> =
        studySubjectRepository.findByOwnerUsername(principal.name)

    @PostMapping("$STUDY_SUBJECT_BASE_URL/{subjectId}/cards")
    fun addCard(
        principal: Principal,
        @PathVariable subjectId: Long,
        @RequestBody flashCardEntity: FlashCardEntity,
    ): StudySubjectEntity {
        val subject = studySubjectRepository.findById(subjectId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (subject.ownerUsername != principal.name) throw ResponseStatusException(HttpStatus.FORBIDDEN)
        if (flashCardEntity.id != null) throw ResponseStatusException(HttpStatus.CONFLICT)
        subject.flashCards.add(flashCardEntity)
        studySubjectRepository.save(subject)
        return subject
    }

    @DeleteMapping("$STUDY_SUBJECT_BASE_URL/{subjectId}/cards/{cardId}")
    fun deleteCard(@PathVariable subjectId: Long, @PathVariable cardId: Long) {
    }

    @PostMapping("$STUDY_SUBJECT_BASE_URL/{subjectId}/cards/{cardId}/reviews")
    fun cardReviewed(@PathVariable subjectId: Long, @PathVariable cardId: Long) {
    }
}

class CardSetRequestBody(val name: String)
