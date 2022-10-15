package clpfd

import org.junit.jupiter.api.Test

class ReificationOpTest: BaseTest() {

    @Test
    fun testNot() {

        val goal = termParser.parseStruct(
            "in(X, '..'(1,10)),  #\\(#>(X,3)) , label([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1)
            )
        }
    }

    @Test
    fun testOr() {

        val goal = termParser.parseStruct(
            "in(X, '..'(1,10)),  #\\/(#<(X,3), #>(X,5)) , label([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1)
            )
        }
    }

    @Test
    fun testAnd() {

        val goal = termParser.parseStruct(
            "in(X, '..'(1,10)),  #/\\(#<(X,6), #>(X,2)) , label([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(3)
            )
        }
    }

    @Test
    fun testXor() {

        val goal = termParser.parseStruct(
            "in(X, '..'(1,10)),  #\\(#<(X,6), #>(X,2)) , label([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1)
            )
        }
    }

    @Test
    fun testIff() {

        val goal = termParser.parseStruct(
            "in(X, '..'(1,10)),  #<==>(#<(X,6), #>(X,2)) , label([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(3)
            )
        }
    }

    @Test
    fun testImplies() {

        val goal = termParser.parseStruct(
            "in(X, '..'(1,10)),  #==>(#<(X,6), #>(X,2)) , label([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(3)
            )
        }
    }

    @Test
    fun testInverseImplies() {

        val goal = termParser.parseStruct(
            "in(X, '..'(1,10)),  #<==(#<(X,6), #>(X,2)) , label([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1)
            )
        }
    }

    @Test
    fun testNestedOperators() {

        val goal = termParser.parseStruct(
            "in(X, '..'(1,10)),  #\\/(#=(X,1),#/\\(#<(X,6), #>(X,2))) , label([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1)
            )
        }
    }

    @Test
    fun testNestedOperatorsWithNotEqual() {

        val goal = termParser.parseStruct(
            "in(X, '..'(1,10)),  #/\\(#\\=(X,1),#\\=(X,3)) , label([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2)
            )
        }
    }

    @Test
    fun testNestedOperatorsWithGreaterEquals() {

        val goal = termParser.parseStruct(
            "in(X, '..'(1,10)),  #/\\(#\\=(X,1),#>=(X,3)) , label([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(3)
            )
        }
    }

    @Test
    fun testNestedOperatorsWithLessEquals() {

        val goal = termParser.parseStruct(
            "in(X, '..'(1,10)),  #/\\(#\\=(X,1),#=<(X,3)) , label([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2)
            )
        }
    }
}