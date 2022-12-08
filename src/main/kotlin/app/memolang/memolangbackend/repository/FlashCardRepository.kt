package app.memolang.memolangbackend.repository

import app.memolang.memolangbackend.entity.FlashCardEntity
import org.springframework.data.repository.CrudRepository

interface FlashCardRepository : CrudRepository<FlashCardEntity, Long>
