package clpqr.utils

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EquationDetectorTest {

    private val detector = EquationDetector(Substitution.empty(), mutableMapOf())

    @Test
    fun testTermIsInteger(){

        val num = Integer.of(1)
        assertEquals(num.accept(detector), mutableMapOf())
    }
}