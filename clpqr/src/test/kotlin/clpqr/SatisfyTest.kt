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

        val solver = getSolver(theory)

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

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                 precision,
                varOf("X") to realOf("3.4375"),
                varOf("Y") to realOf("6.630625000000003")
            )
        }
    }

    @Test
    fun testSatisfyWithLessThanConstraint(){

        val goal = termParser.parseStruct(
            "{ X + Y = 10.0, X < 5.0 }, satisfy([X, Y])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("X") to realOf("1.630625"),
                varOf("Y") to realOf("8.4375")
            )
        }
    }

    @Test
    fun testSatisfyWithLessEqualsConstraint(){

        val goal = termParser.parseStruct(
            "{ X + Y = 10.0, '<='(X,5.0) }, satisfy([X, Y])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("X") to realOf("1.630625"),
                varOf("Y") to realOf("8.4375")
            )
        }
    }

    @Test
    fun testSatisfyWithMinusExpression(){

        val goal = termParser.parseStruct(
            "{ X - Y = 10.0 }, satisfy([X, Y])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("X") to realOf("10.125"),
                varOf("Y") to realOf("0.125")
            )
        }
    }

    @Test
    fun testSatisfyWithDivExpression(){

        val goal = termParser.parseStruct(
            "{ X / Y = 25.0, X > 1.0, Y > 1.0 }, satisfy([X, Y])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("X") to realOf("25.125"),
                varOf("Y") to realOf("1.005")
            )
        }
    }

    @Test
    fun testSatisfyWithMinExpression(){

        val goal = termParser.parseStruct(
            "{ min(X,Y) > 10.0, Y > 10.0 }, satisfy([X, Y])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("X") to realOf("10.06249"),
                varOf("Y") to realOf("10.1249")
            )
        }
    }

    @Test
    fun testSatisfyWithMaxExpression(){

        val goal = termParser.parseStruct(
            "{ max(X,Y) > 10.0, Y > 10.0 }, satisfy([X, Y])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("X") to realOf("0.062499"),
                varOf("Y") to realOf("10.1249")
            )
        }
    }

    @Test
    fun testSatisfyWithAbsExpression(){

        val goal = termParser.parseStruct(
            "{ abs(X) > 10.0 }, satisfy([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("X") to realOf("10.1182")
            )
        }
    }

    @Test
    fun testSatisfyWithNegativeExpression(){

        val goal = termParser.parseStruct(
            "{ '-'(abs(X)) < 10.0 }, satisfy([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("X") to realOf("0.12499")
            )
        }
    }

    @Test
    fun testSatisfyWithSinExpression(){

        val goal = termParser.parseStruct(
            "{ sin(X) > 0.0 }, satisfy([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("X") to realOf("0.12499")
            )
        }
    }

    @Test
    fun testSatisfyWithCosExpression(){

        val goal = termParser.parseStruct(
            "{ cos(X) > 0.0 }, satisfy([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("X") to realOf("0.12499")
            )
        }
    }
}