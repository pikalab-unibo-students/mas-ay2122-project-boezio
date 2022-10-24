package clpfd.utils

import clpfd.search.ProblemType
import it.unibo.tuprolog.core.Struct
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProblemTypeTest {

    @Test
    fun testProblemTypeNull(){
        assertNull(ProblemType.SATISFY.toChoco())
    }

    @Test
    fun testSatisfyFromStruct(){
        assertEquals(ProblemType.SATISFY, ProblemType.fromTerm(Struct.of("struct")))
    }
}