package clpfd

import clpqr.ClpQRLibrary
import it.unibo.tuprolog.core.TermConvertible
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.TrackVariables
import it.unibo.tuprolog.solve.flags.invoke
import it.unibo.tuprolog.solve.library.toRuntime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.ClausesParser
import kotlin.test.assertEquals
import kotlin.test.assertIs


abstract class BaseTest {

    protected val termParser = TermParser.withDefaultOperators()
    protected val theoryParser = ClausesParser.withDefaultOperators()

    // returns a solver for clpfd problems
    protected fun getSolver(theory: Theory = Theory.empty()): Solver =
        ClassicSolverFactory.solverOf(
            staticKb = theory,
            libraries =  ClpFdLibrary.toRuntime(),
            flags = FlagStore.EMPTY + TrackVariables { ON }
        )

    protected fun getFdQRSolver(theory: Theory = Theory.empty()): Solver =
        ClassicSolverFactory.solverOf(
            staticKb = theory,
            libraries =  ClpFdLibrary.toRuntime() + ClpQRLibrary.toRuntime(),
            flags = FlagStore.EMPTY + TrackVariables { ON }
        )

    protected inline fun <reified T : LogicError> assertException(sol: Solution, expected: TermConvertible) {
        val error = sol.exception
        assertIs<T>(error)
        val expectedEnum = error.errorStruct[0].castToStruct()[0]
        assertEquals(expected.toTerm(), expectedEnum)
    }

}