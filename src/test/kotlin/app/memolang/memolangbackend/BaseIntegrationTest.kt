package app.memolang.memolangbackend

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository

abstract class BaseIntegrationTest {
    @Autowired
    private lateinit var repositories: List<CrudRepository<*, *>>

    @BeforeEach
    fun clearDb() {
        repositories.forEach { it.deleteAll() }
    }
}
