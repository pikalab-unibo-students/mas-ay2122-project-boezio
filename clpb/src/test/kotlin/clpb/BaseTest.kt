package clpb

import it.unibo.tuprolog.core.TermConvertible
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.library.toRuntime
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertEquals
import kotlin.test.assertIs

abstract class BaseTest {

    protected val termParser = TermParser.withDefaultOperators()

    protected fun getSolver(theory: Theory = Theory.empty()): Solver =
        Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime(),
            staticKb = theory
        )

    protected inline fun <reified T : LogicError> assertException(sol: Solution, expected: TermConvertible) {
        val error = sol.exception
        assertIs<T>(error)
        val expectedEnum = error.errorStruct[0].castToStruct()[0]
        assertEquals(expected.toTerm(), expectedEnum)
    }

}