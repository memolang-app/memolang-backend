package app.memolang.memolangbackend.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/api/card-set")
class CardSetController {
    @PostMapping
    fun create() {}

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long) {}

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {}

    @GetMapping
    fun getAll() {}

    @PostMapping("/{setId}/cards")
    fun addCard(@PathVariable setId: Long) {}

    @DeleteMapping("/{setId}/cards/{cardId}")
    fun deleteCard(@PathVariable setId: Long, @PathVariable cardId: Long) {}

    @PostMapping("/{setId}/cards/{cardId}/reviews")
    fun cardReviewed(@PathVariable setId: Long, @PathVariable cardId: Long) {}
}
