package app.memolang.memolangbackend

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals

infix fun <T> T.shouldBe(expected: T) = assertEquals(expected, this)
infix fun <T> Collection<T>.shouldHaveSize(expectedSize: Int) = assertThat(this).hasSize(expectedSize)
