package app.memolang.memolangbackend.entity

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime
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
    var stage: Stage = Stage.EVERY_DAY,
    var nextStudyAt: ZonedDateTime = ZonedDateTime.now(),
) {
    @JsonProperty
    fun shouldBeStudied() = stage != Stage.DONE && !nextStudyAt.isAfter(ZonedDateTime.now())

    fun advance() {
        stage = stage.next()
        nextStudyAt = nextStudyAt.plusDays(stage.daysToAdvance.toLong())
    }
}

enum class Stage(var daysToAdvance: Int) {
    EVERY_DAY(2),
    EVERY_TWO_DAY(7),
    EVERY_WEEK(14),
    EVERY_TWO_WEEK(30),
    EVERY_MONTH(0),
    DONE(0);

    fun next() = when (this) {
        EVERY_DAY -> EVERY_TWO_DAY
        EVERY_TWO_DAY -> EVERY_WEEK
        EVERY_WEEK -> EVERY_TWO_WEEK
        EVERY_TWO_WEEK -> EVERY_MONTH
        EVERY_MONTH -> DONE
        DONE -> DONE
    }
}
