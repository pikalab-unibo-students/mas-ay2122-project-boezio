package clpfd


import org.junit.jupiter.api.Test

class ZCompareTest: BaseTest() {

    @Test
    fun testGreater() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                ins([X,Y],'..'(1,10)),
                zcompare('>',X,Y).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2),
                varOf("Y") to intOf(1)
            )
        }
    }
}