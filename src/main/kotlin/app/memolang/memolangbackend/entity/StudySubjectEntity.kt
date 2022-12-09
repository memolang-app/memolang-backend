package app.memolang.memolangbackend.entity

import org.hibernate.annotations.CreationTimestamp
import java.time.ZonedDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(
    indexes = [
        Index(columnList = "owner_username"),
    ]
)
class StudySubjectEntity(
    @Id
    @GeneratedValue
    var id: Long? = null,
    var name: String? = null,
    @Column(name = "owner_username")
    var ownerUsername: String? = null,
    @CreationTimestamp
    @Column(name = "created_at")
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
