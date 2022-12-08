package app.memolang.memolangbackend.controller

import app.memolang.memolangbackend.BaseIntegrationTest
import app.memolang.memolangbackend.Token
import app.memolang.memolangbackend.entity.FlashCardEntity
import app.memolang.memolangbackend.entity.StudySubjectEntity
import app.memolang.memolangbackend.repository.StudySubjectRepository
import app.memolang.memolangbackend.shouldBe
import app.memolang.memolangbackend.shouldHaveSize
import app.memolang.memolangbackend.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class StudySubjectControllerTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var subjectRepository: StudySubjectRepository

    @Test
    fun `Create, retrieve and delete subject`() {
        val token = successfullyCreateUser()
        val postResponse = restTemplate.postForEntity(
            STUDY_SUBJECT_BASE_URL,
            request(token, mapOf("name" to "Spanish")),
            StudySubjectEntity::class.java
        )
        postResponse.statusCode shouldBe HttpStatus.CREATED
        postResponse.body!!.createdAt shouldNotBe null
        val subjectsAfterPost = successfullyListUserSubjects(token)
        subjectsAfterPost shouldHaveSize 1
        restTemplate.exchange(
            "$STUDY_SUBJECT_BASE_URL/${subjectsAfterPost.first()["id"]}",
            HttpMethod.DELETE,
            request(token),
            Any::class.java
        ).statusCode shouldBe HttpStatus.NO_CONTENT
        successfullyListUserSubjects(token) shouldHaveSize 0
    }

    @Test
    fun `Add a card to a subject then review the card`() {
        val token = successfullyCreateUser()
        val createSubjectResponse = restTemplate.postForEntity(
            STUDY_SUBJECT_BASE_URL,
            request(token, mapOf("name" to "Spanish")),
            StudySubjectEntity::class.java
        )
        createSubjectResponse.statusCode shouldBe HttpStatus.CREATED
        createSubjectResponse.body!!.createdAt shouldNotBe null
        val subjectsAfterPost = successfullyListUserSubjects(token)
        subjectsAfterPost shouldHaveSize 1
        restTemplate.postForEntity(
            "$STUDY_SUBJECT_BASE_URL/${subjectsAfterPost.first()["id"]}/cards",
            request(token, mapOf("question" to "hablar", "answer" to "to speak")),
            Any::class.java
        ).statusCode shouldBe HttpStatus.OK
        val subjectsAfterAddingCard = successfullyListUserSubjects(token)
        (subjectsAfterAddingCard.first()["flashCards"] as Collection<*>).shouldHaveSize(1)
        val cardBeforeReview = loadFirstCard((subjectsAfterAddingCard.first()["id"] as Int).toLong())
        cardBeforeReview.shouldBeStudied() shouldBe true
        restTemplate.postForEntity(
            "$STUDY_SUBJECT_BASE_URL/${subjectsAfterAddingCard.first()["id"]}/cards/${cardBeforeReview.id}/reviews",
            request(token, mapOf("known" to true)),
            Any::class.java
        ).statusCode shouldBe HttpStatus.OK
        val cardAfterReview = loadFirstCard((subjectsAfterAddingCard.first()["id"] as Int).toLong())
        cardAfterReview.shouldBeStudied() shouldBe false
    }

    protected fun loadFirstCard(subjectId: Long): FlashCardEntity {
        return subjectRepository
            .findById(subjectId)
            .get()
            .flashCards
            .first()
    }

    @Test
    fun `Deleting someone else's subject fails`() {
        val ownerToken = successfullyCreateUser(username = "user1")
        val otherToken = successfullyCreateUser(username = "user2")
        val createSubjectResponse = restTemplate.postForEntity(
            STUDY_SUBJECT_BASE_URL,
            request(ownerToken, mapOf("name" to "Spanish")),
            StudySubjectEntity::class.java
        )
        createSubjectResponse.statusCode shouldBe HttpStatus.CREATED
        restTemplate.exchange(
            "$STUDY_SUBJECT_BASE_URL/${createSubjectResponse.body!!.id}",
            HttpMethod.DELETE,
            request(otherToken),
            Any::class.java
        ).statusCode shouldBe HttpStatus.FORBIDDEN
        restTemplate.exchange(
            "$STUDY_SUBJECT_BASE_URL/${createSubjectResponse.body!!.id}",
            HttpMethod.DELETE,
            request(ownerToken),
            Any::class.java
        ).statusCode shouldBe HttpStatus.NO_CONTENT
    }

    private fun successfullyListUserSubjects(token: Token): List<Map<*, *>> {
        val response = restTemplate.exchange(
            STUDY_SUBJECT_BASE_URL,
            HttpMethod.GET,
            request(token),
            List::class.java,
        )
        response.statusCode shouldBe HttpStatus.OK
        return (response.body!! as List<Map<*, *>>)
    }
}
