package clpqr.utils

import clpqr.search.ProblemType
import org.junit.jupiter.api.Test
import kotlin.test.assertNull

class ProblemTypeTest {

    @Test
    fun testSatisfy(){
        assertNull(ProblemType.SATISFY.toChoco())
    }
}