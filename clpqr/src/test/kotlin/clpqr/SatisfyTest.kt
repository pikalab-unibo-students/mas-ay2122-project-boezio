package clpqr


import org.junit.jupiter.api.Test


class SatisfyTest: BaseTest() {

    @Test
    fun testSatisfyTuple(){

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- { X >= Y, X + Y = 10.0 }.
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X, Y),satisfy([X, Y])"
        )

        val solver = get_solver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("X") to realOf("8.362656249999997"),
                varOf("Y") to realOf("1.7153125")
            )
        }
    }

    @Test
    fun testSatisfyStruct(){

        val goal = termParser.parseStruct(
            "{ X + Y = 10.0 }, satisfy([X, Y])",
        )

        val solver = get_solver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                 precision,
                varOf("X") to realOf("3.4375"),
                varOf("Y") to realOf("6.630625000000003")
            )
        }

    }
}