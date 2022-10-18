package clpfd.global

import clpfd.BaseTest
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import org.junit.jupiter.api.Test

class GlobalCardinalityThreeTest: BaseTest() {

    @Test
    fun testGlobalCardinality() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y,Z) :- 
                ins([X,Y,Z], '..'(1, 3)), 
                in(W, '..'(2, 2)),
                in(V, '..'(1, 1)),
                global_cardinality([X,Y,Z],['-'(1,W), '-'(2,V)], [consistency(3)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z),label([X,Y,Z])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<ExistenceError>(solution, ExistenceError.ObjectType.RESOURCE)
    }
}