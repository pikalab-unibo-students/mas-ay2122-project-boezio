package clpVarCreation

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.junit.jupiter.api.Assertions.assertTrue
import java.lang.IllegalArgumentException

abstract class BaseTest {

    protected val termParser = TermParser.withDefaultOperators()
    protected val theoryParser = ClausesParser.withDefaultOperators()

    fun assertException(solver: Solver, goal: Struct){

        val solution = try{
            solver.solveOnce(goal)
        }catch (e: IllegalArgumentException){
            true
        }

        assertTrue(solution as Boolean)
    }

}