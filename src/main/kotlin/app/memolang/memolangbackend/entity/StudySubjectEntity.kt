package app.memolang.memolangbackend.entity

import org.hibernate.annotations.CreationTimestamp
import java.time.ZonedDateTime
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class StudySubjectEntity(
    @Id
    @GeneratedValue
    var id: Long? = null,
    var name: String? = null,
    var ownerUsername: String? = null,
    @CreationTimestamp
    var createdAt: ZonedDateTime? = null,
    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER,
    )
    var flashCards: MutableList<FlashCardEntity> = mutableListOf(),
) {
    fun advanceCard(cardId: Long): FlashCardEntity? {
        return flashCards.find { it.id == cardId }?.also { it.advance() }
    }

    fun backToStage1(cardId: Long): FlashCardEntity? {
        return flashCards.find { it.id == cardId }?.also { it.stage = Stage.EVERY_DAY }
    }
}
