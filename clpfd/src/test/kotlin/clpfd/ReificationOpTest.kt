package clpfd

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.toRuntime
import org.junit.jupiter.api.Test

class ReificationOpTest: BaseTest() {

    @Test
    fun testNot() {

        val goal = termParser.parseStruct(
            "in(X, '..'(1,10)),  #\\(#>(X,3)) , label([X])"
        )

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

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

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

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

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

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

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

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

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

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

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

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

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

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

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1)
            )
        }
    }
}