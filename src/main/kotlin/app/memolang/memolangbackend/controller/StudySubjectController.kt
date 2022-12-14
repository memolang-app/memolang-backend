package app.memolang.memolangbackend.controller

import app.memolang.memolangbackend.entity.FlashCardEntity
import app.memolang.memolangbackend.entity.StudySubjectEntity
import app.memolang.memolangbackend.repository.FlashCardRepository
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
import javax.transaction.Transactional

const val STUDY_SUBJECT_BASE_URL = "/api/study-subject"

@RestController
class StudySubjectController(
    private val studySubjectRepository: StudySubjectRepository,
    private val flashCardRepository: FlashCardRepository,
) {
    @PostMapping(STUDY_SUBJECT_BASE_URL)
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun create(principal: Principal, @RequestBody body: CardSetRequestBody): StudySubjectEntity {
        validateUserLimits(principal.name)
        return studySubjectRepository.save(
            StudySubjectEntity(
                name = body.name,
                ownerUsername = principal.name,
            )
        )
    }

    @DeleteMapping("$STUDY_SUBJECT_BASE_URL/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun delete(principal: Principal, @PathVariable id: Long) {
        val subject = studySubjectRepository.findById(id).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (subject.ownerUsername != principal.name) throw ResponseStatusException(HttpStatus.FORBIDDEN)
        studySubjectRepository.delete(subject)
    }

    @GetMapping(STUDY_SUBJECT_BASE_URL)
    @Transactional
    fun getUserSubjects(principal: Principal): List<StudySubjectEntity> =
        studySubjectRepository.findByOwnerUsername(principal.name)

    @PostMapping("$STUDY_SUBJECT_BASE_URL/{subjectId}/cards")
    @Transactional
    fun addCard(
        principal: Principal,
        @PathVariable subjectId: Long,
        @RequestBody flashCardEntity: FlashCardEntity,
    ): StudySubjectEntity {
        validateUserLimits(principal.name)
        val subject = findSubjectWithErrorHandling(principal, subjectId)
        if (flashCardEntity.id != null) throw ResponseStatusException(HttpStatus.CONFLICT)
        subject.flashCards.add(flashCardEntity)
        studySubjectRepository.save(subject)
        return subject
    }

    @PostMapping("$STUDY_SUBJECT_BASE_URL/{subjectId}/cards/{cardId}/reviews")
    @Transactional
    fun cardReviewed(
        principal: Principal,
        @PathVariable subjectId: Long,
        @PathVariable cardId: Long,
        @RequestBody cardReviewRequestBody: CardReviewRequestBody,
    ) {
        val subject = findSubjectWithErrorHandling(principal, subjectId)
        if (cardReviewRequestBody.known) subject.advanceCard(cardId)?.also { flashCardRepository.save(it) }
        else subject.backToStage1(cardId)?.also { flashCardRepository.save(it) }
        studySubjectRepository.save(subject)
    }

    fun findSubjectWithErrorHandling(principal: Principal, subjectId: Long): StudySubjectEntity {
        val subject = studySubjectRepository.findById(subjectId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (subject.ownerUsername != principal.name) throw ResponseStatusException(HttpStatus.FORBIDDEN)
        return subject
    }

    fun validateUserLimits(username: String) {
        val subjects = studySubjectRepository.findByOwnerUsername(username)
        if (subjects.size > 10) throw ResponseStatusException(HttpStatus.FORBIDDEN)
        if (subjects.flatMap { it.flashCards }.size > 100_000) throw ResponseStatusException(HttpStatus.FORBIDDEN)
    }
}

data class CardReviewRequestBody(val known: Boolean)
class CardSetRequestBody(val name: String)
