package app.memolang.memolangbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MemoLangBackendApplication

fun main(args: Array<String>) {
	runApplication<MemoLangBackendApplication>(*args)
}
