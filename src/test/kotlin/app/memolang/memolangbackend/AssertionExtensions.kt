package app.memolang.memolangbackend

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals

infix fun <T> T.shouldBe(expected: T) = assertEquals(expected, this)
infix fun <T> T.shouldNotBe(expected: T?) = assertNotEquals(expected, this)
infix fun <T> Collection<T>.shouldHaveSize(expectedSize: Int) = assertThat(this).hasSize(expectedSize)
