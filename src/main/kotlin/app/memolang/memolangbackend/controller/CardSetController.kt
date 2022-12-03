package app.memolang.memolangbackend.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

const val CARD_SET_BASE_URL = "/api/card-set"

@RestController
class CardSetController {
    @PostMapping(CARD_SET_BASE_URL)
    fun create() {}

    @PutMapping("$CARD_SET_BASE_URL/{id}")
    fun update(@PathVariable id: Long) {}

    @DeleteMapping("$CARD_SET_BASE_URL/{id}")
    fun delete(@PathVariable id: Long) {}

    @GetMapping(CARD_SET_BASE_URL)
    fun getAll() {}

    @PostMapping("$CARD_SET_BASE_URL/{setId}/cards")
    fun addCard(@PathVariable setId: Long) {}

    @DeleteMapping("$CARD_SET_BASE_URL/{setId}/cards/{cardId}")
    fun deleteCard(@PathVariable setId: Long, @PathVariable cardId: Long) {}

    @PostMapping("$CARD_SET_BASE_URL/{setId}/cards/{cardId}/reviews")
    fun cardReviewed(@PathVariable setId: Long, @PathVariable cardId: Long) {}
}
