package app.memolang.memolangbackend.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "flash_cards")
class FlashCardEntity(
    @Id
    @GeneratedValue
    var id: Long? = null,
    @Column(nullable = false)
    var question: String? = null,
    @Column(nullable = false)
    var answer: String? = null,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var stage: Stage? = Stage.EVERY_DAY,
)

enum class Stage {
    EVERY_DAY,
    EVERY_TWO_DAY,
    EVERY_WEEK,
    EVERY_TWO_WEEK,
    EVERY_MONTH,
    DONE;

    fun next() = when (this) {
        EVERY_DAY -> EVERY_TWO_DAY
        EVERY_TWO_DAY -> EVERY_WEEK
        EVERY_WEEK -> EVERY_TWO_WEEK
        EVERY_TWO_WEEK -> EVERY_MONTH
        EVERY_MONTH -> DONE
        DONE -> DONE
    }
}
